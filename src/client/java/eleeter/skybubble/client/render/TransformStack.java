package eleeter.skybubble.client.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

public interface TransformStack
{
    void push();

    void pop();

    void translate(double x, double y, double z);

    void rotate(Quaternionf rotation);

    void rotateY180();

    void scale(float x, float y, float z);

    Matrix4f peekPositionMatrix();
}
