package dev.foltz.de;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

import java.util.Map;
import java.util.Set;

public abstract class AbstractModBlock extends Block {
    public <T extends Comparable<T>, V extends T> AbstractModBlock(Settings settings) {
        super(settings);
        BlockState defaultState = getStateManager().getDefaultState();
        defaultBlockProperties().forEach((key, value) -> {
            defaultState.with((Property<T>) key, (V) value);
        });
        setDefaultState(defaultState);
    }

    public Map<Property<?>, ?> defaultBlockProperties() {
        return Map.of();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        for (Property<?> property : this.defaultBlockProperties().keySet()) {
            builder.add(property);
        }
    }
}
