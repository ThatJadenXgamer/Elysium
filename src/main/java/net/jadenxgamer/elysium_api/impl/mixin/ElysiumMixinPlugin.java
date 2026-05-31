package net.jadenxgamer.elysium_api.impl.mixin;

import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public abstract class ElysiumMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.matches("net.jadenxgamer.elysium_api.impl.mixin.compat.LevelUtilsMixin")) return ModList.get().isLoaded("terrablender");
        return true;
    }
}
