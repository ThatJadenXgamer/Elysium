package net.jadenxgamer.elysium_api;

import net.jadenxgamer.elysium_api.api.feature.PredicateFeature;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ElysiumFeatures {

    public static final PredicateFeature<Player> DODGE_ROLL = new PredicateFeature<>(
            Elysium.id("dodge_roll"), List.of(
                p -> !p.getData(ElysiumAttachmentTypes.DODGE_COOLDOWN).active(),
                p -> p.getFoodData().getFoodLevel() > 6 | p.isCreative(),
                Entity::onGround
            ));

}
