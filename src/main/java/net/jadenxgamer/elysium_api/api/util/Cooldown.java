package net.jadenxgamer.elysium_api.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public final class Cooldown {

    public static final Codec<Cooldown> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("cooldown").forGetter(Cooldown::cooldown),
                    Codec.INT.fieldOf("startTime").forGetter(Cooldown::startTime)
            ).apply(instance, Cooldown::new));

    private final IAttachmentHolder holder;
    private final int cooldown;

    private int startTime = -1;

    // Constructor for Serialization
    private Cooldown(int cooldown, int startTime) {
        this.holder = Minecraft.getInstance().player;
        this.cooldown = cooldown;
        this.startTime = startTime;
    }

    private Cooldown(IAttachmentHolder holder, int cooldown) {
        this.holder = holder;
        this.cooldown = cooldown;
    }

    public static Cooldown forHolder(IAttachmentHolder player, int cooldown) {
        return new Cooldown(player, cooldown);
    }

    public boolean active() {
        return holder.getData(ElysiumAttachmentTypes.COOLDOWN_TICK) - cooldown < startTime;
    }

    public void set() {
        startTime = holder.getData(ElysiumAttachmentTypes.COOLDOWN_TICK);
    }

    private int cooldown() {
        return cooldown;
    }

    private int startTime() {
        return startTime;
    }

}
