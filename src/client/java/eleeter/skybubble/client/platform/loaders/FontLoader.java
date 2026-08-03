package eleeter.skybubble.client.platform.loaders;

import eleeter.elfontlib.font.Font;
import eleeter.elfontlib.font.msdf.MsdfFont;
import eleeter.elfontlib.font.msdf.MsdfFontData;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class FontLoader
{
    private static final Identifier FONT_JSON = Identifier.fromNamespaceAndPath("sky-bubble", "elfont/atlas.json");

    public static Font loadFont(Minecraft client)
    {
        try
        {
            Optional<Resource> resource = client.getResourceManager().getResource(FONT_JSON);
            if (resource.isEmpty())
            {
                System.err.println("[sky-bubble] Could not find " + FONT_JSON + " - did you place atlas.json under assets/sky-bubble/elfont/?");
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8))
            {
                MsdfFontData fontData = new Gson().fromJson(reader, MsdfFontData.class);
                return new MsdfFont(fontData);
            }
        } catch (IOException e)
        {
            System.err.println("[sky-bubble] Failed to load MSDF font data:");
            e.printStackTrace();
            return null;
        }
    }
}