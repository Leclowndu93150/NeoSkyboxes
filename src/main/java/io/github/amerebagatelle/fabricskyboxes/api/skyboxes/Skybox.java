package io.github.amerebagatelle.fabricskyboxes.api.skyboxes;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public interface Skybox {

    /**
     * For purposes on which order the skyboxes will be rendered.
     * The default priority will be set to 0.
     *
     * @return The priority of the skybox.
     */
    default int getPriority() {
        return 0;
    }

    /**
     * The main render method for a skybox.
     * Override this if you are creating a skybox from this one.
     * This will be process if {@link Skybox#isActive()}
     *
     * @param worldRendererAccess Access to the worldRenderer as skyboxes often require it.
     * @param tickDelta           The current tick delta.
     * @param camera              The player camera.
     * @param thickFog            Is using thick fog.
     * @param fogCallback         The fogCallback to run.
     */
    void render(WorldRendererAccess worldRendererAccess, PoseStack matrixStack, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog);

    /**
     * The main thread for a skybox
     * Override this if you need to process conditions for the skybox.
     * This will be process regardless the state of {@link Skybox#isActive()}
     *
     * @param clientWorld The client's world
     */
    void tick(ClientLevel clientWorld);

    /**
     * Gets the state of the skybox.
     *
     * @return State of the skybox.
     */
    boolean isActive();
}
