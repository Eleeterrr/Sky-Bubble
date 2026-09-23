package eleeter.skybubble.client.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RenderTestWidget extends AbstractWidget
{
    public RenderTestWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.literal("Render Test"));
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        int x0 = this.getX();
        int y0 = this.getY();
        int x1 = x0 + this.getWidth();
        int y1 = y0 + this.getHeight();

// --- base panel ---
        graphics.fill(x0, y0, x1, y1, 0xE6141417);              // dark translucent background

// --- subtle gradient accent bar at top ---
        graphics.fillGradient(x0, y0, x1, y0 + 3, 0xFF7C5CFF, 0xFF5865F2);

// --- outer border (1px, low-opacity) ---
        graphics.outline(x0, y0, this.getWidth(), this.getHeight(), 0x33FFFFFF);

// --- inner highlight line (gives a soft "lifted" edge look) ---
        graphics.fill(x0 + 1, y0 + 3, x1 - 1, y0 + 4, 0x14FFFFFF);   // top inner glow
        graphics.fill(x0 + 1, y0 + 4, x0 + 2, y1 - 1, 0x14FFFFFF);   // left inner glow

// --- bottom shadow line for depth ---
        graphics.fill(x0 + 1, y1 - 2, x1 - 1, y1 - 1, 0x33000000);

// --- corner accent dots (small modern touch, optional) ---
        graphics.fill(x0 + 6, y0 + 6, x0 + 8, y0 + 8, 0x665865F2);
        graphics.fill(x1 - 8, y0 + 6, x1 - 6, y0 + 8, 0x665865F2);

// --- content text ---
        graphics.text(Minecraft.getInstance().font, "Render test OK", x0 + 12, y0 + 16, 0xFFFFFFFF, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }
}