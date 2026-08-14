package eleeter.skybubble.client.platform;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import eleeter.elfontlib.render.MeshData;
import eleeter.skybubble.client.render.TextRenderer;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;


public class MinecraftMsdfTextRenderer implements TextRenderer
{

    private static final Identifier FONT_ATLAS = Identifier.fromNamespaceAndPath("sky-bubble", "textures/font/atlas.png");
    private boolean ready = true;


    private static final RenderPipeline PIPELINE = RenderPipeline.builder()
            .withLocation(Identifier.fromNamespaceAndPath("sky-bubble", "pipeline/msdf_text"))
            .withVertexShader(Identifier.fromNamespaceAndPath("sky-bubble", "core/msdf"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("sky-bubble", "core/msdf"))
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
            .build();


    public void setShader(Object shaderProgram)
    {
    }

    @Override
    public boolean isReady()
    {
        return ready;
    }

    @Override
    public void draw(Matrix4f projection, Matrix4f modelView, MeshData mesh, float r, float g, float b, float a)
    {
        float[] v = mesh.vertices;
        int[] indices = mesh.indices;
        int vertexCount = (indices != null && indices.length > 0) ? indices.length : (v.length / 5);

        if (vertexCount == 0)
        {
            return;
        }

        int colorArgb = ARGB.colorFromFloat(a, r, g, b);

        ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize() * vertexCount);
        GpuBuffer vertexBuffer = null;

        try
        {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
            VertexConsumer buf = bufferBuilder;

            if (indices != null && indices.length > 0)
            {
                for (int idx : indices)
                {
                    int base = idx * 5;
                    buf.addVertex(modelView, v[base], v[base + 1], v[base + 2]).setColor(colorArgb).setUv(v[base + 3], v[base + 4]);
                }
            }
            else
            {
                for (int i = 0; i < v.length; i += 5)
                {
                    buf.addVertex(modelView, v[i], v[i + 1], v[i + 2]).setColor(colorArgb).setUv(v[i + 3], v[i + 4]);
                }
            }

            vertexBuffer = buildGpuBuffer(bufferBuilder, byteBufferBuilder, "sky-bubble msdf text vertex buffer");
        }
        finally
        {
            byteBufferBuilder.close();
        }

        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();
        AbstractTexture fontAtlas = textureManager.getTexture(FONT_ATLAS);

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
        RenderPass renderPass = openRenderPass("sky-bubble msdf text");

        try
        {
            renderPass.setPipeline(PIPELINE);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", fontAtlas.getTextureView(), fontAtlas.getSampler());
            renderPass.setVertexBuffer(0, vertexBuffer.slice());
            renderPass.draw(vertexCount, 1, 0, 0);
        } finally
        {
            renderPass.close();
            vertexBuffer.close();
        }
    }

    private static GpuBuffer buildGpuBuffer(BufferBuilder bufferBuilder, ByteBufferBuilder byteBufferBuilder, String label)
    {
        /* My elfont library uses the same class name "MeshData", so we can't import both classes at once */
        com.mojang.blaze3d.vertex.MeshData meshData = bufferBuilder.buildOrThrow();
        try
        {
            return RenderSystem.getDevice().createBuffer(() -> label, GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer());
        }
        finally
        {
            meshData.close();
        }
    }

    private static RenderPass openRenderPass(String label)
    {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainRenderTarget = minecraft.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainRenderTarget.getColorTextureView();
        GpuTextureView depthTexture = mainRenderTarget.getDepthTextureView();

        return RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> label, colorTexture, Optional.empty(), depthTexture, OptionalDouble.empty());
    }
}