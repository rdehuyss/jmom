package org.jmom.interfaces.rfxcom;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeLibraryTools {

	private static final String RXTX_LIBNAME = System.mapLibraryName("rxtxSerial");

	private static Logger LOGGER = LoggerFactory.getLogger(NativeLibraryTools.class);

	static {
		LOGGER.info("java.library.path:       " + getNativeRFXComLookupPath());
		LOGGER.info("current platform:       " + NativeLibraryTools.getCurrentPlatformIdentifier());
	}

	public static String getNativeRFXComLookupPath() {
		return System.getProperty("java.library.path", "").replace(":", ";");
	}

	public static boolean hasNativeRFXComLibrary() {
		try {
			System.loadLibrary("rxtxSerial");
			return true;
		} catch (UnsatisfiedLinkError error) {
			return false;
		}
	}

	public static String getCurrentPlatformIdentifier() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			osName = "win";
		}

		return osName + "/" + System.getProperty("os.arch");
	}

	public static boolean loadEmbeddedLibrary() {

		boolean usingEmbedded = false;

		StringBuilder url = new StringBuilder();
		url.append("/NATIVE/");
		url.append(getCurrentPlatformIdentifier());
		url.append("/" + RXTX_LIBNAME);
		URL nativeLibraryUrl = NativeLibraryTools.class.getResource(url.toString());

		if (nativeLibraryUrl != null) {
			try {

				final File tempFile = File.createTempFile("librxtxSerial-", ".lib");
				final File libfile = new File(tempFile.getParentFile(), RXTX_LIBNAME);
				libfile.deleteOnExit(); // just in case
				final InputStream in = nativeLibraryUrl.openStream();
				final OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));

				int len = 0;
				byte[] buffer = new byte[8192];
				while ((len = in.read(buffer)) > -1)
					out.write(buffer, 0, len);
				out.close();
				in.close();

				System.load(libfile.getAbsolutePath());
				addLibraryPath(libfile.getParent());
				System.loadLibrary("rxtxSerial");

				usingEmbedded = true;

			} catch (Exception x) {
				// mission failed, do nothing
			}

		} // nativeLibraryUrl exists

		return usingEmbedded;

	}

	private static void addLibraryPath(String pathToAdd) throws Exception {
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] paths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (String path : paths) {
			if (path.equals(pathToAdd)) {
				return;
			}
		}

		// add the new path
		final String[] newPaths = new String[paths.length + 1];
		System.arraycopy(paths, 0, newPaths, 0, paths.length - 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
	}

	private NativeLibraryTools() {
	}
}