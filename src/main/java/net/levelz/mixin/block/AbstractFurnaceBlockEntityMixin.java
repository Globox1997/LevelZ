package net.levelz.mixin.block;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.levelz.access.LevelManagerAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.TagInit;
import net.levelz.mixin.misc.SingleItemRecipeAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Unique
    @Nullable
    private ServerPlayer serverPlayerEntity = null;

    @Mutable
    @Final
    @Shadow
    private Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed;

    @Inject(method = "awardUsedRecipesAndPopExperience", at = @At(value = "HEAD"))
    private void dropExperienceForRecipesUsedMixin(ServerPlayer player, CallbackInfo info) {
        this.serverPlayerEntity = player;
    }

    @Inject(method = "awardUsedRecipesAndPopExperience", at = @At(value = "RETURN"))
    private void dropExperienceForRecipesUsedReturnMixin(ServerPlayer player, CallbackInfo info) {
        this.serverPlayerEntity = null;
    }

    @Inject(method = "getRecipesToAwardAndPopExperience", at = @At(value = "TAIL"))
    private void getRecipesUsedAndDropExperienceMixin(ServerLevel world, Vec3 pos, CallbackInfoReturnable<List<RecipeHolder<?>>> info) {
        if (ConfigInit.CONFIG.furnaceXPMultiplier > 0.0F) {
            for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> entry : this.recipesUsed.reference2IntEntrySet()) {
                world.recipeAccess().byKey(entry.getKey()).ifPresent(recipe -> {
                    if (!((SingleItemRecipeAccessor) recipe.value()).callResult().item().is(TagInit.RESTRICTED_FURNACE_EXPERIENCE_ITEMS)) {
                        int i = Mth.floor((float) entry.getIntValue() * ((AbstractCookingRecipe) recipe.value()).experience());
                        float f = Mth.frac((float) entry.getIntValue() * ((AbstractCookingRecipe) recipe.value()).experience());
                        if (f != 0.0f && Math.random() < (double) f) {
                            ++i;
                        }
                        LevelExperienceOrbEntity.spawn(world, pos,
                                (int) (i * ConfigInit.CONFIG.furnaceXPMultiplier
                                        * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                                        ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                                        : 1.0F)));
                    }
                });
            }
        }
    }

}
