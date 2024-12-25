package io.github.amerebagatelle.fabricskyboxes.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStreamReader;
import java.util.Map;

public class SkyboxResourceListener implements ResourceManagerReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().setLenient().create();

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();

        // clear registered skyboxes on reload
        skyboxManager.clearSkyboxes();

        // load new skyboxes
        Map<ResourceLocation, Resource> resources = manager.listResources("sky", ResourceLocation -> ResourceLocation.getPath().endsWith(".json"));

        resources.forEach((ResourceLocation, resource) -> {
            try {
                JsonObject json = GSON.fromJson(new InputStreamReader(resource.open()), JsonObject.class);
                skyboxManager.addSkybox(ResourceLocation, json);
            } catch (Exception e) {
                FabricSkyBoxesClient.getLogger().error("Error reading skybox " + ResourceLocation.toString());
                e.printStackTrace();
            }
        });
    }
}