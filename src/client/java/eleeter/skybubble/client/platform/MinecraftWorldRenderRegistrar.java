package eleeter.skybubble.client.platform;

import eleeter.skybubble.client.render.FrameContext;
import eleeter.skybubble.client.render.WorldRenderRegistrar;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;


public class MinecraftWorldRenderRegistrar implements WorldRenderRegistrar
{

    @Override
    public void onFrameLast(Consumer<FrameContext> callback)
    {
        LevelRenderEvents.END_MAIN.register(ctx -> callback.accept(new MinecraftFrameContext(ctx)));
    }

    @Override
    public void onFrameAfterTranslucent(Consumer<FrameContext> callback)
    {
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(ctx -> callback.accept(new MinecraftFrameContext(ctx)));
    }
}