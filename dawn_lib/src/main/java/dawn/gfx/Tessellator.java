package dawn.gfx;

import java.util.function.Consumer;
import dawn.Identifier;
import dawn.asset.Shader;
import dawn.asset.Texture;
import dawn.lib.Dimension;
import dawn.lib.Point;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public interface Tessellator {

	Tessellator start();

	Tessellator pushMatrix(@Nullable Consumer<Matrix4f> mod);

	Tessellator pushMatrix();

	Tessellator modMatrix(Consumer<Matrix4f> mod);

	Tessellator popMatrix();

	Tessellator set(Texture texture, Color tint);

	Tessellator set(Texture texture);

	Tessellator set(Identifier texture, Color tint);

	Tessellator set(Identifier texture);

	Tessellator set(Color color);

	Tessellator pos(int x, int y, int width, int height);

	Tessellator pos(int x, int y, Dimension size);

	Tessellator pos(Point position, int width, int height);

	Tessellator pos(Point position, Dimension size);

	Tessellator pos(int x, int y);

	Tessellator pos(Point position);

	Tessellator size(int width, int height);

	Tessellator size(Dimension size);

	Tessellator centerX(int maxWidth);

	Tessellator centerY(int maxHeight);

	Tessellator uv(int uMin, int vMin, int uMax, int vMax);

	Tessellator color(Color colorTopLeft, Color colorTopRight, Color colorBottomRight, Color colorBottomLeft);

	Tessellator color(Color color);

	Tessellator colorTopLeft(Color color);

	Tessellator colorTopRight(Color color);

	Tessellator colorBottomLeft(Color color);

	Tessellator colorBottomRight(Color color);

	Tessellator colorTop(Color color);

	Tessellator colorBottom(Color color);

	Tessellator colorLeft(Color color);

	Tessellator colorRight(Color color);

	Tessellator draw(Shader shader);

	Tessellator draw();

	void end();
}
