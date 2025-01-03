package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.Properties;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;


public class EndSkybox extends AbstractSkybox {
    public static Codec<EndSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations)
    ).apply(instance, EndSkybox::new));

    public EndSkybox(Properties properties, Conditions conditions, Decorations decorations) {
        super(properties, conditions, decorations);
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.MONO_COLOR_SKYBOX.get();
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog) {
        Minecraft client = Minecraft.getInstance();
        assert client.level != null;

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, WorldRendererAccess.getEndSky());
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (int i = 0; i < 6; ++i) {
            matrices.pushPose();
            if (i == 1) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                matrices.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = matrices.last().pose();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, (int) (255 * this.alpha)).endVertex();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, (int) (255 * this.alpha)).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, (int) (255 * this.alpha)).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, (int) (255 * this.alpha)).endVertex();
            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        this.renderDecorations(worldRendererAccess, matrices, projectionMatrix, tickDelta, bufferBuilder, this.alpha);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}