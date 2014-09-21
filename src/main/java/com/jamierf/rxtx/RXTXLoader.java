package com.jamierf.rxtx;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RXTXLoader {

    private static final Logger LOG = LoggerFactory.getLogger(RXTXLoader.class);
    private static final Pattern CPU_INFO_MODEL_INFO = Pattern.compile("model name\\s+:\\s+(.*)");
    private static final File CPU_INFO_FILE = new File("/proc/cpuinfo");

    public static enum OperatingSystem {
        WINDOWS("Windows", "rxtxSerial.dll"),
        LINUX("Linux", "librxtxSerial.so"),
        MAC_OSX("Mac OS X", "librxtxSerial.jnilib");

        public static OperatingSystem get() {
            final String name = System.getProperty("os.name");
            return fromString(name);
        }

        public static OperatingSystem fromString(final String name) {
            for (final OperatingSystem os : OperatingSystem.values()) {
                if (name.toLowerCase().contains(os.key.toLowerCase())) {
                    LOG.debug("Detected OS as {} ({})", os, name);
                    return os;
                }
            }

            throw new UnsupportedOperatingSystemException(name);
        }

        private final String key;
        private final String libPath;

        private OperatingSystem(final String key, final String libPath) {
            this.key = key;
            this.libPath = libPath;
        }

        public String getName() {
            return name().toLowerCase();
        }

        public String getLibPath() {
            return libPath;
        }
    }

    public static enum Architecture {
        X86_64("amd64", "x86_64"),
        X86("i386", "x86"),
        ARMv5("ARMv5"), ARMv6("ARMv6"), ARMv7("ARMv7"), ARM("arm");

        public static Architecture get() {
            try {
                // Attempt to read from /proc/cpuinfo if it exists
                if (CPU_INFO_FILE.exists()) {
                    final String cpuInfo = FileUtils.readFileToString(CPU_INFO_FILE);
                    final Matcher matcher = CPU_INFO_MODEL_INFO.matcher(cpuInfo);
                    if (matcher.find()) {
                        final String modelInfo = matcher.group(1).trim();
                        return fromString(modelInfo);
                    }
                }
            }
            catch (IOException e) {
                LOG.warn("Failed to read {}", CPU_INFO_FILE);
            }

            final String name = System.getProperty("os.arch");
            return fromString(name);
        }

        public static Architecture fromString(final String name) {
            for (final Architecture arch : Architecture.values()) {
                for (final String key : arch.keys) {
                    if (name.toLowerCase().contains(key.toLowerCase())) {
                        LOG.debug("Detected OS as {} ({})", arch, name);
                        return arch;
                    }
                }
            }

            throw new UnsupportedArchitectureException(name);
        }

        private final String[] keys;

        private Architecture(final String... keys) {
            this.keys = keys;
        }

        public String getName() {
            return name().toLowerCase();
        }
    }

    public static void load() throws IOException {
        RXTXLoader.load(OperatingSystem.get(), Architecture.get());
    }

    public static void load(final OperatingSystem os, final Architecture arch) throws IOException {
        final File tempDir = RXTXLoader.createTempDirectory();
        final InputStream source = openResource(os, arch);
        if (source == null) {
            throw new IllegalStateException(String.format("Unable to find resource for %s %s", arch, os));
        }

        final File target = new File(tempDir, os.getLibPath());
        LOG.debug("Loading RXTX library for {} {}", arch, os);

        try {
            FileUtils.copyInputStreamToFile(source, target);
            RXTXLoader.addDirToLoadPath(tempDir);
        } finally {
            source.close();
        }

        LOG.info("Loaded RXTX native library for {} {}", arch, os);
    }

    private static InputStream openResource(final OperatingSystem os, final Architecture arch) throws IOException {
        final String path = String.format("%s/%s/%s", os.getName(), arch.getName(), os.getLibPath());
        LOG.trace("Loading native library from {}", path);
        return RXTXLoader.class.getResourceAsStream(path);
    }

    private static File createTempDirectory() {
        final File tempDir = new File(FileUtils.getTempDirectory(), "rxtx-loader");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        return tempDir;
    }

    // See: http://forums.sun.com/thread.jspa?threadID=707176
    private static void addDirToLoadPath(final File dir) throws IOException {
        final String path = dir.getPath();

        try {
            final Field field = ClassLoader.class.getDeclaredField("usr_paths");
            final boolean accessible = field.isAccessible();

            // Make sure we have access to the field
            field.setAccessible(true);

            // Fetch a list of existing paths used by the classloader
            final String[] existingPaths = (String[]) field.get(null);
            final Set<String> newPaths = new HashSet<String>(existingPaths.length + 1);

            // Add all the old paths to our new list of paths
            newPaths.addAll(Arrays.asList(existingPaths));

            // If the new path is already in this list we don't need to continue
            if (newPaths.contains(path)) {
                return;
            }

            // Add the new path to the list of paths
            newPaths.add(path);

            // Set the classloader to use this new list of paths instead
            field.set(null, newPaths.toArray(new String[newPaths.size()]));

            // Return the visibility to whatever it was before
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path", e);
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path", e);
        }
    }
}
