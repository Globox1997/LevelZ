package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {

    public EnchantmentScreenMixin(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // Todo: Requires brain cells
//    @Inject(method = "drawBackground", at = @At(value = "TAIL"))
//    protected void drawBackgroundMixin(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
//        if (LevelLists.enchantingTableList != null && !LevelLists.enchantingTableList.isEmpty()) {
//            int i = (this.width - this.backgroundWidth) / 2;
//            int j = (this.height - this.backgroundHeight) / 2;
//            if (this.isPointWithinBounds(176, 0, 11, 13, (double) mouseX, (double) mouseY)) {
//                context.drawTexture(RenderInit.GUI_ICONS, i + 176, j, 33, 64, 11, 13);
//                List<Text> list = Lists.newArrayList();
//                String skill = Language.getInstance().get("spritetip.levelz.%s_skill".formatted(LevelLists.enchantingTableList.get(0)));
//                list.add((Text.translatable("container.levelz.enchanting_tier", 1, skill, LevelLists.enchantingTableList.get(4))).formatted(Formatting.WHITE));
//                list.add((Text.translatable("container.levelz.enchanting_tier", 2, skill, LevelLists.enchantingTableList.get(5))).formatted(Formatting.WHITE));
//                list.add((Text.translatable("container.levelz.enchanting_tier", 3, skill, LevelLists.enchantingTableList.get(6))).formatted(Formatting.WHITE));
//                context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
//            } else {
//                context.drawTexture(RenderInit.GUI_ICONS, i + 176, j, 22, 64, 11, 13);
//            }
//        }
//    }
}
