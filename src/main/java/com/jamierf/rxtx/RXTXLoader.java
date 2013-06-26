package com.jamierf.rxtx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class RXTXLoader {

	public static enum OperatingSystem {
		WINDOWS("Windows", "rxtxSerial.dll"),
		LINUX("Linux", "librxtxSerial.so"),
		MACOSX("Mac OS X", "librxtxSerial.jnilib");

		public static OperatingSystem fromString(String name) {
			for (OperatingSystem os : OperatingSystem.values()) {
				if (name.indexOf(os.key) >= 0)
					return os;
			}

			return null;
		}

		private final String key;
		private final String libPath;

		private OperatingSystem(String key, String libPath) {
			this.key = key;
			this.libPath = libPath;
		}

		public String getLibPath() {
			return libPath;
		}
	}

	public static enum Architecture {
		X86_64("amd64", "x86_64"),
		X86("i386", "x86");

		public static Architecture fromString(String name) {
			for (Architecture arch : Architecture.values()) {
				for (String key : arch.keys) {
					if (name.indexOf(key) >= 0)
						return arch;
				}
			}

			return null;
		}

		private final String[] keys;

		private Architecture(String ... keys) {
			this.keys = keys;
		}
	}

	public static void load() throws IOException {
		final OperatingSystem os = OperatingSystem.fromString(System.getProperty("os.name"));
		final Architecture arch = Architecture.fromString(System.getProperty("os.arch"));

        RXTXLoader.load(os, arch);
	}

    public static void load(OperatingSystem os, Architecture arch) throws IOException {
        if (os == null || arch == null)
            throw new IOException("Unsupported operating system or architecture");

		final File tempDir = RXTXLoader.createTempDirectory();
        final InputStream source = RXTXLoader.class.getResourceAsStream(os.toString().toLowerCase() + File.separator + arch.toString().toLowerCase() + File.separator + os.getLibPath());
        final File target = new File(tempDir, os.getLibPath());

        try {
            FileUtils.copyInputStreamToFile(source, target);
            RXTXLoader.addDirToLoadPath(tempDir);
        } finally {
            source.close();
        }
    }

	private static File createTempDirectory() {
		final File tempDir = new File(FileUtils.getTempDirectory(), "rxtx-loader");
		if (!tempDir.exists())
			tempDir.mkdir();

		return tempDir;
	}

	// See: http://forums.sun.com/thread.jspa?threadID=707176
	private static void addDirToLoadPath(File dir) throws IOException {
        final String path = dir.getPath();

		try {
			final Field field = ClassLoader.class.getDeclaredField("usr_paths");
			final boolean accessible = field.isAccessible();

			// Make sure we have access to the field
			field.setAccessible(true);

			// Fetch a list of existing paths used by the classloader
			final String[] existingPaths = (String[]) field.get(null);
			final List<String> newPaths = new ArrayList<String>(existingPaths.length + 1);

			// Add all the old paths to our new list of paths
			for (String existingPath : existingPaths)
				newPaths.add(existingPath);

			// If the new path is already in this list we don't need to continue
			if (newPaths.contains(path))
				return;

			// Add the new path to the list of paths
			newPaths.add(path);

			// Set the classloader to use this new list of paths instead
			field.set(null, newPaths.toArray(new String[newPaths.size()]));

			// Return the visibility to whatever it was before
			field.setAccessible(accessible);
		}
		catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		}
		catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}
}
