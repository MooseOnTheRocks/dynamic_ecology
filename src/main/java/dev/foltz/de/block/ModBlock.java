package dev.foltz.de.block;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public abstract class ModBlock extends Block implements PlayerBlockBreakEvents.Before {
    public <T extends Comparable<T>, V extends T> ModBlock(Settings settings) {
        super(settings);
        // Dynamically create default BlockState
        BlockState defaultState = getStateManager().getDefaultState();
        for (Map.Entry<Property<?>, ?> entry : defaultBlockProperties().entrySet()) {
            defaultState = defaultState.with((Property<T>) entry.getKey(), (V) entry.getValue());
        }
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

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        return true;
    }
}
