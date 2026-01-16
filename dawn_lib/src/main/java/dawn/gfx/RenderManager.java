package dawn.gfx;

import dawn.asset.Shader;

public interface RenderManager {

	Shader getRootShader();

	Tessellator getTessellator();

	FontRenderer getFontRenderer();
}
