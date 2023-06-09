package net.levelz.mixin.misc;

import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    private ClientWorld world;

    @Shadow
    private static void drawShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue,
            float alpha, boolean bl) {
        throw new AbstractMethodError("shadow");
    }

    @Inject(method = "drawBlockOutline", at = @At(value = "HEAD"), cancellable = true)
    private void drawBlockOutlineMixin(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState,
            CallbackInfo info) {
        if (ConfigInit.CONFIG.highlightLocked && PlayerStatsManager.listContainsItemOrBlock(MinecraftClient.getInstance().player, Registries.BLOCK.getRawId(blockState.getBlock()), 1)) {
            drawShapeOutline(matrices, vertexConsumer, blockState.getOutlineShape(this.world, blockPos, ShapeContext.of(entity)), (double) blockPos.getX() - cameraX,
                    (double) blockPos.getY() - cameraY, (double) blockPos.getZ() - cameraZ, 1.0F, 0.0F, 0.0F, 0.4F, false);
            info.cancel();
        }
    }
}
