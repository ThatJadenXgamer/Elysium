package net.jadenxgamer.elysium_api;

import net.jadenxgamer.elysium_api.api.feature.PredicateFeature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ElysiumFeatures {

    public static final PredicateFeature<Player> DODGE_ROLL = new PredicateFeature<>(List.of(Entity::onGround));

}
