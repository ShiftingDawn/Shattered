package dawn.internal;

import com.google.gson.Gson;
import dawn.Dawn;
import dawn.event.EventBus;
import dawn.gfx.GlStateManager;
import dawn.gfx.ShaderProps;
import dawn.gui.GuiManager;
import dawn.registry.Registries;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public final class DawnLib {

	public static Dawn INSTANCE;
	public static String IDENTIFIER_DEFAULT_DOMAIN;
	public static EventBus BUS;
	public static Registries REGISTRIES;
	public static Gson GSON;
	public static JsonValidator JSON_VALIDATOR;
	public static StringTypeValidator STRING_TYPE_VALIDATOR;
	public static GlStateManager GL;
	public static ShaderProps SHADER_PROPS;
	public static final ThreadLocal<GuiManager> GUI_MANAGER = new ThreadLocal<>();

	private DawnLib() {
	}
}
