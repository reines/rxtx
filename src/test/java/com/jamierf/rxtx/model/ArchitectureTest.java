package com.jamierf.rxtx.model;

import com.jamierf.rxtx.error.UnsupportedArchitectureException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArchitectureTest {

    @Test(expected = UnsupportedArchitectureException.class)
    public void testUnsupported() {
        Architecture.fromString("unsupported");
    }

    @Test(expected = UnsupportedArchitectureException.class)
    public void testEmpty() {
        Architecture.fromString("");
    }

    @Test(expected = UnsupportedArchitectureException.class)
    public void testNull() {
        Architecture.fromString(null);
    }

    @Test
    public void testAmd64() {
        assertEquals(Architecture.X86_64, Architecture.fromString("amd64"));
    }

    @Test
    public void testX8664() {
        assertEquals(Architecture.X86_64, Architecture.fromString("x86_64"));
    }
}
