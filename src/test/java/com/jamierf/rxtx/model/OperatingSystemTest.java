package com.jamierf.rxtx.model;

import com.jamierf.rxtx.error.UnsupportedOperatingSystemException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OperatingSystemTest {

    private static void mockOperatingSystem(final String name) {
        System.setProperty(OperatingSystem.OS_NAME_SYSTEM_PROPERTY, name);
    }

    @Test(expected = UnsupportedOperatingSystemException.class)
    public void testEmpty() {
        mockOperatingSystem("");
        OperatingSystem.get();
    }

    @Test(expected = UnsupportedOperatingSystemException.class)
    public void testNull() {
        OperatingSystem.fromString(null);
    }

    @Test
    public void testExceptionCapturesOperatingSystem() {
        try {
            OperatingSystem.fromString("turtles");
            fail("Expected UnsupportedOperatingSystemException");
        }
        catch (UnsupportedOperatingSystemException e) {
            assertEquals("turtles", e.getOperatingSystem());
        }
    }

    @Test
    public void testExactMatchLinux() {
        assertEquals(OperatingSystem.LINUX, OperatingSystem.fromString("Linux"));
    }

    @Test
    public void testLinux() {
        mockOperatingSystem("Linux 3.12.28+ #709 PREEMPT");
        assertEquals(OperatingSystem.LINUX, OperatingSystem.get());
    }

    @Test
    public void testExactMatchWindows() {
        mockOperatingSystem("Windows");
        assertEquals(OperatingSystem.WINDOWS, OperatingSystem.get());
    }

    @Test
    public void testWindows() {
        mockOperatingSystem("Windows 7");
        assertEquals(OperatingSystem.WINDOWS, OperatingSystem.get());
    }

    @Test
    public void testExactMatchMac() {
        mockOperatingSystem("Mac OS X");
        assertEquals(OperatingSystem.MAC_OSX, OperatingSystem.get());
    }

    @Test
    public void testMac() {
        mockOperatingSystem("Mac OS X 10.7.4");
        assertEquals(OperatingSystem.MAC_OSX, OperatingSystem.get());
    }

    @Test
    public void testNameIsLowerCase() {
        assertEquals("windows", OperatingSystem.WINDOWS.getName());
    }

    @Test
    public void testWindowsLibExtension() {
        assertTrue(OperatingSystem.WINDOWS.getLibPath().endsWith(".dll"));
    }

    @Test
    public void testMacLibExtension() {
        assertTrue(OperatingSystem.MAC_OSX.getLibPath().endsWith(".jnilib"));
    }

    @Test
    public void testLinuxLibExtension() {
        assertTrue(OperatingSystem.LINUX.getLibPath().endsWith(".so"));
    }
}
