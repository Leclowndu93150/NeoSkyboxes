package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Properties {
    public static final Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(Properties::getPriority),
            Fade.CODEC.fieldOf("fade").forGetter(Properties::getFade),
            Utils.getClampedFloat(0F, 1.0F).optionalFieldOf("minAlpha", 0F).forGetter(Properties::getMaxAlpha),
            Utils.getClampedFloat(0F, 1.0F).optionalFieldOf("maxAlpha", 1.0F).forGetter(Properties::getMaxAlpha),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).optionalFieldOf("transitionInDuration", 20).forGetter(Properties::getTransitionInDuration),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).optionalFieldOf("transitionOutDuration", 20).forGetter(Properties::getTransitionOutDuration),
            Codec.BOOL.optionalFieldOf("changeFog", false).forGetter(Properties::isChangeFog),
            Codec.BOOL.optionalFieldOf("changeFogDensity", false).forGetter(Properties::isChangeFogDensity),
            RGBA.CODEC.optionalFieldOf("fogColors", RGBA.DEFAULT).forGetter(Properties::getFogColors),
            Codec.BOOL.optionalFieldOf("sunSkyTint", true).forGetter(Properties::isRenderSunSkyTint),
            Codec.BOOL.optionalFieldOf("inThickFog", true).forGetter(Properties::isRenderInThickFog),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.DEFAULT).forGetter(Properties::getRotation)
    ).apply(instance, Properties::new));

    public static final Properties DEFAULT = new Properties(0, Fade.DEFAULT, 0F, 1F, 20, 20, false, false, RGBA.DEFAULT, true, true, Rotation.DEFAULT);

    private final int priority;
    private final Fade fade;
    private final float minAlpha;
    private final float maxAlpha;
    private final int transitionInDuration;
    private final int transitionOutDuration;
    private final boolean changeFog;
    private final boolean changeFogDensity;
    private final RGBA fogColors;
    private final boolean renderSunSkyTint;
    private final boolean renderInThickFog;
    private final Rotation rotation;

    public Properties(int priority, Fade fade, float minAlpha, float maxAlpha, int transitionInDuration, int transitionOutDuration, boolean changeFog, boolean changeFogDensity, RGBA fogColors, boolean renderSunSkyTint, boolean renderInThickFog, Rotation rotation) {
        this.priority = priority;
        this.fade = fade;
        if (minAlpha > maxAlpha) {
            throw new IllegalStateException("Maximum alpha is lower than the minimum alpha:\n" + this);
        }
        this.minAlpha = minAlpha;
        this.maxAlpha = maxAlpha;
        this.transitionInDuration = transitionInDuration;
        this.transitionOutDuration = transitionOutDuration;
        this.changeFog = changeFog;
        this.changeFogDensity = changeFogDensity;
        this.fogColors = fogColors;
        this.renderSunSkyTint = renderSunSkyTint;
        this.renderInThickFog = renderInThickFog;
        this.rotation = rotation;
    }

    public static Properties ofSkybox(AbstractSkybox skybox) {
        Rotation rot = Rotation.DEFAULT;
        if (skybox instanceof RotatableSkybox) {
            rot = ((RotatableSkybox) skybox).getRotation();
        }
        return new Builder()
                .changeFog(skybox.getProperties().isChangeFog())
                .renderSunSkyTint(skybox.getProperties().isRenderSunSkyTint())
                .fogColors(skybox.getProperties().getFogColors())
                .fade(skybox.getProperties().getFade())
                .maxAlpha(skybox.getProperties().getMaxAlpha())
                .rotation(rot)
                .build();
    }

    public int getPriority() {
        return this.priority;
    }

    public Fade getFade() {
        return this.fade;
    }

    public float getMinAlpha() {
        return this.minAlpha;
    }

    public float getMaxAlpha() {
        return this.maxAlpha;
    }

    public int getTransitionInDuration() {
        return this.transitionInDuration;
    }

    public int getTransitionOutDuration() {
        return this.transitionOutDuration;
    }

    public boolean isChangeFog() {
        return this.changeFog;
    }

    public boolean isChangeFogDensity() {
        return this.changeFogDensity;
    }

    public RGBA getFogColors() {
        return this.fogColors;
    }

    public boolean isRenderSunSkyTint() {
        return this.renderSunSkyTint;
    }

    public boolean isRenderInThickFog() {
        return this.renderInThickFog;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class Builder {
        private int priority = 0;
        private Fade fade = Fade.DEFAULT;
        private float minAlpha = 0F;
        private float maxAlpha = 1F;
        private int transitionInDuration = 20;
        private int transitionOutDuration = 20;
        private boolean changeFog = false;
        private boolean changeFogDensity = false;
        private RGBA fogColors = RGBA.DEFAULT;
        private boolean renderSunSkyTint = true;
        private boolean renderInTickFog = true;
        private Rotation rotation = Rotation.DEFAULT;

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder fade(Fade fade) {
            this.fade = fade;
            return this;
        }

        public Builder minAlpha(float minAlpha) {
            this.minAlpha = minAlpha;
            return this;
        }

        public Builder maxAlpha(float maxAlpha) {
            this.maxAlpha = maxAlpha;
            return this;
        }

        public Builder transitionInDuration(int transitionInDuration) {
            this.transitionInDuration = transitionInDuration;
            return this;
        }

        public Builder transitionOutDuration(int transitionOutDuration) {
            this.transitionOutDuration = transitionOutDuration;
            return this;
        }

        public Builder changesFog() {
            this.changeFog = true;
            return this;
        }

        public Builder changesFogDensity() {
            this.changeFogDensity = true;
            return this;
        }

        public Builder rendersSunSkyTint() {
            this.renderSunSkyTint = true;
            return this;
        }

        public Builder renderInThickFog(boolean renderInThickFog) {
            this.renderInTickFog = renderInThickFog;
            return this;
        }

        public Builder fogColors(RGBA fogColors) {
            this.fogColors = fogColors;
            return this;
        }

        public Builder rotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder changeFog(boolean changeFog) {
            if (changeFog) {
                return this.changesFog();
            } else {
                return this;
            }
        }

        public Builder renderSunSkyTint(boolean renderSunSkyTint) {
            if (renderSunSkyTint) {
                return this.rendersSunSkyTint();
            } else {
                return this;
            }
        }

        public Properties build() {
            return new Properties(this.priority, this.fade, this.minAlpha, this.maxAlpha, this.transitionInDuration, this.transitionOutDuration, this.changeFog, this.changeFogDensity, this.fogColors, this.renderSunSkyTint, this.renderInTickFog, this.rotation);
        }
    }
}
