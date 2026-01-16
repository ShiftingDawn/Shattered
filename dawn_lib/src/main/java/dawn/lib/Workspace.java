package dawn.lib;

import java.io.File;
import java.io.IOException;
import lombok.Getter;

public final class Workspace {

	private final File rootDir;
	private final @Getter File binDir;
	private final @Getter File dataDir;
	private final @Getter File logsDir;
	private final @Getter File tempDir;

	public Workspace(File rootDir) throws IOException {
		this.rootDir = rootDir;
		this.binDir = makeDir("bin");
		this.dataDir = makeDir("data");
		this.logsDir = makeDir("logs");
		this.tempDir = makeDir("temp");
	}

	public File getBinFile(String name) {
		return new File(this.binDir, name);
	}

	public File getDataFile(String name) {
		return new File(this.dataDir, name);
	}

	public File getTempFile(String name) {
		return new File(this.tempDir, name);
	}

	private File makeDir(String name) throws IOException {
		File result = new File(this.rootDir, name);
		if (result.exists()) {
			if (!result.isDirectory()) {
				throw new IOException("Could not create directory %s. Reason: name taken by regular file".formatted(result.getAbsolutePath()));
			}
		} else if (!result.mkdirs()) {
			throw new IOException("Could not create directory %s. Reason: unknown".formatted(result.getAbsolutePath()));
		}
		return result;
	}
}
