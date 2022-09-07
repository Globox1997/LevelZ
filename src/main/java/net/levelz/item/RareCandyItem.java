package net.levelz.item;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

// Texture made by Pois1x
public class RareCandyItem extends Item {

    public RareCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (!user.isCreative())
                stack.decrement(1);
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) user).getPlayerStatsManager();
            ((PlayerSyncAccess) user).addLevelExperience(playerStatsManager.getNextLevelExperience() - ((int) (playerStatsManager.levelProgress * playerStatsManager.getNextLevelExperience())));
        }
        return TypedActionResult.success(stack, world.isClient());
    }

}
