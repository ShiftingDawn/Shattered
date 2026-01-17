package dawn.core;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.TreeSet;
import dawn.core.app.IBootApp;
import org.apache.logging.log4j.LogManager;

public final class Main {

	private static final File ROOT_DIR = Main.getRootDir();

	static void main(final String[] args) {
		final File logsDir = new File(Main.ROOT_DIR, "logs");
		Main.addFileAppender(logsDir);
		Main.cleanOldLogs(logsDir);
		final IBootApp bootApp = ServiceLoader.load(IBootApp.class, Main.class.getClassLoader()).findFirst()
			.orElseThrow(() -> new IllegalStateException("Cannot find BootApp"));
		try {
			dawn.core.DawnImpl.start(Main.ROOT_DIR, args, bootApp);
		} catch (final ExitException ignored) {
			System.exit(1);
		}
	}

	private static void addFileAppender(final File logsDir) {
		final String fullPath = new File(logsDir, "latest.log").getAbsolutePath();
		System.setProperty("dawn.log.file", fullPath);
		final String cleanedPath = logsDir.getAbsolutePath() + File.separator + "%d{yyyy-MM-dd_HH:mm:ss}-%i.log.gz";
		System.setProperty("dawn.log.archive", cleanedPath);
		final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();
	}

	private static void cleanOldLogs(final File logsDir) {
		final Runnable cleaner = () -> {
			final File[] files = logsDir.listFiles((_, name) -> name.endsWith(".log.gz"));
			//Try next time if its null
			if (files != null) {
				final TreeSet<File> sorted = new TreeSet<>((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
				sorted.addAll(Arrays.asList(files));
				final Iterator<File> iterator = sorted.iterator();
				while (iterator.hasNext() && sorted.size() > 5) {
					final File f = iterator.next();
					f.delete();
					iterator.remove();
				}
			}
		};
		final Thread t = new Thread(cleaner, "Old log cleaner thread");
		t.setDaemon(true);
		t.start();
	}

	private static File getRootDir() {
		final String rootPathProperty = System.getProperty("dawn.workspace.root");
		if (rootPathProperty != null) {
			final File result = new File(rootPathProperty);
			if (!result.exists() && !result.mkdirs()) {
				throw new IllegalArgumentException("Cannot use path as workspace: " + rootPathProperty);
			}
			return result;
		}
		final int os = Main.getOperatingSystem();
		File result = new File(System.getProperty("user.home"), ".local/share/ShiftingDawn");
		if (os == 1) {
			final String appdata = System.getenv("APPDATA");
			if (appdata != null) {
				result = new File(appdata, "ShiftingDawn");
			}
		} else if (os == 2) {
			result = new File(System.getProperty("user.home"), "Library/Application Support/ShiftingDawn");
		}
		if (!result.exists() && !result.mkdirs()) {
			throw new IllegalArgumentException("Cannot make workspace: " + result.getAbsolutePath());
		}
		return result;
	}

	private static int getOperatingSystem() {
		final String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (os.contains("win")) {
			return 1;
		} else if (os.contains("mac") || os.contains("darwin")) {
			return 2;
		}
		return 0;
	}
}