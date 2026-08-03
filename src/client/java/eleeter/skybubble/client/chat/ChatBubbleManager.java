package eleeter.skybubble.client.chat;

import eleeter.elfontlib.font.Font;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ChatBubbleManager
{

    /**
     * Max bubbles stacked above a single player (oldest drops when exceeded).
     */
    private static final int MAX_BUBBLES_PER_PLAYER = 3;

    /**
     * Extra vertical gap between stacked bubbles (blocks).
     */
    public static final float BUBBLE_STACK_GAP = 0.55f;

    private static Font font;


    private static final Map<UUID, ArrayDeque<ChatBubble>> dumbbubbleMap = new LinkedHashMap<>();

    public static void init(Font loadedFont)
    {
        font = loadedFont;
    }

    public static void onChatMessage(UUID speakerUuid, String message)
    {
        if (font == null)
        {
            return;
        }

        if (message == null || message.isBlank())
        {
            return;
        }


        ArrayDeque<ChatBubble> deque = dumbbubbleMap.computeIfAbsent(speakerUuid, k -> new ArrayDeque<>());
        if (!deque.isEmpty())
        {
            ChatBubble top = deque.peekLast();
            if (top.originalText.equals(message) && top.age < 0.25f)
            {
                return;
            }
        }

        ChatBubble bubble = new ChatBubble(message, font);

        while (deque.size() >= MAX_BUBBLES_PER_PLAYER)
        {
            deque.pollFirst();
        }
        deque.addLast(bubble);
    }

    public static void tick(float deltaSec)
    {
        if (dumbbubbleMap.isEmpty())
        {
            return;
        }

        Iterator<Map.Entry<UUID, ArrayDeque<ChatBubble>>> playerIt = dumbbubbleMap.entrySet().iterator();
        while (playerIt.hasNext())
        {
            Map.Entry<UUID, ArrayDeque<ChatBubble>> entry = playerIt.next();
            ArrayDeque<ChatBubble> deque = entry.getValue();

            deque.removeIf(b ->
            {
                b.age += deltaSec;
                b.updateAnimation();
                return b.isDead();
            });

            if (deque.isEmpty())
            {
                playerIt.remove();
            }
        }
    }

    public static Map<UUID, ArrayDeque<ChatBubble>> getBubbleMap()
    {
        return Collections.unmodifiableMap(dumbbubbleMap);
    }

}
