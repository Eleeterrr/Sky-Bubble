package eleeter.skybubble.client.platform.loaders;

import com.google.gson.Gson;
import eleeter.elfontlib.emoji.EmojiAtlasData;
import eleeter.elfontlib.emoji.EmojiFont;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


public class EmojiAtlasLoader
{
    private static final Identifier ATLAS_JSON = Identifier.fromNamespaceAndPath("sky-bubble", "emoji/atlas.json");

    public static final Identifier EMOJI_ATLAS_TEXTURE = Identifier.fromNamespaceAndPath("sky-bubble", "textures/font/emoji/atlas.png");
    public static EmojiAtlasData skybubble_lastAtlasData;

    public static EmojiFont loadEmojiFont(Minecraft client)
    {
        try
        {
            Optional<Resource> resource = client.getResourceManager().getResource(ATLAS_JSON);
            if (resource.isEmpty())
            {
                System.err.println("[sky-bubble] Could not find " + ATLAS_JSON);
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8))
            {
                EmojiAtlasData data = new Gson().fromJson(reader, EmojiAtlasData.class);
                skybubble_lastAtlasData = data;
                return new EmojiFont(data);
            }
        } catch (IOException e)
        {
            System.err.println("[sky-bubble] Failed to load emoji atlas data:");
            e.printStackTrace();
            return null;
        } catch (RuntimeException e)
        {
            System.err.println("[sky-bubble] Emoji atlas.json was malformed:");
            e.printStackTrace();
            return null;
        }
    }
}