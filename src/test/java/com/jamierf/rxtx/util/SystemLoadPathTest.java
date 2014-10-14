package com.jamierf.rxtx.util;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemLoadPathTest {

    private static class NoFieldClass {}

    private static class WrongFieldypeClass {
        @SuppressWarnings("unused")
        private static final int usr_paths = 0;
    }

    @Test
    public void testSet() throws NoSuchFieldException {
        final SystemLoadPath loadPath = SystemLoadPath.getInstance();
        final Collection<String> paths = ImmutableList.of("/tmp/testSet");
        loadPath.set(paths);
        assertEquals(paths, loadPath.get());
    }

    @Test
    public void testAdd() throws NoSuchFieldException {
        final SystemLoadPath loadPath = SystemLoadPath.getInstance();
        loadPath.add("/tmp/testAdd");
        assertTrue(loadPath.get().contains("/tmp/testAdd"));
    }

    @Test(expected = NoSuchFieldException.class)
    public void testNoSuchField() throws NoSuchFieldException {
        new SystemLoadPath(NoFieldClass.class, "usr_paths");
    }

    @Test(expected = RuntimeException.class)
    public void testGetWrongFieldType() throws NoSuchFieldException {
        new SystemLoadPath(WrongFieldypeClass.class, "usr_paths").get();
    }

    @Test(expected = RuntimeException.class)
    public void testSetWrongFieldType() throws NoSuchFieldException {
        new SystemLoadPath(WrongFieldypeClass.class, "usr_paths").set(ImmutableList.of("/tmp/testSetWrongFieldType"));
    }
}
