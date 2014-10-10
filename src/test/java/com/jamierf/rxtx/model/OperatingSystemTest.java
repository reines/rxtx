package com.jamierf.rxtx.model;

import com.jamierf.rxtx.error.UnsupportedOperatingSystemException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperatingSystemTest {

    @Test(expected = UnsupportedOperatingSystemException.class)
    public void testUnsupported() {
        OperatingSystem.fromString("unsupported");
    }

    @Test(expected = UnsupportedOperatingSystemException.class)
    public void testEmpty() {
        OperatingSystem.fromString("");
    }

    @Test(expected = UnsupportedOperatingSystemException.class)
    public void testNull() {
        OperatingSystem.fromString(null);
    }

    @Test
    public void testExactMatchLinux() {
        assertEquals(OperatingSystem.LINUX, OperatingSystem.fromString("Linux"));
    }

    @Test
    public void testLinux() {
        assertEquals(OperatingSystem.LINUX, OperatingSystem.fromString("Linux 3.12.28+ #709 PREEMPT"));
    }

    @Test
    public void testExactMatchWindows() {
        assertEquals(OperatingSystem.WINDOWS, OperatingSystem.fromString("Windows"));
    }

    @Test
    public void testWindows() {
        assertEquals(OperatingSystem.WINDOWS, OperatingSystem.fromString("Windows 7"));
    }

    @Test
    public void testExactMatchMac() {
        assertEquals(OperatingSystem.MAC_OSX, OperatingSystem.fromString("Mac OS X"));
    }

    @Test
    public void testMac() {
        assertEquals(OperatingSystem.MAC_OSX, OperatingSystem.fromString("Mac OS X 10.7.4"));
    }
}
