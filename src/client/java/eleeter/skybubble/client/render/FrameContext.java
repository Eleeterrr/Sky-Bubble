package eleeter.skybubble.client.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

public interface FrameContext
{
    Vec3f cameraPos();

    Quaternionf cameraRotation();

    Matrix4f projectionMatrix();

    float tickDelta();

    TransformStack transformStack();
}
