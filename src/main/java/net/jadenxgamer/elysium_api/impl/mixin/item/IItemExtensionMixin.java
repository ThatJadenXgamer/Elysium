package net.jadenxgamer.elysium_api.impl.mixin.item;

import net.jadenxgamer.elysium_api.api.util.RegistryAccessHelper;
import net.jadenxgamer.elysium_api.impl.core.datadriven.item.RemainderTransformer;
import net.jadenxgamer.elysium_api.impl.core.datadriven.item.remainder_transformer.RemainderType;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(IItemExtension.class)
public interface IItemExtensionMixin {

    @Shadow Item self();

    @Inject(
            method = "getCraftingRemainingItem",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$remainderTransformer(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!RegistryAccessHelper.hasAccess()) return;
        Optional<RemainderTransformer> registry = RegistryAccessHelper.getAccessOrThrow().registryOrThrow(ElysiumRegistries.REMAINDER_TRANSFORMERS).stream().filter(s -> s.items().contains(self().builtInRegistryHolder())).findFirst();
        if (registry.isEmpty()) return;

        switch (registry.get().remainderType()) {
            case NONE -> cir.setReturnValue(null);
            case NON_CONSUMABLE -> cir.setReturnValue(new ItemStack(self()));
            case CHANGE_ITEM -> cir.setReturnValue(registry.get().changeItem());
        }
    }

    @Inject(
            method = "hasCraftingRemainingItem",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$hasRemainderTransformer(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!RegistryAccessHelper.hasAccess()) return;
        Optional<RemainderTransformer> registry = RegistryAccessHelper.getAccessOrThrow().registryOrThrow(ElysiumRegistries.REMAINDER_TRANSFORMERS).stream().filter(s -> s.items().contains(self().builtInRegistryHolder())).findFirst();
        if (registry.isEmpty()) return;
        cir.setReturnValue(registry.get().remainderType() != RemainderType.NONE);
    }
}
