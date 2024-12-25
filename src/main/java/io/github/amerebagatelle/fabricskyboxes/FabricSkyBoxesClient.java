package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.config.FabricSkyBoxesConfig;
import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceListener;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.LegacyDeserializer;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FabricSkyBoxesClient.MODID)
public class FabricSkyBoxesClient {
    public static final String MODID = "forgeskyboxes";
    private static Logger LOGGER;
    private static FabricSkyBoxesConfig CONFIG;

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger("ForgeSkyboxes");
        }
        return LOGGER;
    }

    public static FabricSkyBoxesConfig config() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    private static FabricSkyBoxesConfig loadConfig() {
        return FabricSkyBoxesConfig.load(FMLPaths.CONFIGDIR.get().resolve("neoforgeskyboxes-config.json").toFile());
    }

    public FabricSkyBoxesClient(IEventBus bus) {

        SkyboxType.SKYBOX_TYPES.register(bus);
        LegacyDeserializer.DESERIALIZER.register(bus);

        bus.addListener(this::onInitializeClient);
        bus.addListener(this::registerBindings);

        NeoForge.EVENT_BUS.register(this);
    }

    public void onInitializeClient(FMLClientSetupEvent event) {
        IEventBus bus = NeoForge.EVENT_BUS;
        SkyboxType.SKYBOX_TYPES.register(bus);
        SkyboxManager.getInstance().setEnabled(config().generalSettings.enable);

        ReloadableResourceManager resourceManager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        resourceManager.registerReloadListener(new SkyboxResourceListener());

        NeoForge.EVENT_BUS.register(SkyboxManager.getInstance());
        NeoForge.EVENT_BUS.register(config().getKeyBinding());

        Minecraft.getInstance().reloadResourcePacks();
    }

    public void registerBindings(RegisterKeyMappingsEvent event) {
        FabricSkyBoxesConfig.KeyBindingImpl keyMappings = config().getKeyBinding();

        event.register(keyMappings.toggleFabricSkyBoxes);
        event.register(keyMappings.toggleSkyboxDebugHud);
    }
}