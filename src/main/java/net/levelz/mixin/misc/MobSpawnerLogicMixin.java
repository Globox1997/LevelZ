package net.levelz.mixin.misc;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.access.MobEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;

@SuppressWarnings("rawtypes")
@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/MobEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/EntityData;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void serverTickMixin(ServerWorld world, BlockPos pos, CallbackInfo info, boolean bl, Random random, MobSpawnerEntry mobSpawnerEntry, int i, NbtCompound nbtCompound, Optional optional,
            NbtList nbtList, int j, double d, double e, double f, BlockPos blockPos, Entity entity) {
        ((MobEntityAccess) entity).setSpawnerMob(true);
    }

}
