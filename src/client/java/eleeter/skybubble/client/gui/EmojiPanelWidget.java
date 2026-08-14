package eleeter.skybubble.client.gui;

import eleeter.elfontlib.emoji.EmojiAtlasData;
import eleeter.elfontlib.emoji.EmojiFont;
import eleeter.elfontlib.emoji.EmojiGlyph;
import eleeter.skybubble.client.platform.MinecraftEmojiPanelRenderer;
import eleeter.skybubble.client.render.EmojiPanelRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EmojiPanelWidget extends AbstractWidget
{
    private static final int CELL_SIZE = 20;
    private static final int CELL_PAD = 2;
    private static final int GLYPH_SIZE = 16;

    private final EmojiFont emojiFont;
    private final List<EmojiGlyph> glyphs = new ArrayList<>();
    private final Consumer<String> onEmojiPicked;

    private int columns = 6;
    private double scroll = 0.0;
    private int hoveredIndex = -1;

    public EmojiPanelWidget(int x, int y, int width, int height, EmojiFont emojiFont, EmojiAtlasData atlasData, Consumer<String> onEmojiPicked)
    {
        super(x, y, width, height, Component.translatable("sky_bubble.emoji_panel.title"));
        this.emojiFont = emojiFont;
        this.onEmojiPicked = onEmojiPicked;
        this.visible = false;

        if (emojiFont != null && atlasData != null && atlasData.emojis != null)
        {
            for (EmojiAtlasData.EmojiEntry entry : atlasData.emojis)
            {
                if (entry.codepoints == null || entry.codepoints.length == 0)
                {
                    continue;
                }
                EmojiGlyph glyph = emojiFont.getEmoji(entry.codepoints);

                if (glyph != null)
                {
                    this.glyphs.add(glyph);
                }
            }
        }

        this.columns = Math.max(1, (width - CELL_PAD) / (CELL_SIZE + CELL_PAD));
    }

    public void toggle()
    {
        this.visible = !this.visible;
        if (!this.visible)
        {
            this.hoveredIndex = -1;
        }
    }

    public void close()
    {
        this.visible = false;
        this.hoveredIndex = -1;
    }

    private int rowCount()
    {
        return (int) Math.ceil(glyphs.size() / (double) columns);
    }

    private int maxScroll()
    {
        int contentHeight = rowCount() * (CELL_SIZE + CELL_PAD) + CELL_PAD;
        return Math.max(0, contentHeight - this.getHeight());
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        EmojiPanelRenderer renderer = new MinecraftEmojiPanelRenderer(graphics);
        renderWidget(renderer, mouseX, mouseY);
    }

    public void renderWidget(EmojiPanelRenderer renderer, int mouseX, int mouseY)
    {
        int x0 = this.getX();
        int y0 = this.getY();
        int x1 = x0 + this.getWidth();
        int y1 = y0 + this.getHeight();

        renderer.fill(x0, y0, x1, y1, 0xF01E1F22);
        renderer.fill(x0, y0, x1, y0 + 2, 0x805865F2);
        renderer.fill(x0, y0, x0 + 1, y1, 0x1AFFFFFF);
        renderer.fill(x1 - 1, y0, x1, y1, 0x1AFFFFFF);
        renderer.fill(x0, y1 - 1, x1, y1, 0x1AFFFFFF);

        if (glyphs.isEmpty())
        {
            renderer.text(Component.translatable("sky_bubble.emoji_panel.empty").getString(), x0 + 6, y0 + 8, 0xAAAAAA, false);
            return;
        }

        this.hoveredIndex = -1;
        int gridLeft = x0 + CELL_PAD;
        int gridTop  = y0 + CELL_PAD + 3 - (int) scroll;

        renderer.enableScissor(x0, y0 + 3, x1, y1);

        for (int i = 0; i < glyphs.size(); i++)
        {
            int col = i % columns;
            int row = i / columns;

            int cellX = gridLeft + col * (CELL_SIZE + CELL_PAD);
            int cellY = gridTop + row * (CELL_SIZE + CELL_PAD);

            if (cellY + CELL_SIZE < y0 || cellY > y1)
            {
                continue;
            }

            boolean hovered = mouseX >= cellX && mouseX < cellX + CELL_SIZE && mouseY >= Math.max(cellY, y0) && mouseY < Math.min(cellY + CELL_SIZE, y1) && mouseX >= x0 && mouseX < x1 - 4;

            if (hovered)
            {
                this.hoveredIndex = i;
                renderer.fill(cellX - 1, cellY - 1, cellX + CELL_SIZE + 1, cellY + CELL_SIZE + 1, 0x33FFFFFF);
            }

            EmojiGlyph glyph = glyphs.get(i);
            int gx = cellX + (CELL_SIZE - GLYPH_SIZE) / 2;
            int gy = cellY + (CELL_SIZE - GLYPH_SIZE) / 2;

            renderer.blitTexture(glyph, gx, gy, GLYPH_SIZE);
        }

        renderer.disableScissor();

        int maxScroll = maxScroll();
        if (maxScroll > 0)
        {
            int trackH = this.getHeight() - 6;
            int thumbH = Math.max(10, trackH * this.getHeight() / (this.getHeight() + maxScroll));
            int thumbY = y0 + 4 + (int) ((trackH - thumbH) * (scroll / maxScroll));
            int sbX = x1 - 4 - 2;
            renderer.fill(sbX, thumbY, sbX + 4, thumbY + thumbH, 0x66FFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (!this.visible)
        {
            return false;
        }

        if (!this.isMouseOver(event.x(), event.y()))
        {
            return false;
        }

        if (event.buttonInfo().button() == 0 && hoveredIndex >= 0 && hoveredIndex < glyphs.size())
        {
            EmojiGlyph glyph = glyphs.get(hoveredIndex);
            String text = codepointsToString(glyph.codepoints);

            if (onEmojiPicked != null)
            {
                onEmojiPicked.accept(text);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        if (!this.visible || !this.isMouseOver(x, y))
        {
            return false;
        }

        int maxScroll = maxScroll();
        if (maxScroll <= 0)
        {
            return true;
        }

        this.scroll -= scrollY * (CELL_SIZE + CELL_PAD);
        this.scroll = Math.max(0, Math.min(maxScroll, this.scroll));
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return this.visible && mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    private static String codepointsToString(int[] codepoints)
    {
        StringBuilder sb = new StringBuilder();
        for (int cp : codepoints)
        {
            sb.appendCodePoint(cp);
        }
        return sb.toString();
    }
}