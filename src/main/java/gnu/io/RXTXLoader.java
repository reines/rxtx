package gnu.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class RXTXLoader {

	private enum OperatingSystem {
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
	};

	private enum Architecture {
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

	private static final Random rand;

	static {
		rand = new Random();
	}

	public static void load() throws IOException {
		final File tempDir = RXTXLoader.createTempDirectory();

		final OperatingSystem osType = OperatingSystem.fromString(System.getProperty("os.name"));
		final Architecture osArch = Architecture.fromString(System.getProperty("os.arch"));
		if (osType == null || osArch == null)
			return;

		final String source = osType.toString().toLowerCase() + File.separator + osArch.toString().toLowerCase() + File.separator + osType.getLibPath();
		final String target = tempDir.getPath() + File.separator + osType.getLibPath();

		final File lib = RXTXLoader.copyResourceToFS(source, target);
		lib.deleteOnExit();

		RXTXLoader.addDirToLoadPath(tempDir.getPath());
	}

	private static File createTempDirectory() {
		final String baseTempPath = System.getProperty("java.io.tmpdir");
		final int randomInt = 100000 + rand.nextInt(899999);

		final File tempDir = new File(baseTempPath + File.separator + "rxtx" + randomInt);
		if (!tempDir.exists())
			tempDir.mkdir();

		tempDir.deleteOnExit();

		return tempDir;
	}

	private static File copyResourceToFS(String resourcePath, String targetFsLocation) throws IOException {
		final InputStream source = RXTXLoader.class.getResourceAsStream(resourcePath);
		final File target = new File(targetFsLocation);

		FileUtils.copyInputStreamToFile(source, target);

		source.close();

		return target;
	}

	// See: http://forums.sun.com/thread.jspa?threadID=707176
	private static void addDirToLoadPath(String path) throws IOException {
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
