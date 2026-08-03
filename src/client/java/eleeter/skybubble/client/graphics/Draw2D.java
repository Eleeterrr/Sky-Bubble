package eleeter.skybubble.client.graphics;

import eleeter.skybubble.client.chat.ChatBubble;
import eleeter.skybubble.client.render.ColoredGeometryRenderer;
import org.joml.Matrix4f;

public class Draw2D
{
    private static final float BG_R = 0.94f, BG_G = 0.94f, BG_B = 0.94f;
    private static final float BG_BASE_ALPHA = 1.0f;


    public static class VertexList
    {
        public float[] data = new float[256];
        public int size = 0;

        public void add(float x, float y, float z, float r, float g, float b, float a)
        {
            if (size + 7 > data.length)
            {
                data = java.util.Arrays.copyOf(data, Math.max(data.length * 2, size + 7));
            }
            data[size++] = x;
            data[size++] = y;
            data[size++] = z;
            data[size++] = r;
            data[size++] = g;
            data[size++] = b;
            data[size++] = a;
        }

        public float[] toArray()
        {
            return java.util.Arrays.copyOf(data, size);
        }
    }

    public static void drawBackground(Matrix4f mv, ChatBubble bubble, float alpha, boolean drawTail, ColoredGeometryRenderer coloredRenderer)
    {
        float x0 = bubble.boxMinX();
        float x1 = bubble.boxMaxX();
        float y0 = bubble.boxMinY();
        float y1 = bubble.boxMaxY();
        float r = ChatBubble.CORNER_R;
        int seg = ChatBubble.CORNER_SEGS;

        float sx = 18f, sy = 18f, sz = -0.5f;

        VertexList shadow = new VertexList();
        Draw2D.fillRoundedRect(shadow, x0 + sx, y0 + sy, x1 + sx, y1 + sy, r, seg,
                0f, 0f, 0f, alpha * 0.3f, sz);
        if (drawTail)
        {
            Draw2D.drawTailTriangle(shadow, x0, x1, y0, 0f, 0f, 0f, alpha * 0.3f, sz, sx, sy);
        }
        coloredRenderer.draw(mv, shadow.toArray());


        VertexList background = new VertexList();
        Draw2D.fillRoundedRect(background, x0, y0, x1, y1, r, seg, BG_R, BG_G, BG_B, alpha * BG_BASE_ALPHA, 0f);
        if (drawTail)
        {
            Draw2D.drawTailTriangle(background, x0, x1, y0, BG_R, BG_G, BG_B, alpha * BG_BASE_ALPHA, 0f, 0f, 0f);
        }
        coloredRenderer.draw(mv, background.toArray());
    }

    public static void fillRoundedRect(VertexList out, float x0, float y0, float x1, float y1, float r, int seg, float red, float green, float blue, float alpha, float z)
    {
        float cx0 = x0 + r, cx1 = x1 - r;
        float cy0 = y0 + r, cy1 = y1 - r;

        /* Centre rectangle */
        addQuad(out, cx0, cy0, cx1, cy1, red, green, blue, alpha, z);

        /* Top / bottom edge strips */
        addQuad(out, cx0, cy1, cx1, y1, red, green, blue, alpha, z);

        addQuad(out, cx0, y0, cx1, cy0, red, green, blue, alpha, z);

        /* Left / right edge strips */
        addQuad(out, x0, cy0, cx0, cy1, red, green, blue, alpha, z);
        addQuad(out, cx1, cy0, x1, cy1, red, green, blue, alpha, z);


        addCornerFan(out, x0 + r, y0 + r, r, seg, (float) Math.PI, red, green, blue, alpha, z); /* BL */
        addCornerFan(out, x1 - r, y0 + r, r, seg, (float) (Math.PI * 1.5), red, green, blue, alpha, z); /* BR */
        addCornerFan(out, x1 - r, y1 - r, r, seg, 0f, red, green, blue, alpha, z); /* TR */
        addCornerFan(out, x0 + r, y1 - r, r, seg, (float) (Math.PI * 0.5), red, green, blue, alpha, z); /* TL */
    }

    public static void drawTailTriangle(VertexList out, float x0, float x1, float y0, float red, float green, float blue, float alpha, float z, float offX, float offY)
    {
        float midX = (x0 + x1) * 0.5f + offX;
        float tipY = y0 - 150F + offY;
        float baseY = y0 + offY;

        out.add(midX - 150F, baseY, z, red, green, blue, alpha);
        out.add(midX + 200F, baseY, z, red, green, blue, alpha);
        out.add(midX, tipY, z, red, green, blue, alpha);
    }


    public static void addCornerFan(VertexList out, float cx, float cy, float r, int seg, float startAngle, float red, float green, float blue, float alpha, float z)
    {
        float step = (float) (Math.PI * 0.5) / seg;
        for (int i = 0; i < seg; i++)
        {
            float a0 = startAngle + i * step;
            float a1 = a0 + step;
            out.add(cx, cy, z, red, green, blue, alpha);
            out.add(cx + (float) Math.cos(a0) * r, cy + (float) Math.sin(a0) * r, z, red, green, blue, alpha);
            out.add(cx + (float) Math.cos(a1) * r, cy + (float) Math.sin(a1) * r, z, red, green, blue, alpha);
        }
    }

    public static void addQuad(VertexList out, float x0, float y0, float x1, float y1, float r, float g, float b, float a, float z)
    {
        out.add(x0, y0, z, r, g, b, a);
        out.add(x1, y0, z, r, g, b, a);
        out.add(x1, y1, z, r, g, b, a);
        out.add(x0, y0, z, r, g, b, a);
        out.add(x1, y1, z, r, g, b, a);
        out.add(x0, y1, z, r, g, b, a);
    }

}
