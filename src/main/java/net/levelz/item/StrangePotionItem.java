package net.levelz.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrangePotionItem extends Item {

    public StrangePotionItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide() && user instanceof ServerPlayer playerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(playerEntity, stack);

            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            List<Integer> list = new ArrayList<>(levelManager.getPlayerSkills().keySet());
            Collections.shuffle(list);

            for (int skillId : list) {
                if (levelManager.resetSkill(skillId) && !ConfigInit.CONFIG.opStrangePotion) {
                    break;
                }
            }
            PacketHelper.updatePlayerSkills(playerEntity, null);

            if (!playerEntity.isCreative()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                }
                playerEntity.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }

            user.gameEvent(GameEvent.DRINK);
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 32;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

}
