package net.levelz.mixin.misc;

import net.levelz.access.ExperienceOrbAccess;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin implements ExperienceOrbEntityAccess, ExperienceOrbAccess {
    @Shadow
    private int amount;

    private boolean isPlayerDropped = false;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("IsPlayerDropped", this.isPlayerDropped);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.isPlayerDropped = nbt.getBoolean("IsPlayerDropped");
    }

    @Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperience(I)V"))
    private void onPlayerCollisionMixin(PlayerEntity player, int i) {
        if (this.isPlayerDropped) {
            player.addScore(i);
            player.experienceProgress += (float) i / (float) player.getNextLevelExperience();
            player.totalExperience = MathHelper.clamp(player.totalExperience + i, 0, 2147483647);

            while (player.experienceProgress < 0.0F) {
                float f = player.experienceProgress * (float) player.getNextLevelExperience();
                if (player.experienceLevel > 0) {
                    player.addExperienceLevels(-1);
                    player.experienceProgress = 1.0F + f / (float) player.getNextLevelExperience();
                } else {
                    player.addExperienceLevels(-1);
                    player.experienceProgress = 0.0F;
                }
            }

            while (player.experienceProgress >= 1.0F) {
                player.experienceProgress = (player.experienceProgress - 1.0F) * (float) player.getNextLevelExperience();
                player.addExperienceLevels(1);
                player.experienceProgress /= (float) player.getNextLevelExperience();
            }
        } else
            player.addExperience(i);
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void setDroppedByPlayer() {
        this.isPlayerDropped = true;
    }
}
