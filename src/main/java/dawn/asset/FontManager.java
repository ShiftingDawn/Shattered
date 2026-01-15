package dawn.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentHashMap;
import dawn.Dawn;
import dawn.registry.Identifier;
import dawn.registry.Registries;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png_to_func;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForMappingEmToPixels;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class FontManager {

	//TODO make variable
	private static final int FONT_HEIGHT = 64;
	private static final int FONT_CHARS = 95;
	private static final int FONT_CHAR_START = 32;
	private static final int FONT_SIZE_START = 64;
	private static final Logger LOGGER = Dawn.getLogger("Fonts");
	private final ConcurrentHashMap<Identifier, Font> mapping = new ConcurrentHashMap<>();
	private final AssetManager assets;

	void init() {
		FontManager.LOGGER.info("Reloading fonts");
		for (final FontAsset font : Registries.FONTS) {
			this.unloadFont(font);
			this.loadFont(font);
		}
	}

	private void loadFont(final FontAsset font) {
		FontManager.LOGGER.debug("\tLoading font {}", font.getRegistryKey());
		if (this.mapping.containsKey(font.getRegistryKey())) {
			FontManager.LOGGER.error("\tTrying to load duplicate font {}. This will most likely result in a memory leak.", font.getRegistryKey());
		}
		final ByteBuffer fileDataBuffer;
		try {
			FontManager.LOGGER.debug("\t\tReading font file");
			final String path = this.assets.getResources().makePath(font.getRegistryKey(), "font", "ttf");
			fileDataBuffer = this.assets.getResources().getBuffer(path);
			if (fileDataBuffer == null) {
				throw new FileNotFoundException("Font file does not exist. Expected path: " + path);
			}
		} catch (final IOException e) {
			FontManager.LOGGER.error("Could not load font {}.", font.getRegistryKey());
			FontManager.LOGGER.error(e);
			return;
		}
		FontManager.LOGGER.debug("\t\tLoading metadata");
		final float lineHeight;
		final float baseline;
		try (STBTTFontinfo fontInfo = STBTTFontinfo.create()) {
			stbtt_InitFont(fontInfo, fileDataBuffer);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				final IntBuffer ascentPtr = stack.mallocInt(1);
				final IntBuffer descentPtr = stack.mallocInt(1);
				final IntBuffer lineGapPtr = stack.mallocInt(1);
				stbtt_GetFontVMetrics(fontInfo, ascentPtr, descentPtr, lineGapPtr);
				final int ascent = ascentPtr.get(0);
				final int descent = descentPtr.get(0);
				final int lineGap = lineGapPtr.get(0);
				final float scale = stbtt_ScaleForMappingEmToPixels(fontInfo, FontManager.FONT_HEIGHT);
				lineHeight = scale * (ascent + descent + lineGap);
				baseline = scale * ascent;
			}
		}
		FontManager.LOGGER.debug("\t\tBaking bitmap");
		final STBTTBakedChar.Buffer charData = STBTTBakedChar.create(FontManager.FONT_CHARS);
		final BakeResult bakeResult = FontManager.createBakedBuffer(FontManager.FONT_SIZE_START, FontManager.FONT_SIZE_START, fileDataBuffer, charData);
		final ByteBuffer imageData = memAlloc(bakeResult.buffer().capacity() * 4);
		for (int i = 0; i < bakeResult.buffer().capacity(); ++i) {
			imageData.put((byte) 255);
			imageData.put((byte) 255);
			imageData.put((byte) 255);
			imageData.put(bakeResult.buffer().get(i));
		}
		imageData.flip();
		FontManager.LOGGER.debug("\t\tSerializing bitmap");
		final InMemoryPngWriter writer = new InMemoryPngWriter();
		//TODO check if context (window) is needed
		stbi_write_png_to_func(writer, Dawn.getDawn().getWindow().getPointer(), bakeResult.width, bakeResult.height, 4, imageData, bakeResult.width * 4);
		this.assets.dumpAsset("font/%s.png".formatted(font.getRegistryKey().toPathSafeString()), writer.getData());
		FontManager.LOGGER.debug("\t\tCalculating glyph data");
		final Char2ObjectArrayMap<Font.Glyph> glyphs = new Char2ObjectArrayMap<>();
		for (int i = 0; i < charData.capacity(); ++i) {
			final STBTTBakedChar bakedChar = charData.get(i);
			final Font.Glyph glyph = new Font.Glyph(
				bakedChar.x0(), bakedChar.y0(), bakedChar.x1(), bakedChar.y1(),
				bakedChar.xoff(), bakedChar.yoff(),
				bakedChar.xadvance()
			);
			glyphs.put((char) (i + 32), glyph);
		}
		FontManager.LOGGER.debug("\t\tGenerating data");
		writer.getData().position(0);
		final Texture fontTexture = TextureManager.makeTextureFromData(new TextureAsset.Default(font.getRegistryKey()), writer.getData(), false);
		FontManager.LOGGER.debug("\t\tCleaning up");
		writer.free();
		charData.free();
		MemoryUtil.memFree(imageData);
		MemoryUtil.memFree(bakeResult.buffer());
		final Font result = new Font(font, fontTexture, Char2ObjectMaps.unmodifiable(glyphs), lineHeight, baseline, FontManager.FONT_HEIGHT);
		this.mapping.put(font.getRegistryKey(), result);
		FontManager.LOGGER.debug("\t\tDone");
	}

	private void unloadFont(final FontAsset font) {
		final Font fnt = this.mapping.get(font.getRegistryKey());
		if (fnt != null) {
			FontManager.LOGGER.debug("\tUnloading font {} (texture={})", font.getRegistryKey(), fnt.texture().id());
			glDeleteTextures(fnt.texture().id());
			this.mapping.remove(font.getRegistryKey());
			FontManager.LOGGER.debug("\t\tDone");
		}
	}

	public Font getFont(final Identifier font) {
		return this.mapping.get(font);
	}

	public Font getFont(final FontAsset font) {
		return this.getFont(font.getRegistryKey());
	}

	private static BakeResult createBakedBuffer(final int width, final int height, final ByteBuffer fileDataBuffer, final STBTTBakedChar.Buffer charData) {
		final ByteBuffer bitmap = memAlloc(width * height);
		if (stbtt_BakeFontBitmap(fileDataBuffer, FontManager.FONT_HEIGHT, bitmap, width, height, FontManager.FONT_CHAR_START, charData) < 0) {
			final int newWidth = width == height ? width * 2 : width;
			final int newHeight = width == height ? height : height * 2;
			FontManager.LOGGER.debug("\t\tCould not bake bitmap. Trying new size {}x{}", newWidth, newHeight);
			memFree(bitmap);
			return FontManager.createBakedBuffer(newWidth, newHeight, fileDataBuffer, charData);
		}
		return new BakeResult(bitmap, width, height);
	}

	private record BakeResult(ByteBuffer buffer, int width, int height) {}
}