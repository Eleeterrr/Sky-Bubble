package eleeter.skybubble.client;

import eleeter.elfontlib.font.Font;
import eleeter.skybubble.client.chat.ChatBubbleManager;
import eleeter.skybubble.client.chat.render.ChatBubbleRenderer;
import eleeter.skybubble.client.platform.MinecraftColoredGeometryRenderer;
import eleeter.skybubble.client.platform.MinecraftMsdfTextRenderer;
import eleeter.skybubble.client.platform.MinecraftPlayerLocator;
import eleeter.skybubble.client.platform.MinecraftWorldRenderRegistrar;
import eleeter.skybubble.client.platform.loaders.FontLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


public class SkyBubbleClient implements ClientModInitializer
{

    private final MinecraftMsdfTextRenderer textRenderer = new MinecraftMsdfTextRenderer();

    @Override
    public void onInitializeClient()
    {

        MinecraftWorldRenderRegistrar registrar = new MinecraftWorldRenderRegistrar();
        MinecraftPlayerLocator playerLocator = new MinecraftPlayerLocator();
        MinecraftColoredGeometryRenderer coloredRenderer = new MinecraftColoredGeometryRenderer();

        ChatBubbleRenderer.register(registrar, playerLocator, coloredRenderer, textRenderer);

        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            if (!client.isPaused())
            {
                ChatBubbleManager.tick(1f / 20f);
            }
        });


        ClientLifecycleEvents.CLIENT_STARTED.register(ignoredClient ->
        {
            Font font = FontLoader.loadFont(net.minecraft.client.Minecraft.getInstance());
            if (font == null)
            {
                return;
            }

            ChatBubbleManager.init(font);
        });
    }


}