package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;

public class Animation {
    public static final Codec<Animation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Texture.CODEC.fieldOf("texture").forGetter(Animation::getTexture),
            UVRange.CODEC.fieldOf("uvRanges").forGetter(Animation::getUvRanges),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).fieldOf("gridColumns").forGetter(Animation::getGridColumns),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).fieldOf("gridRows").forGetter(Animation::getGridRows),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).fieldOf("duration").forGetter(Animation::getDuration),
            Codec.BOOL.optionalFieldOf("interpolate", true).forGetter(Animation::isInterpolate),
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("frameDuration", new HashMap<>()).forGetter(Animation::getFrameDuration)
    ).apply(instance, Animation::new));

    private final Texture texture;
    private final UVRange uvRange;
    private final int gridRows;
    private final int gridColumns;
    private final int duration;
    private final boolean interpolate;
    private final Map<String, Integer> frameDuration;

    private UVRange currentFrame;
    private int index;
    private long nextTime;

    public Animation(Texture texture, UVRange uvRange, int gridColumns, int gridRows, int duration, boolean interpolate, Map<String, Integer> frameDuration) {
        this.texture = texture;
        this.uvRange = uvRange;
        this.gridColumns = gridColumns;
        this.gridRows = gridRows;
        this.duration = duration;
        this.interpolate = interpolate;
        this.frameDuration = frameDuration;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public UVRange getUvRanges() {
        return this.uvRange;
    }

    public int getGridColumns() {
        return this.gridColumns;
    }

    public int getGridRows() {
        return this.gridRows;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isInterpolate() {
        return this.interpolate;
    }

    public Map<String, Integer> getFrameDuration() {
        return this.frameDuration;
    }

    public void tick() {
        if (this.nextTime <= Util.getEpochMillis()) {
            // Current Frame
            this.index = (this.index + 1) % (this.gridRows * this.gridColumns);
            this.currentFrame = this.calculateNextFrameUVRange(this.index);
            this.nextTime = Util.getEpochMillis() + this.frameDuration.getOrDefault(String.valueOf(this.index + 1), this.duration);
        }
    }

    public UVRange getCurrentFrame() {
        return currentFrame;
    }

    private UVRange calculateNextFrameUVRange(int nextFrameIndex) {
        float frameWidth = 1.0F / this.gridColumns;
        float frameHeight = 1.0F / this.gridRows;
        float minU = (float) (nextFrameIndex / this.gridRows) * frameWidth;
        float maxU = minU + frameWidth;
        float minV = (float) (nextFrameIndex % this.gridRows) * frameHeight;
        float maxV = minV + frameHeight;
        return new UVRange(minU, minV, maxU, maxV);
    }
}
