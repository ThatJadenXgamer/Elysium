package net.jadenxgamer.elysium_api.impl.core.datadriven.block.use_behaviors;

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

    private record CompiledCondition(Operator operator, String[] values, Double numericValue, boolean isNumeric) {
        CompiledCondition(Operator operator, String[] values) {
            this(operator, values, null, false);
        }

        CompiledCondition(Operator operator, Double numericValue) {
            this(operator, null, numericValue, true);
        }

        CompiledCondition(Operator operator, String value) {
            this(operator, new String[]{value}, null, false);
        }
    }

    public boolean matches(BlockState state) {
        for (var entry : properties.entrySet()) {
            var property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
            if (property == null) return false;

            var currentValue = state.getValue(property);
            var condition = CONDITION_CACHE.computeIfAbsent(entry.getValue(), this::compileCondition);

            if (!checkCondition(property, currentValue, condition)) {
                return false;
            }
        }
        return true;
    }

    private CompiledCondition compileCondition(String conditionValue) {
        String trimmed = conditionValue.trim();

        // NOT operator
        if (trimmed.charAt(0) == '!') {
            String target = trimmed.substring(1).trim();
            if (target.startsWith("(") && target.endsWith(")")) {
                return new CompiledCondition(Operator.NOT,
                        splitOnPipe(target.substring(1, target.length() - 1)));
            }
            return new CompiledCondition(Operator.NOT, target);
        }

        // OR operator
        if (trimmed.indexOf('|') != -1) {
            return new CompiledCondition(Operator.OR, splitOnPipe(trimmed));
        }

        // Numeric comparisons
        if (trimmed.length() >= 2) {
            var numericCondition = parseNumericCondition(trimmed);
            if (numericCondition != null) return numericCondition;
        }

        // Default: exact match
        return new CompiledCondition(Operator.EXACT_MATCH, trimmed);
    }

    private CompiledCondition parseNumericCondition(String trimmed) {
        try {
            double trimmed1Value = Double.parseDouble(trimmed.substring(1).trim());
            double trimmed2Value = Double.parseDouble(trimmed.substring(2).trim());
            if (trimmed.startsWith(">=") && trimmed.length() > 2) {
                return new CompiledCondition(Operator.GREATER_THAN_OR_EQUAL, trimmed2Value);
            } else if (trimmed.startsWith("<=") && trimmed.length() > 2) {
                return new CompiledCondition(Operator.LESS_THAN_OR_EQUAL, trimmed2Value);
            } else {
                if (trimmed.startsWith(">")) {
                    return new CompiledCondition(Operator.GREATER_THAN, trimmed1Value);
                } else if (trimmed.startsWith("<")) {
                    return new CompiledCondition(Operator.LESS_THAN, trimmed1Value);
                }
            }
        } catch (NumberFormatException e) {
            ElysiumAPI.LOGGER.warn("Failed to parse numeric condition '{}'", trimmed);
        }
        return null;
    }

    private String[] splitOnPipe(String input) {
        int pipeCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '|') pipeCount++;
        }

        String[] result = new String[pipeCount + 1];
        int start = 0;
        int resultIndex = 0;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '|') {
                result[resultIndex++] = input.substring(start, i).trim();
                start = i + 1;
            }
        }

        result[resultIndex] = input.substring(start).trim();
        return result;
    }

    private boolean checkCondition(Property<?> property, Comparable<?> currentValue, CompiledCondition condition) {
        return switch (condition.operator) {
            case NOT -> handleNot(property, currentValue, condition);
            case OR -> handleOr(property, currentValue, condition);
            case GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL ->
                    handleNumeric(currentValue, condition);
            default -> compareValues(property, currentValue, condition.values[0]);
        };
    }

    private boolean handleNot(Property<?> property, Comparable<?> currentValue, CompiledCondition condition) {
        if (condition.values.length > 1) {
            for (String value : condition.values) {
                if (compareValues(property, currentValue, value)) return false;
            }
            return true;
        }
        return !compareValues(property, currentValue, condition.values[0]);
    }

    private boolean handleOr(Property<?> property, Comparable<?> currentValue, CompiledCondition condition) {
        for (String value : condition.values) {
            if (compareValues(property, currentValue, value)) return true;
        }
        return false;
    }

    private boolean handleNumeric(Comparable<?> currentValue, CompiledCondition condition) {
        if (!(currentValue instanceof Number currentNum)) return false;

        double targetNum = condition.numericValue;
        return switch (condition.operator) {
            case GREATER_THAN -> currentNum.doubleValue() > targetNum;
            case LESS_THAN -> currentNum.doubleValue() < targetNum;
            case GREATER_THAN_OR_EQUAL -> currentNum.doubleValue() >= targetNum;
            case LESS_THAN_OR_EQUAL -> currentNum.doubleValue() <= targetNum;
            default -> false;
        };
    }

    private boolean compareValues(Property<?> property, Comparable<?> currentValue, String conditionValue) {
        try {
            Optional<?> parsedValue = property.getValue(conditionValue);
            if (parsedValue.isPresent()) {
                return currentValue.equals(parsedValue.get());
            }
        } catch (Exception e) {
            ElysiumAPI.LOGGER.warn("Failed to parse value '{}' for property '{}'", conditionValue, property.getName());
        }

        return currentValue.toString().equals(conditionValue);
    }
}