package dev.foltz.de.plant;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;

public class PlantBehavior {
    private final Map<Predicate<PlantState>, PlantTransition> transitions;
    private final Map<Predicate<PlantState>, PlantAction> actions;

    public PlantBehavior() {
        this.transitions = new LinkedHashMap<>();
        this.actions = new LinkedHashMap<>();
    }

    public PlantBehavior withAction(Predicate<PlantState> predicate, PlantAction action) {
        actions.put(predicate, action);
        return this;
    }

    public PlantBehavior withTransition(Predicate<PlantState> predicate, PlantTransition transition) {
        this.transitions.put(predicate, transition);
        return this;
    }

    public void performAction(PlantState plantState) {
        for (Map.Entry<Predicate<PlantState>, PlantAction> entry : actions.entrySet()) {
            Predicate<PlantState> predicate = entry.getKey();
            if (predicate.test(plantState)) {
                PlantAction action = entry.getValue();
                if (action.performAction(plantState)) {
                    return;
                }
            }
        }
    }

    public Optional<BlockState> nextState(PlantState plantState) {
        for (Map.Entry<Predicate<PlantState>, PlantTransition> entry : transitions.entrySet()) {
            Predicate<PlantState> predicate = entry.getKey();
            if (predicate.test(plantState)) {
                PlantTransition transition = entry.getValue();
                return transition.nextBlockState(plantState);
            }
        }
        return Optional.of(plantState.getBlockState());
    }

    @FunctionalInterface
    public interface PlantAction {
        boolean performAction(PlantState state);
    }

    @FunctionalInterface
    public interface PlantTransition {
        Optional<BlockState> nextBlockState(PlantState state);
    }
}
