package net.jadenxgamer.elysium_api.api.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateFeature<T> extends Feature {

    private final List<Predicate<T>> predicates = new ArrayList<>(List.of(t -> true));

    public PredicateFeature() {}

    public PredicateFeature(List<Predicate<T>> defaults) {
        predicates.addAll(defaults);
    }


    public void registerPredicate(Predicate<T> predicate) {
        predicates.add(predicate);
    }

    public boolean test(T t) {
        return this.isEnabled() && predicates.stream().reduce(Predicate::and).orElseThrow().test(t);
    }

}
