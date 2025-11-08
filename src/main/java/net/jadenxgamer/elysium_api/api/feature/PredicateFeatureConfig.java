package net.jadenxgamer.elysium_api.api.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateFeatureConfig<T> {

    private final List<Predicate<T>> predicates = new ArrayList<>(List.of(t -> true));

    public PredicateFeatureConfig() {
    }

    public PredicateFeatureConfig(List<Predicate<T>> defaults) {
        predicates.addAll(defaults);
    }

    public void addPredicate(Predicate<T> predicate) {
        predicates.add(predicate);
    }

    public boolean test(T t) {
        return predicates.stream().reduce(Predicate::and).orElseThrow().test(t);
    }

}
