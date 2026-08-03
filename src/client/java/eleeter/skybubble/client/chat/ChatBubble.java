package eleeter.skybubble.client.chat;

import eleeter.elfontlib.font.Font;
import eleeter.skybubble.client.graphics.WorldTextInstance;

public class ChatBubble
{

    public static final float LIFETIME = 6.0f;
    public static final float FADE_IN_TIME = 0.3f;
    public static final float FADE_OUT_TIME = 0.5f;

    public static final float PAD_H = 400f;
    public static final float PAD_V = 300f;
    public static final float CORNER_R = 300f;
    public static final int CORNER_SEGS = 32;

    public final String originalText;
    public final WorldTextInstance textInstance;

    public final float textMinX, textMinY, textMaxX, textMaxY;

    /**
     * Total seconds alive.
     */
    public float age = 0f;

    public float animAlpha = 0f;

    private static final int MAX_CHARS_PER_LINE = 34;


    private static String wrapText(String message)
    {
        String[] words = message.split(" ", -1);
        StringBuilder result = new StringBuilder();
        int lineLen = 0;
        for (String word : words)
        {
            while (word.length() > MAX_CHARS_PER_LINE)
            {
                int space = MAX_CHARS_PER_LINE - lineLen;
                if (lineLen > 0 && space <= 0)
                {
                    result.append('\n');
                    lineLen = 0;
                    space = MAX_CHARS_PER_LINE;
                } else if (lineLen > 0)
                {
                    result.append(' ');
                    lineLen++;
                    space = MAX_CHARS_PER_LINE - lineLen;
                }
                result.append(word, 0, space);
                word = word.substring(space);
                lineLen += space;
                if (lineLen >= MAX_CHARS_PER_LINE)
                {
                    result.append('\n');
                    lineLen = 0;
                }
            }
            if (word.isEmpty())
            {
                continue;
            }

            if (lineLen > 0 && lineLen + 1 + word.length() > MAX_CHARS_PER_LINE)
            {
                result.append('\n');
                lineLen = 0;
            } else if (lineLen > 0) result.append(' ');
            lineLen++;
        }
        result.append(word);
        lineLen += word.length();
    }
        return result.toString();
}

public ChatBubble(String message, Font font)
{
    this.originalText = message;
    this.textInstance = new WorldTextInstance(wrapText(message), font, 1000f);

    float[] v = textInstance.mesh.vertices;
    float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
    for (int i = 0; i < v.length; i += 5)
    {
        float vx = v[i], vy = v[i + 1];
        if (vx < minX) minX = vx;
        if (vy < minY) minY = vy;
        if (vx > maxX) maxX = vx;
        if (vy > maxY) maxY = vy;
    }
    this.textMinX = (v.length > 0) ? minX : 0f;
    this.textMinY = (v.length > 0) ? minY : 0f;
    this.textMaxX = (v.length > 0) ? maxX : 0f;
    this.textMaxY = (v.length > 0) ? maxY : 0f;
}


public boolean isDead()
{
    return age >= LIFETIME;
}


public float boxMinX()
{
    return textMinX - PAD_H;
}

public float boxMaxX()
{
    return textMaxX + PAD_H;
}

public float boxMinY()
{
    return textMinY - PAD_V;
}

public float boxMaxY()
{
    return textMaxY + PAD_V;
}

public float boxWidth()
{
    return boxMaxX() - boxMinX();
}

public float boxHeight()
{
    return boxMaxY() - boxMinY();
}


public void updateAnimation()
{
    float remaining = LIFETIME - age;
    float fadeIn = Math.min(age / FADE_IN_TIME, 1f);
    float fadeOut = Math.min(remaining / FADE_OUT_TIME, 1f);
    animAlpha = fadeIn * fadeOut;
}
}
