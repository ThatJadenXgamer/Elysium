package net.jadenxgamer.elysium_api.impl.util;

import com.mojang.serialization.Codec;
import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public record BlockStatePropertiesCondition(Map<String, String> properties) {
    public static final Codec<BlockStatePropertiesCondition> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
            .xmap(BlockStatePropertiesCondition::new, BlockStatePropertiesCondition::properties);

    private static final Map<String, CompiledCondition> CONDITION_CACHE = new ConcurrentHashMap<>();

    private enum Operator {
        EXACT_MATCH, NOT, OR, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
    }

    private record CompiledCondition(Operator operator, String[] values, double numericValue, boolean isNumeric) {
        CompiledCondition(Operator operator, String[] values) {
            this(operator, values, 0.0, false);
        }
        CompiledCondition(Operator operator, double numericValue) {
            this(operator, null, numericValue, true);
        }
        CompiledCondition(Operator operator, String value) {
            this(operator, new String[]{value}, 0.0, false);
        }
    }

    public boolean matches(BlockState state) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Property<?> property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (property == null) return false;
            Comparable<?> currentValue = state.getValue(property);
            CompiledCondition condition = CONDITION_CACHE.computeIfAbsent(entry.getValue(), this::compileCondition);
            if (!checkCondition(property, currentValue, condition)) return false;
        }
        return true;
    }

    private CompiledCondition compileCondition(String conditionValue) {
        String trimmed = conditionValue.trim();
        if (trimmed.isEmpty()) return new CompiledCondition(Operator.EXACT_MATCH, "");
        char first = trimmed.charAt(0);
        if (first == '!') {
            String target = trimmed.substring(1).trim();
            if (target.length() > 1 && target.charAt(0) == '(' && target.charAt(target.length() - 1) == ')')
                return new CompiledCondition(Operator.NOT, splitOnPipe(target.substring(1, target.length() - 1)));

            return new CompiledCondition(Operator.NOT, target);
        }
        if (trimmed.indexOf('|') != -1) return new CompiledCondition(Operator.OR, splitOnPipe(trimmed));
        if (trimmed.length() >= 2) {
            CompiledCondition numeric = tryParseNumeric(trimmed);
            if (numeric != null) return numeric;
        }
        return new CompiledCondition(Operator.EXACT_MATCH, trimmed);
    }

    private CompiledCondition tryParseNumeric(String s) {
        Operator op = null;
        int startIdx = -1;
        if (s.startsWith(">=")) { op = Operator.GREATER_THAN_OR_EQUAL; startIdx = 2; }
        else if (s.startsWith("<=")) { op = Operator.LESS_THAN_OR_EQUAL; startIdx = 2; }
        else if (s.startsWith(">")) { op = Operator.GREATER_THAN; startIdx = 1; }
        else if (s.startsWith("<")) { op = Operator.LESS_THAN; startIdx = 1; }
        if (op == null) return null;

        try {
            double value = Double.parseDouble(s.substring(startIdx).trim());
            return new CompiledCondition(op, value);
        } catch (NumberFormatException e) {
            ElysiumAPI.LOGGER.warn("Failed to parse numeric condition '{}'", s);
            return null;
        }
    }

    private String[] splitOnPipe(String input) { return input.split("\\|", -1); }

    private boolean checkCondition(Property<?> property, Comparable<?> currentValue, CompiledCondition condition) {
        return switch (condition.operator) {
            case NOT -> handleNot(property, currentValue, condition);
            case OR -> handleOr(property, currentValue, condition);
            case GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL -> handleNumeric(currentValue, condition);
            default -> compareValues(property, currentValue, condition.values[0]);
        };
    }

    private boolean handleNot(Property<?> property, Comparable<?> currentValue, CompiledCondition condition) {
        String[] values = condition.values;
        if (values.length > 1) {
            for (String value : values) if (compareValues(property, currentValue, value)) return false;
            return true;
        }
        return !compareValues(property, currentValue, values[0]);
    }

    private boolean handleOr(Property<?> property, Comparable<?> currentValue, CompiledCondition condition) {
        for (String value : condition.values) if (compareValues(property, currentValue, value)) return true;
        return false;
    }

    private boolean handleNumeric(Comparable<?> currentValue, CompiledCondition condition) {
        if (!(currentValue instanceof Number currentNum)) return false;
        double current = currentNum.doubleValue();
        double target = condition.numericValue;
        return switch (condition.operator) {
            case GREATER_THAN -> current > target;
            case LESS_THAN -> current < target;
            case GREATER_THAN_OR_EQUAL -> current >= target;
            case LESS_THAN_OR_EQUAL -> current <= target;
            default -> false;
        };
    }

    private boolean compareValues(Property<?> property, Comparable<?> currentValue, String conditionValue) {
        Optional<?> parsed = property.getValue(conditionValue);
        return parsed.map(currentValue::equals).orElseGet(() -> currentValue.toString().equals(conditionValue));
    }
}