package eleeter.skybubble.client.render;

import eleeter.elfontlib.emoji.EmojiGlyph;

public interface EmojiPanelRenderer
{
    void fill(int x1, int y1, int x2, int y2, int color);
    void text(String text, int x, int y, int color, boolean shadow);
    void blitTexture(EmojiGlyph glyph, int gx, int gy, int glyphSize);
    void enableScissor(int x0, int y0, int x1, int y1);
    void disableScissor();
}