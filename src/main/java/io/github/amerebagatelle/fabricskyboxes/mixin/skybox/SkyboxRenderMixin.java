package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.material.FogType;

import com.mojang.blaze3d.vertex.PoseStack;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class SkyboxRenderMixin {

    @Invoker("doesMobEffectBlockSky")
    protected abstract boolean doesMobEffectBlockSky(Camera camera);

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled() && !skyboxManager.getActiveSkyboxes().isEmpty()) {
            skyFogSetup.run();
            FogType fogType = camera.getFluidInCamera();
            boolean renderSky = !FabricSkyBoxesClient.config().generalSettings.keepVanillaBehaviour || (!bl && fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA && fogType != FogType.WATER && !this.doesMobEffectBlockSky(camera));
            if (renderSky) {
                skyboxManager.renderSkyboxes((WorldRendererAccess) this, frustumMatrix, projectionMatrix, partialTick, camera, isFoggy);
            }
            ci.cancel();
        }
    }
}