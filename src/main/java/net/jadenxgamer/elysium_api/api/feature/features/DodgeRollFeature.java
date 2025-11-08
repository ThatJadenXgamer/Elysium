package net.jadenxgamer.elysium_api.api.feature.features;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.feature.Feature;
import net.jadenxgamer.elysium_api.api.feature.PredicateFeatureConfig;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DodgeRollFeature extends Feature<PredicateFeatureConfig<Player>> {

    public static final DodgeRollFeature INSTANCE = new DodgeRollFeature();

    private DodgeRollFeature() {
        super(Elysium.id("dodge_roll"), new PredicateFeatureConfig<>(List.of(
                p -> !p.getData(ElysiumAttachmentTypes.DODGE_COOLDOWN).active(),
                p -> p.getFoodData().getFoodLevel() > 6 | p.isCreative(),
                Entity::onGround
        )));
    }

    public boolean canDodge(Player player) {
        return this.isEnabled() && this.config().test(player);
    }
}
