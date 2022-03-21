package dev.foltz.de.mixins;

import dev.foltz.de.DEMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    //    Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V
    @Inject(method="drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at=@At("HEAD"), cancellable=true)
    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() == DEMod.CUBE_STALK_PLANT) {
            final VoxelShape CUBE_STALK_OUTLINE_LOWER = Block.createCuboidShape(4, 0, 4, 12, 8, 12);
            final VoxelShape CUBE_STALK_OUTLINE_UPPER = Block.createCuboidShape(4, 8, 4, 12, 16, 12);

            HitResult hitResult = ((IWorldRendererMixin) this).getClient().crosshairTarget;
            if (hitResult == null || hitResult.getPos() == null) {
                return;
            }
            double frac = Math.abs(hitResult.getPos().y % 1);
            boolean above = frac < 0.5;
//            System.out.println("Hit: " + hitResult.getPos() + "; " + frac + "; " + above);
            VoxelShape outlineShape = above ? CUBE_STALK_OUTLINE_UPPER : CUBE_STALK_OUTLINE_LOWER;

            {
                double dd = pos.getX() - d;
                double ee = pos.getY() - e;
                double ff = pos.getZ() - f;
                float rr = 0f;
                float gg = 0f;
                float bb = 0f;
                float aa = 0.4f;
                net.minecraft.client.util.math.MatrixStack.Entry entry = matrices.peek();
                outlineShape.forEachEdge((k, l, m, n, o, p) -> {
                    float q = (float)(n - k);
                    float r = (float)(o - l);
                    float s = (float)(p - m);
                    float t = MathHelper.sqrt(q * q + r * r + s * s);
                    q /= t;
                    r /= t;
                    s /= t;
                    vertexConsumer.vertex(entry.getPositionMatrix(), (float)(k + dd), (float)(l + ee), (float)(m + ff)).color(rr, gg, bb, aa).normal(entry.getNormalMatrix(), q, r, s).next();
                    vertexConsumer.vertex(entry.getPositionMatrix(), (float)(n + dd), (float)(o + ee), (float)(p + ff)).color(rr, gg, bb, aa).normal(entry.getNormalMatrix(), q, r, s).next();
                });
            }
            ci.cancel();
        }
    }
}
