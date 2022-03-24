package dev.foltz.de.plant.plants;

import dev.foltz.de.block.ModBlockFactory;
import dev.foltz.de.mixins.IWorldRendererMixin;
import dev.foltz.de.plant.Plant;
import dev.foltz.de.plant.PlantBehavior;
import dev.foltz.de.plant.PlantState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.event.GameEvent;

import static dev.foltz.de.util.PlantUtil.apply;
import static dev.foltz.de.util.PlantUtil.require;

public class CubeStalkPlant {
    public static final Plant CUBE_STALK_PLANT = new Plant("cube_stalk");

    public static final BooleanProperty TWO_TALL = BooleanProperty.of("two_tall");

    public static final PlantBehavior CUBE_STALK_PLANT_BEHAVIOR = new PlantBehavior()
            .withAction(require(TWO_TALL, true),
                    state -> state.world.setBlockState(state.pos.up(), state.getBlockState().with(TWO_TALL, true)))
            .withTransition(require(TWO_TALL, false), apply(TWO_TALL, true));

    public static final Block CUBE_STALK_PLANT_BLOCK = ModBlockFactory
            .builder(ModBlockFactory.SETTINGS_PLANT_SOLID)
            .withDefaultProperty(TWO_TALL, false)
            .withShape((blockState, blockView, blockPos, shapeContext) -> blockState.get(TWO_TALL)
                    ? Block.createCuboidShape(4, 0, 4, 12, 16, 12)
                    : Block.createCuboidShape(4, 0, 4, 12, 8, 12))
            .canExist((world, state, pos) -> {
                BlockState bs = world.getBlockState(pos.down());
                return bs.isIn(BlockTags.DIRT) || bs.isIn(BlockTags.SAND) || (bs.getBlock() == CUBE_STALK_PLANT.block && bs.get(TWO_TALL));
            })
            .withBreak((world, player, pos, state, blockEntity) -> {
                if (state.getBlock() != CUBE_STALK_PLANT.block) {
                    return true;
                }
                HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
                if (hitResult == null || hitResult.getPos() == null) {
                    return true;
                }
                double frac = Math.abs(hitResult.getPos().y % 1);
                boolean above = frac < 0.5;
                if (state.get(TWO_TALL) && above) {
                    System.out.println("Breaking the top of a two tall!");
                    world.setBlockState(pos, state.with(TWO_TALL, false));
                    world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                    return false;
                }
                return true;
            })
            .withTick((world, blockState, blockPos, random) -> new PlantState(CUBE_STALK_PLANT, world, blockPos).tick())
            .create();
}
