package dev.foltz.de.client;

import dev.foltz.de.DEMod;
import dev.foltz.de.plant.Plants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public class DEModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // TODO: Specify which blocks go in which layer (check block settings?)
        for (Block block : Plants.ALL_PLANT_BLOCKS.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
        }
    }
}
