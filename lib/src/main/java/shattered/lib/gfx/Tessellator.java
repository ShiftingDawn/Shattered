package shattered.lib.gfx;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import shattered.lib.util.Color;
import shattered.lib.util.math.Dimension;
import shattered.lib.util.math.Point;

public interface Tessellator {

	Tessellator start();

	Tessellator pushMatrix(@Nullable Consumer<Matrix4f> mod);

	Tessellator pushMatrix();

	Tessellator popMatrix();

	Tessellator set(int x, int y, int width, int height);

	Tessellator set(int x, int y, Dimension size);

	Tessellator set(Point position, int width, int height);

	Tessellator set(Point position, Dimension size);

	Tessellator position(int x, int y);

	Tessellator position(Point position);

	Tessellator size(int width, int height);

	Tessellator size(Dimension size);

	Tessellator draw(Color color);

	void end();
}
