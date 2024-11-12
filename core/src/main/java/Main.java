import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import shattered.core.Shattered;
import shattered.core.lib.RuntimeMetadata;

public final class Main {

	private static final File ROOT_DIR = Main.getRootDir();

	public static void main(final String[] args) throws IOException, URISyntaxException {
		Main.addFileAppender();
		RuntimeMetadata.scan(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		Shattered.start(Main.ROOT_DIR, args);
	}

	private static void addFileAppender() {
		final File logsDir = new File(Main.ROOT_DIR, "logs");
		final String fullPath = new File(logsDir, "latest.log").getAbsolutePath();
		System.setProperty("shattered.log.file", fullPath);
		final String cleanedPath = logsDir.getAbsolutePath() + File.separator + "%d{yyyy-MM-dd}-%i.log.gz";
		System.setProperty("shattered.log.archive", cleanedPath);
		final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();
	}

	private static File getRootDir() {
		final String rootPathProperty = System.getProperty("shattered.workspace.root");
		if (rootPathProperty != null) {
			final File result = new File(rootPathProperty);
			if (!result.exists() && !result.mkdirs()) {
				throw new IllegalArgumentException("Cannot use path as workspace: " + rootPathProperty);
			}
			return result;
		}
		final int os = Main.getOperatingSystem();
		File result = new File(System.getProperty("user.home"), ".shattered");
		if (os == 1) {
			final String appdata = System.getenv("APPDATA");
			if (appdata != null) {
				result = new File(appdata, "Shattered");
			}
		} else if (os == 2) {
			result = new File(System.getProperty("user.home"), "Library/Application Support/Shattered");
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
