package net.jadenxgamer.elysium_api.api.feature;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateFeature<T> extends Feature {

    private final List<Predicate<T>> predicates = new ArrayList<>(List.of(t -> true));

    public PredicateFeature(ResourceLocation id) {
        super(id);
    }

    public PredicateFeature(ResourceLocation id, List<Predicate<T>> defaults) {
        super(id);
        predicates.addAll(defaults);
    }


    public void registerPredicate(Predicate<T> predicate) {
        predicates.add(predicate);
    }

    public boolean test(T t) {
        return this.isEnabled() && predicates.stream().reduce(Predicate::and).orElseThrow().test(t);
    }

}
