package dawn.core.registry;

import com.google.gson.annotations.SerializedName;
import dawn.Identifier;
import dawn.core.ExitException;
import dawn.lib.Json;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoShader;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullUnmarked;

final class ProtoShaderContentFactory extends BaseRegistryContentFactory<ProtoShader> {

	@Override
	public ProtoShader make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		this.printDescription(logger, "shader", registryKey);
		final JsonData data = this.loadJsonData(JsonData.class, logger, resources, registryKey, "shader", false)
			.orElseThrow(ExitException::new);
		return new ProtoShaderImpl(registryKey, data.canTexture, data.canColor,
			data.propOutColor, data.propMatrixProjection, data.propMatrixModelView, data.propMatrixTessellatorTransform,
			data.propEnableTexture
		);
	}

	@NullUnmarked
	private static class JsonData {

		@SerializedName("can_texture")
		public Boolean canTexture;

		@SerializedName("can_color")
		public Boolean canColor;

		@SerializedName("prop_out_color")
		@Json.Required
		public String propOutColor;

		@SerializedName("prop_matrix_projection")
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
}
