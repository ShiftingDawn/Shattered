import java.io.File;
import java.util.Locale;
import dawn.core.ExitException;
import org.apache.logging.log4j.LogManager;

public final class Main {

	private static final File ROOT_DIR = Main.getRootDir();

	static void main(final String[] args) {
		Main.addFileAppender();
		try {
			dawn.core.DawnImpl.start(Main.ROOT_DIR, args);
		} catch (final ExitException ignored) {
			System.exit(1);
		}
	}

	private static void addFileAppender() {
		final File logsDir = new File(Main.ROOT_DIR, "logs");
		final String fullPath = new File(logsDir, "latest.log").getAbsolutePath();
		System.setProperty("dawn.log.file", fullPath);
		final String cleanedPath = logsDir.getAbsolutePath() + File.separator + "%d{yyyy-MM-dd}-%i.log.gz";
		System.setProperty("dawn.log.archive", cleanedPath);
		final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();
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