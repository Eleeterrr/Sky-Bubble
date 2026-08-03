package eleeter.skybubble.client.render;

import eleeter.elfontlib.render.MeshData;
import org.joml.Matrix4f;

public interface TextRenderer
{
    boolean isReady();

    void draw(Matrix4f projection, Matrix4f modelView, MeshData mesh, float r, float g, float b, float a);
}
