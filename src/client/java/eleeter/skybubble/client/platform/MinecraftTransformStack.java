package eleeter.skybubble.client.platform;

import eleeter.skybubble.client.render.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class MinecraftTransformStack implements TransformStack
{

    private final PoseStack matrixStack;

    public MinecraftTransformStack(PoseStack matrixStack)
    {
        this.matrixStack = matrixStack;
    }

    @Override
    public void push()
    {
        matrixStack.pushPose();
    }

    @Override
    public void pop()
    {
        matrixStack.popPose();
    }

    @Override
    public void translate(double x, double y, double z)
    {
        matrixStack.translate(x, y, z);
    }

    @Override
    public void rotate(Quaternionf rotation)
    {
        matrixStack.mulPose(rotation);
    }

    @Override
    public void rotateY180()
    {
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
    }

    @Override
    public void scale(float x, float y, float z)
    {
        matrixStack.scale(x, y, z);
    }

    @Override
    public Matrix4f peekPositionMatrix()
    {
        return new Matrix4f(matrixStack.last().pose());
    }
}