package dev.foltz.de.client;

import dev.foltz.de.DEMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;

public class DEModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(DEMod.CABlock, RenderLayer.getCutout());
    }
}
