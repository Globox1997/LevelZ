package net.levelz.mixin.misc;

import net.levelz.access.MobEntityAccess;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@SuppressWarnings("rawtypes")
@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/mob/MobEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;)Lnet/minecraft/entity/EntityData;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void serverTickMixin(ServerWorld world, BlockPos pos, CallbackInfo ci, boolean bl, Random random, MobSpawnerEntry mobSpawnerEntry, int i, NbtCompound nbtCompound, Optional optional, NbtList nbtList, int j, double d, double e, double f, BlockPos blockPos, Entity entity, int k, MobEntity mobEntity, boolean bl2) {
        ((MobEntityAccess) entity).setSpawnerMob(true);
    }

}
