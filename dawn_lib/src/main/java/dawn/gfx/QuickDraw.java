package dawn.gfx;

import dawn.lib.Dimension;
import dawn.lib.Point;
import dawn.lib.Rectangle;

public interface QuickDraw {

	QuickDraw pos(Rectangle pos);

	QuickDraw pos(int x, int y, int width, int height);

	QuickDraw pos(int x, int y, Dimension size);

	QuickDraw pos(Point position, int width, int height);

	QuickDraw pos(Point position, Dimension size);

	QuickDraw pos(int x, int y);

	QuickDraw pos(Point position);
}
