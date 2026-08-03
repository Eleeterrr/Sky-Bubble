package eleeter.skybubble.client.platform;

import eleeter.elfontlib.emoji.EmojiGlyph;
import eleeter.skybubble.client.platform.loaders.EmojiAtlasLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class MinecraftEmojiPanelRenderer implements EmojiPanelRenderer
{
    private final GuiGraphicsExtractor graphics;

    public MinecraftEmojiPanelRenderer(GuiGraphicsExtractor graphics)
    {
        this.graphics = graphics;
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color)
    {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void text(String text, int x, int y, int color, boolean shadow)
    {
        graphics.text(Minecraft.getInstance().font, Component.literal(text), x, y, color, shadow);
    }

    @Override
    public void blitTexture(EmojiGlyph glyph, int gx, int gy, int glyphSize)
    {
        graphics.blit(EmojiAtlasLoader.EMOJI_ATLAS_TEXTURE, gx, gy, gx + glyphSize, gy + glyphSize, glyph.u0, glyph.u1, glyph.v0, glyph.v1
        );
    }

    @Override
    public void enableScissor(int x0, int y0, int x1, int y1)
    {
        graphics.enableScissor(x0, y0, x1, y1);
    }

    @Override
    public void disableScissor()
    {
        graphics.disableScissor();
    }
}