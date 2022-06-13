package net.levelz.item;

import java.util.Iterator;
import java.util.List;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class StrangePotionItem extends Item {

    public StrangePotionItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (playerEntity != null && !playerEntity.world.isClient) {
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) playerEntity, stack);
            }

            List<String> list = PlayerStatsManager.getAllSkills();
            boolean foundSkill = false;
            for (int i = 0; i < list.size(); i++) {
                if (PlayerStatsManager.resetSkill(playerEntity, list.get(world.random.nextInt(list.size())))) {
                    foundSkill = true;
                    break;
                }
            }
            if (!foundSkill) {
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (PlayerStatsManager.resetSkill(playerEntity, iterator.next()))
                        break;
                }
            }

            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            if (!playerEntity.getAbilities().creativeMode) {
                if (stack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                }
                playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }

            user.emitGameEvent(GameEvent.DRINK);
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

}
