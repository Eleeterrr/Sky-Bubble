package eleeter.skybubble.client.render;

import org.joml.Matrix4f;

public interface ColoredGeometryRenderer
{
    void draw(Matrix4f transform, float[] xyzrgba);
}
