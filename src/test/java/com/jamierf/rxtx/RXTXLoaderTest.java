package com.jamierf.rxtx;

import com.jamierf.rxtx.model.Architecture;
import com.jamierf.rxtx.model.OperatingSystem;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class RXTXLoaderTest {

    private void testResourceExists(final OperatingSystem os, final Architecture arch) throws IOException {
        final InputStream in = RXTXLoader.openResource(os, arch);
        try {
            assertNotNull(in);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Test
    public void testCreateTempDirectory() {
        final File tempDir = RXTXLoader.createTempDirectory();

        try {
            assertTrue(tempDir.isDirectory());
        }
        finally {
            tempDir.delete();
        }
    }

    @Test
    public void testLinuxResources() throws IOException {
        testResourceExists(OperatingSystem.LINUX, Architecture.X86);
        testResourceExists(OperatingSystem.LINUX, Architecture.X86_64);
        testResourceExists(OperatingSystem.LINUX, Architecture.ARM);
        testResourceExists(OperatingSystem.LINUX, Architecture.ARMv5);
        testResourceExists(OperatingSystem.LINUX, Architecture.ARMv6);
        testResourceExists(OperatingSystem.LINUX, Architecture.ARMv7);
    }

    @Test
    public void testWindowsResources() throws IOException {
        testResourceExists(OperatingSystem.WINDOWS, Architecture.X86);
        testResourceExists(OperatingSystem.WINDOWS, Architecture.X86_64);
    }

    @Test
    public void testMacResources() throws IOException {
        testResourceExists(OperatingSystem.MAC_OSX, Architecture.X86);
        testResourceExists(OperatingSystem.MAC_OSX, Architecture.X86_64);
    }
}
