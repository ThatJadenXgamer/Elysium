package net.jadenxgamer.elysium_api.impl.mixin.block;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.properties_transformer.BlockProperties;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviourMixin {

    @Unique
    private final BlockState state = ((BlockState) (Object) this);

    @Inject(
            method = "getLightEmission",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$lightEmission(CallbackInfoReturnable<Integer> cir) {
        if (Elysium.registryAccess != null) {
            Optional<BlockProperties> registry = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BLOCK_PROPERTIES_TRANSFORMER).stream().filter(s -> s.blocks().contains(state.getBlockHolder())).findFirst();
            if (registry.isEmpty() || registry.get().lightEmission().isEmpty()) {
                return;
            }

            cir.setReturnValue(registry.get().lightEmission().get());
        }
    }
}
