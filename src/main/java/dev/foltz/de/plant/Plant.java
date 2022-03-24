package dev.foltz.de.plant;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Plant {
    public final String name;
    public PlantBehavior behavior;
    public Block block;

    public Plant(String name) {
        this.name = name;
        this.behavior = new PlantBehavior();
        this.block = null;
    }

    public Plant withBehavior(PlantBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public Plant withBlock(Block block) {
        this.block = block;
        return this;
    }
}
