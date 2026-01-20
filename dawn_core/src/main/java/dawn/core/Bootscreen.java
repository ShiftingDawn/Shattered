package dawn.core;

import dawn.Dawn;
import dawn.Identifier;
import dawn.core.audio.AudioCategory;
import dawn.core.audio.SoundSystem;
import dawn.core.gui.GuiManagerImpl;
import dawn.gfx.Color;
import dawn.gfx.Tessellator;
import dawn.gfx.Window;
import dawn.lib.option.OptionSupplier;
import dawn.registry.ProtoAudio;
import dawn.registry.Registries;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class Bootscreen {

	private static final Identifier TEXTURE = Identifier.of("loading");
	private static final Identifier BOOT_AUDIO = Identifier.of("boot");
	private final OptionSupplier options;
	private final Window window;
	private final SoundSystem soundSystem;
	private final GuiManagerImpl guiManager;
	private boolean complete = false;
	private long startTime;

	public void init() {
		if (!this.options.enableBootscreen().getAsBoolean()) {
			this.complete = true;
			this.guiManager.setEnabled(true);
			return;
		}
		final ProtoAudio proto = Registries.get().audio().get(Bootscreen.BOOT_AUDIO);
		if (proto != null) {
			this.soundSystem.play(proto, AudioCategory.UI);
		}
		this.startTime = Dawn.clock();
	}

	public void render(final Tessellator tessellator) {
		if (this.complete) {
			return;
		}
		if (Dawn.clock() - this.startTime < 2000) {
			tessellator.render(Color.BLACK, p -> p.pos(0, 0, this.window.getWidth(), this.window.getHeight()));
			final float alpha = (Dawn.clock() - this.startTime) * 0.001f;
			tessellator.start().set(Bootscreen.TEXTURE, Color.WHITE.withAlpha(Math.min(alpha, 1f)))
				.pos(0, 0, 256, 42).centerX(this.window.getWidth()).centerY(this.window.getHeight()).draw().end();
		} else {
			if (!this.guiManager.isEnabled()) {
				this.guiManager.setEnabled(true);
			}
			final long time = Dawn.clock() - this.startTime - 2000;
			final float alpha = 1f - (time * 0.002f);
			tessellator.render(Color.BLACK.withAlpha(alpha), p -> p.pos(0, 0, this.window.getWidth(), this.window.getHeight()));
			tessellator.start().set(Bootscreen.TEXTURE, Color.WHITE.withAlpha(alpha))
				.pos(0, 0, 256, 42).centerX(this.window.getWidth()).centerY(this.window.getHeight()).draw().end();
			if (time >= 1500) {
				this.complete = true;
			}
		}
	}
}
