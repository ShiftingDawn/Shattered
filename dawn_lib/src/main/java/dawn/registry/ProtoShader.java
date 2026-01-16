package dawn.registry;

public interface ProtoShader extends RegistryObject {

	boolean isCanTexture();

	boolean isCanColor();

	String getPropOutColor();

	String getPropMatrixProjection();

	String getPropMatrixModelView();

	String getPropMatrixTessellatorTransform();

	String getPropEnableTexture();
}
