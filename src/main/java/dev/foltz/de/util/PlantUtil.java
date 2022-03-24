package dev.foltz.de.util;

import dev.foltz.de.plant.PlantBehavior;
import dev.foltz.de.plant.PlantState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PlantUtil {
    public static final Random RANDOM = new Random();

    public static double randomRange(int start, int range) {
        return Math.round(start + range - 2 * RANDOM.nextDouble() * range);
    }

    /**************
     * Predicates *
     **************/

    public static final PlantBehavior.PlantTransition T_DEAD = state -> Optional.empty();

    public static <T> Predicate<T> not(Predicate<T> pred) {
        return pred.negate();
    }

    @SafeVarargs
    public static Predicate<PlantState> all(Predicate<PlantState> first, Predicate<PlantState>... others) {
        Predicate<PlantState> pred = first;
        for (Predicate<PlantState> other : others) {
            pred = pred.and(other);
        }
        return pred;
    }

    public static <T extends Comparable<T>> Predicate<PlantState> require(Property<T> property, T value) {
        return state -> state.get(property).compareTo(value) == 0;
    }

    public static <T extends Comparable<T>> Predicate<PlantState> lt(Property<T> property, T value) {
        return state -> state.get(property).compareTo(value) < 0;
    }

    public static <T extends Comparable<T>> Predicate<PlantState> lte(Property<T> property, T value) {
        return state -> state.get(property).compareTo(value) <= 0;
    }

    public static <T extends Comparable<T>> Predicate<PlantState> gt(Property<T> property, T value) {
        return state -> state.get(property).compareTo(value) > 0;
    }

    public static <T extends Comparable<T>> Predicate<PlantState> gte(Property<T> property, T value) {
        return state -> state.get(property).compareTo(value) >= 0;
    }

    public static <T extends Comparable<T>> Predicate<PlantState> eq(Property<T> property, T value) {
        return state -> state.get(property).compareTo(value) == 0;
    }

    public static Predicate<PlantState> lt(Function<PlantState, Integer> f, int value) {
        return state -> f.apply(state) < 0;
    }

    public static Predicate<PlantState> lte(Function<PlantState, Integer> f, int value) {
        return state -> f.apply(state) <= value;
    }

    public static Predicate<PlantState> gt(Function<PlantState, Integer> f, int value) {
        return state -> f.apply(state) > value;
    }

    public static Predicate<PlantState> gte(Function<PlantState, Integer> f, int value) {
        return state -> f.apply(state) >= value;
    }

    public static Predicate<PlantState> eq(Function<PlantState, Integer> f, int value) {
        return state -> f.apply(state) == value;
    }

    public static <T extends Comparable<T>> PlantBehavior.PlantTransition apply(Property<T> property, T value) {
        return state -> Optional.of(state.getBlockState().with(property, value));
    }

    public static PlantBehavior.PlantTransition inc(IntProperty property) {
        return inc(property, 1);
    }

    public static PlantBehavior.PlantTransition inc(IntProperty property, int amount) {
        return state -> Optional.of(state.getBlockState().with(property, state.get(property) + amount));
    }

    public static PlantBehavior.PlantTransition dec(IntProperty property, int amount) {
        return inc(property, -amount);
    }

    public static PlantBehavior.PlantTransition dec(IntProperty property) {
        return dec(property, 1);
    }
}
