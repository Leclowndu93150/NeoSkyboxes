package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.FSBSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import net.minecraft.client.renderer.FogRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(FogRenderer.class)
public class SunSkyColorMixin {
    @ModifyConstant(
            method = "setupColor",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;")),
            constant = @Constant(intValue = 4, ordinal = 0)
    )
    private static int renderSkyColor(int original) {
        Skybox skybox = SkyboxManager.getInstance().getCurrentSkybox();
        if (skybox instanceof FSBSkybox fsbSkybox) {
            if (!fsbSkybox.getProperties().isRenderSunSkyTint()) {
                return Integer.MAX_VALUE;
            }
        }
        return original;
    }
}