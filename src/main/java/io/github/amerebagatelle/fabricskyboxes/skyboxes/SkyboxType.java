package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.IRegistryExtension;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkyboxType<T extends AbstractSkybox> {
    public static final ResourceKey<Registry<SkyboxType<?>>> SKYBOX_TYPE_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(FabricSkyBoxesClient.MODID, "skybox_type"));

    public static final DeferredRegister<SkyboxType<?>> SKYBOX_TYPES = DeferredRegister.create(SKYBOX_TYPE_KEY, FabricSkyBoxesClient.MODID);

    public static final Supplier<IRegistryExtension<SkyboxType<?>>> REGISTRY = SKYBOX_TYPES.makeRegistry(RegistryBuilder::create);

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<MonoColorSkybox>> MONO_COLOR_SKYBOX = SKYBOX_TYPES.register("monocolor",
            () -> SkyboxType.Builder.create(MonoColorSkybox.class, "monocolor")
                    .legacySupported()
                    .deserializer(LegacyDeserializer.MONO_COLOR_SKYBOX_DESERIALIZER.get())
                    .factory(MonoColorSkybox::new)
                    .add(2, MonoColorSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<OverworldSkybox>> OVERWORLD_SKYBOX = SKYBOX_TYPES.register("overworld",
            () -> SkyboxType.Builder.create(OverworldSkybox.class, "overworld")
                    .add(2, OverworldSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<EndSkybox>> END_SKYBOX = SKYBOX_TYPES.register("end",
            () -> SkyboxType.Builder.create(EndSkybox.class, "end")
                    .add(2, EndSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<SquareTexturedSkybox>> SQUARE_TEXTURED_SKYBOX = SKYBOX_TYPES.register("square_textured",
            () -> SkyboxType.Builder.create(SquareTexturedSkybox.class, "square-textured")
                    .deserializer(LegacyDeserializer.SQUARE_TEXTURED_SKYBOX_DESERIALIZER.get())
                    .legacySupported()
                    .factory(SquareTexturedSkybox::new)
                    .add(2, SquareTexturedSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<SingleSpriteSquareTexturedSkybox>> SINGLE_SPRITE_SQUARE_TEXTURED_SKYBOX = SKYBOX_TYPES.register("single_sprite_square_textured",
            () -> SkyboxType.Builder.create(SingleSpriteSquareTexturedSkybox.class, "single-sprite-square-textured")
                    .add(2, SingleSpriteSquareTexturedSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<AnimatedSquareTexturedSkybox>> ANIMATED_SQUARE_TEXTURED_SKYBOX = SKYBOX_TYPES.register("animated_square_textured",
            () -> SkyboxType.Builder.create(AnimatedSquareTexturedSkybox.class, "animated-square-textured")
                    .add(2, AnimatedSquareTexturedSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<SingleSpriteAnimatedSquareTexturedSkybox>> SINGLE_SPRITE_ANIMATED_SQUARE_TEXTURED_SKYBOX = SKYBOX_TYPES.register("single_sprite_animated_square_textured",
            () -> SkyboxType.Builder.create(SingleSpriteAnimatedSquareTexturedSkybox.class, "single-sprite-animated-square-textured")
                    .add(2, SingleSpriteAnimatedSquareTexturedSkybox.CODEC)
                    .build());

    public static final DeferredHolder<SkyboxType<?>, SkyboxType<MultiTextureSkybox>> MULTI_TEXTURE_SKYBOX = SKYBOX_TYPES.register("multi_texture",
            () -> SkyboxType.Builder.create(MultiTextureSkybox.class, "multi-texture")
                    .add(2, MultiTextureSkybox.CODEC)
                    .build());

    public static final Codec<ResourceLocation> SKYBOX_ID_CODEC = Codec.STRING.xmap(
            s -> !s.contains(":") ? ResourceLocation.fromNamespaceAndPath(FabricSkyBoxesClient.MODID, s.replace('-', '_'))
                    : ResourceLocation.withDefaultNamespace(s.replace('-', '_')),
            id -> id.getNamespace().equals(FabricSkyBoxesClient.MODID) ? id.getPath().replace('_', '-')
                    : id.toString().replace('_', '-')
    );

    private final BiMap<Integer, Codec<T>> codecBiMap;
    private final boolean legacySupported;
    private final String name;
    @Nullable
    private final Supplier<T> factory;
    @Nullable
    private final LegacyDeserializer<T> deserializer;

    private SkyboxType(BiMap<Integer, Codec<T>> codecBiMap, boolean legacySupported, String name,
                       @Nullable Supplier<T> factory, @Nullable LegacyDeserializer<T> deserializer) {
        this.codecBiMap = codecBiMap;
        this.legacySupported = legacySupported;
        this.name = name;
        this.factory = factory;
        this.deserializer = deserializer;
    }

    public static void register(IEventBus modEventBus) {
        SKYBOX_TYPES.register(modEventBus);
    }

    public String getName() {
        return this.name;
    }

    public boolean isLegacySupported() {
        return this.legacySupported;
    }

    @NotNull
    public T instantiate() {
        return Objects.requireNonNull(Objects.requireNonNull(this.factory, "Can't instantiate from a null factory").get());
    }

    @Nullable
    public LegacyDeserializer<T> getDeserializer() {
        return this.deserializer;
    }

    public ResourceLocation createId(String namespace) {
        return this.createIdFactory().apply(namespace);
    }

    public Function<String, ResourceLocation> createIdFactory() {
        return ns -> ResourceLocation.fromNamespaceAndPath(ns, this.getName().replace('-', '_'));
    }

    public Codec<T> getCodec(int schemaVersion) {
        return Objects.requireNonNull(this.codecBiMap.get(schemaVersion),
                String.format("Unsupported schema version '%d' for skybox type %s", schemaVersion, this.name));
    }


    public static class Builder<T extends AbstractSkybox> {
        private final ImmutableBiMap.Builder<Integer, Codec<T>> builder = ImmutableBiMap.builder();
        private String name;
        private boolean legacySupported = false;
        private Supplier<T> factory;
        private LegacyDeserializer<T> deserializer;

        private Builder() {
        }

        public static <S extends AbstractSkybox> Builder<S> create(@SuppressWarnings("unused") Class<S> clazz, String name) {
            Builder<S> builder = new Builder<>();
            builder.name = name;
            return builder;
        }

        public static <S extends AbstractSkybox> Builder<S> create(String name) {
            Builder<S> builder = new Builder<>();
            builder.name = name;
            return builder;
        }

        protected Builder<T> legacySupported() {
            this.legacySupported = true;
            return this;
        }

        protected Builder<T> factory(Supplier<T> factory) {
            this.factory = factory;
            return this;
        }

        protected Builder<T> deserializer(LegacyDeserializer<T> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public Builder<T> add(int schemaVersion, Codec<T> codec) {
            Preconditions.checkArgument(schemaVersion >= 2, "schema version was lesser than 2");
            Preconditions.checkNotNull(codec, "codec was null");
            this.builder.put(schemaVersion, codec);
            return this;
        }

        public SkyboxType<T> build() {
            if (this.legacySupported) {
                Preconditions.checkNotNull(this.factory, "factory was null");
                Preconditions.checkNotNull(this.deserializer, "deserializer was null");
            }
            return new SkyboxType<>(this.builder.build(), this.legacySupported, this.name, this.factory, this.deserializer);
        }

        /*public SkyboxType<T> buildAndRegister(String namespace) {
            return Registry.register(SkyboxType.REGISTRY, new ResourceLocation(namespace, this.name.replace('-', '_')), this.build());
        }*/
    }
}