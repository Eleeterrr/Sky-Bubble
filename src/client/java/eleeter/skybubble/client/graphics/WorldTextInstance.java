package eleeter.skybubble.client.graphics;

import eleeter.elfontlib.font.Font;
import eleeter.elfontlib.render.MeshData;
import eleeter.elfontlib.render.TextMeshGenerator;
import eleeter.elfontlib.shaping.SimpleTextShaper;
import eleeter.elfontlib.shaping.TextLayout;
import eleeter.elfontlib.shaping.TextShaper;

public class WorldTextInstance
{
    public MeshData mesh;

    public WorldTextInstance(String text, Font font, float fontSize)
    {
        updateMesh(text, font, fontSize);
    }

    public void updateMesh(String text, Font font, float fontSize)
    {
        TextShaper shaper = new SimpleTextShaper();
        TextLayout layout = shaper.shape(text, font, fontSize);
        this.mesh = TextMeshGenerator.generate(layout, font);
    }
}