package eleeter.skybubble.client.chat.render;

import eleeter.skybubble.client.graphics.Draw2D;
import eleeter.skybubble.client.graphics.DrawText;
import eleeter.skybubble.client.render.ColoredGeometryRenderer;
import eleeter.skybubble.client.render.Vec3f;
import eleeter.skybubble.client.render.PlayerLocator;
import eleeter.skybubble.client.render.TextRenderer;
import eleeter.skybubble.client.render.TransformStack;
import eleeter.skybubble.client.render.WorldRenderRegistrar;
import eleeter.skybubble.client.chat.ChatBubble;
import eleeter.skybubble.client.chat.ChatBubbleManager;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;


public class ChatBubbleRenderer
{

    private static final float TEXT_SCALE = 2e-4f;
    private static final float NAMETAG_ATTACH_OFFSET = 0.5f;
    private static final float NAMETAG_GLYPH_HEIGHT = 9.0f * 0.025f; /* NAMETAG Height, btw */
    private static final float NAMETAG_EXTRA_GAP = 0.05f;
    private static final float BASE_STACK_OFFSET = 0.5f;


    public static void register(WorldRenderRegistrar registrar, PlayerLocator playerLocator, ColoredGeometryRenderer coloredRenderer, TextRenderer textRenderer)
    {
        registrar.onFrameAfterTranslucent(ctx ->
        {
            if (!textRenderer.isReady())
            {
                return;
            }
            Map<UUID, ArrayDeque<ChatBubble>> map = ChatBubbleManager.getBubbleMap();

            if (map.isEmpty())
            {
                return;
            }


            Vec3f cam = ctx.cameraPos();
            Quaternionf camRot = ctx.cameraRotation();

            TransformStack matrices = ctx.transformStack();
            Matrix4f projection = ctx.projectionMatrix();

            float delta = ctx.tickDelta();

            for (Map.Entry<UUID, ArrayDeque<ChatBubble>> entry : map.entrySet())
            {
                UUID uuid = entry.getKey();
                ArrayDeque<ChatBubble> deque = entry.getValue();
                int total = deque.size();

                float[] offsets = new float[total];
                float currentOffset = BASE_STACK_OFFSET;

                Iterator<ChatBubble> descIt = deque.descendingIterator();
                int idx = total - 1;

                while (descIt.hasNext())
                {
                    ChatBubble bubble = descIt.next();
                    offsets[idx] = currentOffset;

                    float bubbleHeightBlocks = bubble.boxHeight() * TEXT_SCALE;
                    currentOffset += bubbleHeightBlocks + 0.25f;
                    idx--;
                }

                int stackIndex = 0;
                for (ChatBubble bubble : deque)
                {
                    if (bubble.animAlpha > 0.002f)
                    {
                        renderBubble(bubble, uuid, offsets[stackIndex], stackIndex == total - 1, delta, cam, camRot, matrices, projection, playerLocator, coloredRenderer, textRenderer);
                    }
                    stackIndex++;
                }
            }
        });
    }


    private static void renderBubble(ChatBubble bubble, UUID uuid, float stackOffset, boolean isClosestToPlayer, float tickDelta, Vec3f cam, Quaternionf camRot, TransformStack matrix, Matrix4f projection, PlayerLocator playerLocator, ColoredGeometryRenderer coloredRenderer, TextRenderer textRenderer)
    {

        if (!playerLocator.exists(uuid))
        {
            return;
        }

        if (playerLocator.isLocalPlayerFirstPerson(uuid))
        {
            return;
        }

        Vec3f lerped = playerLocator.getLerpedPosition(uuid, tickDelta);


        float bbHeight = playerLocator.getNameTagAnchorHeight(uuid);
        float nameTagTopEdge = bbHeight + NAMETAG_ATTACH_OFFSET + NAMETAG_GLYPH_HEIGHT;
        double py = lerped.y() + (double) (nameTagTopEdge + NAMETAG_EXTRA_GAP + stackOffset - BASE_STACK_OFFSET);

        float cx = (bubble.boxMinX() + bubble.boxMaxX()) * 0.5f;
        float anchorY = bubble.boxMinY();

        matrix.push();


        Vector3f worldOffset = new Vector3f((float) (lerped.x() - cam.x()), (float) (py - cam.y()), (float) (lerped.z() - cam.z()));
        camRot.transform(worldOffset);

        matrix.translate(worldOffset.x, worldOffset.y, worldOffset.z);


        float s = TEXT_SCALE;
        matrix.scale(s, s, s);

        float alpha = bubble.animAlpha;

        matrix.translate(-cx, -anchorY, 0f);

        Matrix4f mv = matrix.peekPositionMatrix();

        Draw2D.drawBackground(mv, bubble, alpha, isClosestToPlayer, coloredRenderer);


        matrix.push();
        matrix.translate(0.0, 0.0, 0.5);
        Matrix4f mvText = matrix.peekPositionMatrix();
        DrawText.Text(mvText, projection, bubble, alpha, textRenderer);
        matrix.pop();

        matrix.pop();
    }




}