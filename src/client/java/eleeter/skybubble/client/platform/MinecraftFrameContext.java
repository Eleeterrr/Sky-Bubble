package eleeter.skybubble.client.platform;

import eleeter.skybubble.client.render.FrameContext;
import eleeter.skybubble.client.render.TransformStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import eleeter.skybubble.client.render.Vec3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;


public class MinecraftFrameContext implements FrameContext
{

    private final LevelRenderContext context;
    private final TransformStack transformStack;

    public MinecraftFrameContext(LevelRenderContext context)
    {
        this.context = context;
        this.transformStack = new MinecraftTransformStack(context.poseStack());
    }

    private CameraRenderState camera()
    {
        return context.levelState().cameraRenderState;
    }

    @Override
    public Vec3f cameraPos()
    {
        Vec3 pos = camera().pos;
        return new Vec3f(pos.x, pos.y, pos.z);
    }

    @Override
    public Quaternionf cameraRotation()
    {
        return camera().orientation.conjugate(new Quaternionf());
    }

    @Override
    public Matrix4f projectionMatrix()
    {
        return new Matrix4f(camera().projectionMatrix);
    }

    @Override
    public float tickDelta()
    {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    @Override
    public TransformStack transformStack()
    {
        return transformStack;
    }
}