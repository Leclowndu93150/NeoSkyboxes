package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.Camera;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import org.joml.Matrix4f;


public class SquareTexturedSkybox extends TexturedSkybox {
    public static Codec<SquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            Textures.CODEC.fieldOf("textures").forGetter(s -> s.textures)
    ).apply(instance, SquareTexturedSkybox::new));
    public Textures textures;

    public SquareTexturedSkybox() {
    }

    public SquareTexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, Textures textures) {
        super(properties, conditions, decorations, blend);
        this.textures = textures;
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.SQUARE_TEXTURED_SKYBOX.get();
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, PoseStack matrices, float tickDelta, Camera camera, boolean thickFog) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        for (int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            Texture tex = this.textures.byId(i);
            RenderSystem.setShaderTexture(0, tex.getTextureId());
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            matrices.pushPose();

            if (i == 1) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            } else if (i == 2) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            } else if (i == 3) {
                matrices.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            } else if (i == 4) {
                matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                matrices.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
            } else if (i == 5) {
                matrices.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                matrices.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            }

            Matrix4f matrix4f = matrices.last().pose();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(tex.getMinU(), tex.getMinV()).endVertex();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(tex.getMinU(), tex.getMaxV()).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(tex.getMaxU(), tex.getMaxV()).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(tex.getMaxU(), tex.getMinV()).endVertex();
            matrices.popPose();
            BufferUploader.drawWithShader(bufferBuilder.end());
        }
    }
}