package net.levelz.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.access.ServerPlayerSyncAccess;
import net.levelz.level.LevelManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// Texture made by Pois1x
public class RareCandyItem extends Item {

    public RareCandyItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (!world.isClientSide()) {
            if (!user.isCreative()) {
                stack.shrink(1);
            }
            LevelManager levelManager = ((LevelManagerAccess) user).getLevelManager();
            ((ServerPlayerSyncAccess) user)
                    .addLevelExperience(levelManager.getNextLevelExperience() - ((int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience())));
        }
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

}
