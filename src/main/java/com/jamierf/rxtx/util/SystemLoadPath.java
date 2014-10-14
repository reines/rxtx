package com.jamierf.rxtx.util;

import java.lang.reflect.Field;
import java.util.*;

public class SystemLoadPath {

    private static SystemLoadPath instance;

    public static SystemLoadPath getInstance() throws NoSuchFieldException {
        if (instance == null) {
            instance = new SystemLoadPath(ClassLoader.class, "usr_paths");
        }

        return instance;
    }

    private final Field field;

    protected SystemLoadPath(final Class<?> clazz, final String name) throws NoSuchFieldException {
        field = clazz.getDeclaredField(name);
        field.setAccessible(true);
    }

    // See: http://forums.sun.com/thread.jspa?threadID=707176
    public void add(final String path) {
        final Set<String> paths = new LinkedHashSet<String>();
        paths.addAll(get());
        paths.add(path);
        set(paths);
    }

    public void set(final Collection<String> paths) {
        try {
            field.set(null, paths.toArray(new String[paths.size()]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> get() {
        try {
            return Arrays.asList((String[]) field.get(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
