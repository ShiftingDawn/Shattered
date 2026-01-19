package dawn.core.gfx;

import dawn.gfx.QuickDraw;
import dawn.gfx.Tessellator;
import dawn.lib.Dimension;
import dawn.lib.Point;
import dawn.lib.Rectangle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TessellatorQuickDrawImpl implements QuickDraw {

	private final Tessellator tessellator;

	@Override
	public QuickDraw pos(final Rectangle pos) {
		this.tessellator.pos(pos);
		return this;
	}

	@Override
	public QuickDraw pos(final int x, final int y, final int width, final int height) {
		this.tessellator.pos(x, y, width, height);
		return this;
	}

	@Override
	public QuickDraw pos(final int x, final int y, final Dimension size) {
		this.tessellator.pos(x, y, size);
		return this;
	}

	@Override
	public QuickDraw pos(final Point position, final int width, final int height) {
		this.tessellator.pos(position, width, height);
		return this;
	}

	@Override
	public QuickDraw pos(final Point position, final Dimension size) {
		this.tessellator.pos(position, size);
		return this;
	}

	@Override
	public QuickDraw pos(final int x, final int y) {
		this.tessellator.pos(x, y);
		return this;
	}

	@Override
	public QuickDraw pos(final Point position) {
		this.tessellator.pos(position);
		return this;
	}
}
