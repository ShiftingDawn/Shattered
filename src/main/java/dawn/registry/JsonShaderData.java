package dawn.registry;

import com.google.gson.annotations.SerializedName;
import dawn.lib.json.Json;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
class JsonShaderData {

	@SerializedName("can_texture")
	public Boolean canTexture;

	@SerializedName("can_color")
	public Boolean canColor;

	@SerializedName("prop_out_color")
	@Json.Required
	public String propOutColor;

	@SerializedName("prop_matrix_rojection")
	@Json.Required
	public String propMatrixProjection;

	@SerializedName("prop_matrix_modelview")
	@Json.Required
	public String propMatrixModelView;

	@SerializedName("prop_matrix_tessellator_transform")
	@Json.Required
	public String propMatrixTessellatorTransform;

	@SerializedName("prop_enable_textures")
	@Json.Required.When(fieldName = "can_texture")
	public String propEnableTexture;
}
