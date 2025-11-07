package net.jadenxgamer.elysium_api.api.util;

import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public final class Cooldown {

    private final IAttachmentHolder holder;
    private final int cooldown;

    private int startTime = -1;

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
}
