package dev.foltz.de.client;

import dev.foltz.de.DEMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class DEModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(DEMod.CONWAY_PLANT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DEMod.REACT_DIFF_PLANT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DEMod.EYE_PLANT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DEMod.SUN_PLANT, RenderLayer.getCutout());
    }
}
