package eleeter.skybubble.client.graphics;

import eleeter.skybubble.client.chat.ChatBubble;
import eleeter.skybubble.client.render.TextRenderer;
import org.joml.Matrix4f;

public class DrawText
{
    private static final float TEXT_R = 0.12f, TEXT_G = 0.12f, TEXT_B = 0.14f;


    public static void Text(Matrix4f mv, Matrix4f proj, ChatBubble bubble, float alpha, TextRenderer textRenderer)
    {
        textRenderer.draw(proj, mv, bubble.textInstance.mesh, TEXT_R, TEXT_G, TEXT_B, alpha);
    }
}
