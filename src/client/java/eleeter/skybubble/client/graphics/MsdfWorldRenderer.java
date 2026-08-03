package eleeter.skybubble.client.graphics;

import eleeter.skybubble.client.render.TextRenderer;
import eleeter.skybubble.client.render.TransformStack;
import eleeter.skybubble.client.render.WorldRenderRegistrar;
import eleeter.skybubble.client.render.Vec3f;
import org.joml.Matrix4f;

public class MsdfWorldRenderer
{
    private static WorldTextInstance text;

    private static double worldX = 0, worldY = 64, worldZ = 0;

    public static void setInstance(WorldTextInstance instance)
    {
        text = instance;
    }

    public static void setPosition(double x, double y, double z)
    {
        worldX = x;
        worldY = y;
        worldZ = z;
    }

    public static void register(WorldRenderRegistrar registrar, TextRenderer textRenderer)
    {
        registrar.onFrameAfterTranslucent(ctx ->
        {
            if (text == null || !textRenderer.isReady()) return;

            Vec3f cam = ctx.cameraPos();

            TransformStack matrices = ctx.transformStack();
            matrices.push();
            matrices.translate(worldX - cam.x(), worldY - cam.y(), worldZ - cam.z());

            float scale = 0.05f / 1000.0f;
            matrices.scale(scale, scale, scale);

            Matrix4f modelView = matrices.peekPositionMatrix();
            Matrix4f projection = ctx.projectionMatrix();

            matrices.pop();

            textRenderer.draw(projection, modelView, text.mesh, 1.0f, 1.0f, 1.0f, 1.0f);
        });
    }
}
