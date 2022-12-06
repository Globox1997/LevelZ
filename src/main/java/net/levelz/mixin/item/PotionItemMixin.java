package net.levelz.mixin.item;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.Skill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    @ModifyVariable(method = "finishUsing", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/potion/PotionUtil;getPotionEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"), ordinal = 0)
    private List<StatusEffectInstance> finishUsingMixin(List<StatusEffectInstance> original, ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity) {
            int alchemyLevel = ((PlayerStatsManagerAccess) (PlayerEntity) user).getPlayerStatsManager().getSkillLevel(Skill.ALCHEMY);
            if (alchemyLevel >= ConfigInit.CONFIG.maxLevel && (float) alchemyLevel * ConfigInit.CONFIG.alchemyPotionChance > world.random.nextFloat()) {
                List<StatusEffectInstance> newEffectList = new ArrayList<>();
                for (int i = 0; i < original.size(); i++) {
                    newEffectList.add(
                            new StatusEffectInstance(original.get(i).getEffectType(), original.get(i).getEffectType().isInstant() ? original.get(i).getDuration() : original.get(i).getDuration() * 2,
                                    original.get(i).getEffectType().isInstant() ? original.get(i).getAmplifier() + 1 : original.get(i).getAmplifier(), original.get(i).isAmbient(),
                                    original.get(i).shouldShowParticles(), original.get(i).shouldShowIcon()));
                }
                return newEffectList;
            }
        }
        return original;
    }
}
