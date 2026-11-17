package net.levelz.mixin.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    @Mutable
    @Final
    private Minecraft minecraft;

    @Nullable
    @Shadow
    private ClientLevel level;

    @Inject(method = "renderHitOutline", at = @At(value = "HEAD"), cancellable = true)
    private void drawBlockOutlineMixin(PoseStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState,
                                       CallbackInfo info) {
        if (ConfigInit.CONFIG.highlightLocked && !((LevelManagerAccess) minecraft.player).getLevelManager().hasRequiredMiningLevel(blockState.getBlock())) {
            LevelRenderer.renderVoxelShape(matrices, vertexConsumer, blockState.getShape(this.level, blockPos, CollisionContext.of(entity)), (double) blockPos.getX() - cameraX,
                    (double) blockPos.getY() - cameraY, (double) blockPos.getZ() - cameraZ, 1.0F, 0.0F, 0.0F, 0.4F, false);
            info.cancel();
        }
    }
}
