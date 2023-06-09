package net.levelz.mixin.block;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Nullable
    private ServerPlayerEntity serverPlayerEntity = null;

    @Mutable
    @Final
    @Shadow
    private Object2IntOpenHashMap<Identifier> recipesUsed;

    @Inject(method = "dropExperienceForRecipesUsed", at = @At(value = "HEAD"))
    private void dropExperienceForRecipesUsedMixin(ServerPlayerEntity player, CallbackInfo info) {
        serverPlayerEntity = player;
    }

    @Inject(method = "getRecipesUsedAndDropExperience", at = @At(value = "TAIL"))
    private void getRecipesUsedAndDropExperienceMixin(ServerWorld world, Vec3d pos, CallbackInfoReturnable<List<Recipe<?>>> info) {
        if (ConfigInit.CONFIG.furnaceXPMultiplier > 0.0F) {
            for (Object2IntMap.Entry<Identifier> entry : this.recipesUsed.object2IntEntrySet()) {
                world.getRecipeManager().get((Identifier) entry.getKey()).ifPresent(recipe -> {
                    if (!recipe.getOutput(world.getRegistryManager()).isIn(TagInit.RESTRICTED_FURNACE_EXPERIENCE_ITEMS)) {
                        int i = MathHelper.floor((float) entry.getIntValue() * ((AbstractCookingRecipe) recipe).getExperience());
                        float f = MathHelper.fractionalPart((float) entry.getIntValue() * ((AbstractCookingRecipe) recipe).getExperience());
                        if (f != 0.0f && Math.random() < (double) f) {
                            ++i;
                        }
                        LevelExperienceOrbEntity.spawn(world, pos,
                                (int) (i * ConfigInit.CONFIG.furnaceXPMultiplier
                                        * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                                                ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager().getOverallLevel()
                                                : 1.0F)));
                    }
                });
            }
        }
    }

}
