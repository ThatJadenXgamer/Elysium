package net.jadenxgamer.elysium_api.impl.core.datadriven.sound_transformers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public record SoundTransformer(HolderSet<Block> blocks, SoundEvent breakSound, SoundEvent stepSound,
                               SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound,
                               float volume, float pitch) {
    public static final Codec<SoundTransformer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(SoundTransformer::blocks),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("break_sound").forGetter(SoundTransformer::breakSound),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("step_sound").forGetter(SoundTransformer::stepSound),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("place_sound").forGetter(SoundTransformer::placeSound),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("hit_sound").forGetter(SoundTransformer::hitSound),
            BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("fall_sound").forGetter(SoundTransformer::fallSound),
            Codec.FLOAT.fieldOf("volume").orElse(1.0f).forGetter(SoundTransformer::volume),
            Codec.FLOAT.fieldOf("pitch").orElse(1.0f).forGetter(SoundTransformer::pitch)
    ).apply(instance, SoundTransformer::new));

    public SoundType toSoundType() {
        return new SoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }
}