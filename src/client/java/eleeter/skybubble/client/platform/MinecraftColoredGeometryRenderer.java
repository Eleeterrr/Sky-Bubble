package eleeter.skybubble.client.platform;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import eleeter.skybubble.client.render.ColoredGeometryRenderer;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;


public class MinecraftColoredGeometryRenderer implements ColoredGeometryRenderer
{

    private static final RenderPipeline PIPELINE = RenderPipelines.register(RenderPipeline.builder()
            .withLocation(Identifier.fromNamespaceAndPath("sky-bubble", "pipeline/colored_geometry"))
            .withVertexShader(Identifier.fromNamespaceAndPath("sky-bubble", "core/position_color"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("sky-bubble", "core/position_color"))
            .withBindGroupLayout(BindGroupLayouts.GLOBALS)
            .withBindGroupLayout(BindGroupLayouts.PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
            .build());

    @Override
    public void draw(Matrix4f transform, float[] xyzrgba)
    {
        int vertexCount = xyzrgba.length / 7;

        if (vertexCount == 0)
        {
            return;
        }

        ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR.getVertexSize() * vertexCount);
        GpuBuffer vertexBuffer = null;

        try
        {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, PrimitiveTopology.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
            VertexConsumer buf = bufferBuilder;

            for (int i = 0; i < xyzrgba.length; i += 7)
            {
                buf.addVertex(transform, xyzrgba[i], xyzrgba[i + 1], xyzrgba[i + 2]).setColor(xyzrgba[i + 3], xyzrgba[i + 4], xyzrgba[i + 5], xyzrgba[i + 6]);
            }

            vertexBuffer = buildGpuBuffer(bufferBuilder, byteBufferBuilder, "sky-bubble colored geometry vertex buffer");
        } finally
        {
            byteBufferBuilder.close();
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
        RenderPass renderPass = openRenderPass("sky-bubble colored geometry");

        try
        {
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(PIPELINE));            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
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
        MeshData meshData = bufferBuilder.buildOrThrow();
        try
        {
            return RenderSystem.getDevice().createBuffer(() -> label, GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer());
        } finally
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