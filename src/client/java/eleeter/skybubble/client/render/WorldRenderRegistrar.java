package eleeter.skybubble.client.render;

import java.util.function.Consumer;

public interface WorldRenderRegistrar
{
    void onFrameLast(Consumer<FrameContext> callback);

    void onFrameAfterTranslucent(Consumer<FrameContext> callback);
}
