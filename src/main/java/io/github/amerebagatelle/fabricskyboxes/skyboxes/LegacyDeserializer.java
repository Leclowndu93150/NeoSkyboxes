package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LegacyDeserializer<T extends AbstractSkybox> {
    public static final ResourceKey<Registry<LegacyDeserializer<? extends AbstractSkybox>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(FabricSkyBoxesClient.MODID, "legacy_skybox_deserializer"));

    public static final DeferredRegister<LegacyDeserializer<? extends AbstractSkybox>> DESERIALIZER =
            DeferredRegister.create(REGISTRY_KEY, FabricSkyBoxesClient.MODID);

    public static final DeferredHolder<LegacyDeserializer<? extends AbstractSkybox>, LegacyDeserializer<MonoColorSkybox>> MONO_COLOR_SKYBOX_DESERIALIZER =
            DESERIALIZER.register("mono_color_skybox_legacy_deserializer",
                    () -> new LegacyDeserializer<>(LegacyDeserializer::decodeMonoColor, MonoColorSkybox.class));

    public static final DeferredHolder<LegacyDeserializer<? extends AbstractSkybox>, LegacyDeserializer<SquareTexturedSkybox>> SQUARE_TEXTURED_SKYBOX_DESERIALIZER =
            DESERIALIZER.register("square_textured_skybox_legacy_deserializer",
                    () -> new LegacyDeserializer<>(LegacyDeserializer::decodeSquareTextured, SquareTexturedSkybox.class));

    private final BiConsumer<JsonObjectWrapper, AbstractSkybox> deserializer;

    private LegacyDeserializer(BiConsumer<JsonObjectWrapper, AbstractSkybox> deserializer, Class<T> clazz) {
        this.deserializer = deserializer;
    }

    private static void decodeSquareTextured(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        decodeSharedData(wrapper, skybox);
        ((SquareTexturedSkybox) skybox).rotation = new Rotation(true, new Vector3f(0f, 0f, 0f), new Vector3f(wrapper.getOptionalArrayFloat("axis", 0, 0), wrapper.getOptionalArrayFloat("axis", 1, 0), wrapper.getOptionalArrayFloat("axis", 2, 0)), new Vector3i(0, 0, 0), 0, 1, 0);
        ((SquareTexturedSkybox) skybox).blend = new Blend(wrapper.getOptionalBoolean("shouldBlend", false) ? "add" : "", Blender.DEFAULT);
        ((SquareTexturedSkybox) skybox).textures = new Textures(
                new Texture(wrapper.getJsonStringAsId("texture_north")),
                new Texture(wrapper.getJsonStringAsId("texture_south")),
                new Texture(wrapper.getJsonStringAsId("texture_east")),
                new Texture(wrapper.getJsonStringAsId("texture_west")),
                new Texture(wrapper.getJsonStringAsId("texture_top")),
                new Texture(wrapper.getJsonStringAsId("texture_bottom"))
        );
    }

    private static void decodeMonoColor(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        decodeSharedData(wrapper, skybox);
        ((MonoColorSkybox) skybox).color = new RGBA(wrapper.get("red").getAsFloat(), wrapper.get("green").getAsFloat(), wrapper.get("blue").getAsFloat());
    }

    private static void decodeSharedData(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        float maxAlpha = wrapper.getOptionalFloat("maxAlpha", 1f);
        skybox.properties = new Properties.Builder()
                .fade(new Fade(
                        wrapper.get("startFadeIn").getAsInt(),
                        wrapper.get("endFadeIn").getAsInt(),
                        wrapper.get("startFadeOut").getAsInt(),
                        wrapper.get("endFadeOut").getAsInt(),
                        false
                ))
                .maxAlpha(maxAlpha)
                .transitionInDuration((int) (maxAlpha / wrapper.getOptionalFloat("transitionSpeed", 0.05f)))
                .transitionOutDuration((int) (maxAlpha / wrapper.getOptionalFloat("transitionSpeed", 0.05f)))
                .changeFog(wrapper.getOptionalBoolean("changeFog", false))
                .fogColors(new RGBA(
                        wrapper.getOptionalFloat("fogRed", 0f),
                        wrapper.getOptionalFloat("fogGreen", 0f),
                        wrapper.getOptionalFloat("fogBlue", 0f)
                ))
                .build();
        // decorations
        skybox.decorations = Decorations.DEFAULT;
        // environment specifications
        JsonElement element;
        element = wrapper.getOptionalValue("weather").orElse(null);
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    skybox.conditions.getWeathers().add(Weather.fromString(jsonElement.getAsString()));
                }
            } else if (GsonHelper.isStringValue(element)) {
                skybox.conditions.getWeathers().add(Weather.fromString(element.getAsString()));
            }
        }
        element = wrapper.getOptionalValue("biomes").orElse(null);
        processIds(element, skybox.conditions.getBiomes());
        element = wrapper.getOptionalValue("dimensions").orElse(null);
        processIds(element, skybox.conditions.getWorlds());
        element = wrapper.getOptionalValue("heightRanges").orElse(null);
        if (element != null) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                JsonArray insideArray = jsonElement.getAsJsonArray();
                float low = insideArray.get(0).getAsFloat();
                float high = insideArray.get(1).getAsFloat();
                skybox.conditions.getYRanges().add(new MinMaxEntry(low, high));
            }
        }
    }

    private static void processIds(JsonElement element, List<ResourceLocation> list) {
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    list.add(ResourceLocation.withDefaultNamespace(jsonElement.getAsString()));
                }
            } else if (GsonHelper.isStringValue(element)) {
                list.add(ResourceLocation.withDefaultNamespace(element.getAsString()));
            }
        }
    }

    private static <T extends AbstractSkybox> LegacyDeserializer<T> register(LegacyDeserializer<T> deserializer, String name) {
        return DESERIALIZER.register(name, () -> deserializer).get();
    }

    public BiConsumer<JsonObjectWrapper, AbstractSkybox> getDeserializer() {
        return this.deserializer;
    }
}