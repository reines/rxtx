package com.jamierf.rxtx.model;

import com.google.common.io.Resources;
import com.jamierf.rxtx.error.UnsupportedArchitectureException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ArchitectureTest {

    private static void mockArchitecture(final String architecture) {
        System.setProperty(Architecture.OS_ARCHITECTURE_SYSTEM_PROPERTY, architecture);
    }


    @Test(expected = UnsupportedArchitectureException.class)
    public void testEmpty() {
        mockArchitecture("");
        Architecture.get();
    }

    @Test(expected = UnsupportedArchitectureException.class)
    public void testNull() {
        Architecture.fromString(null);
    }

    @Test
    public void testExceptionCapturesArchitecture() {
        try {
            mockArchitecture("turtles");
            Architecture.get();
            fail("Expected UnsupportedArchitectureException");
        }
        catch (UnsupportedArchitectureException e) {
            assertEquals("turtles", e.getArchitecture());
        }
    }

    @Test
    public void testAmd64() {
        mockArchitecture("amd64");
        assertEquals(Architecture.X86_64, Architecture.get());
    }

    @Test
    public void testX8664() {
        mockArchitecture("x86_64");
        assertEquals(Architecture.X86_64, Architecture.get());
    }

    @Test
    public void testNameIsLowerCase() {
        assertEquals("arm", Architecture.ARM.getName());
    }

    @Test
    public void testGetFromCpuInfoFile() throws URISyntaxException {
        final File file = new File(ArchitectureTest.class.getResource("armv6l.txt").toURI());
        assertEquals(Architecture.ARMv6, Architecture.getFromCpuInfo(file));
    }

    @Test
    public void testGetFromCpuInfoFileWhenNoSuchFile() {
        assertNull(Architecture.getFromCpuInfo(new File("no_such_file")));
    }

    @Test
    public void testGetFromCpuInfoFileWhenDirectory() {
        assertNull(Architecture.getFromCpuInfo(FileUtils.getTempDirectory()));
    }

    @Test
    public void testGetFromCpuInfoString() throws IOException {
        final String info = Resources.toString(ArchitectureTest.class.getResource("armv6l.txt"), StandardCharsets.UTF_8);
        assertEquals(Architecture.ARMv6, Architecture.getFromCpuInfo(info));
    }

    @Test
    public void testGetFromCpuInfoEmptyString() {
        assertNull(Architecture.getFromCpuInfo(""));
    }
}
