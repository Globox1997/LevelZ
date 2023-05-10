package net.levelz.compat;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.brewing.DefaultBrewingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.init.ItemInit;
import net.levelz.screen.SkillInfoScreen;
import net.levelz.screen.SkillListScreen;
import net.levelz.screen.SkillScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class LevelzReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.add(new DefaultBrewingDisplay(EntryIngredients.of(Items.DRAGON_BREATH), EntryIngredients.of(Items.NETHER_STAR), EntryStacks.of(new ItemStack(ItemInit.STRANGE_POTION))));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new DisplayBoundsProvider<SkillScreen>() {

            @Override
            public <R extends Screen> ActionResult shouldScreenBeOverlaid(R screen) {
                return ActionResult.SUCCESS;
            }

            @Override
            public @Nullable Rectangle getScreenBounds(SkillScreen screen) {
                return new Rectangle(screen.width / 2 - (screen.getWidth() / 2), screen.height / 2 - (screen.getHeight() / 2), screen.getWidth(), screen.getHeight());
            }

            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
                if (screen.equals(SkillScreen.class))
                    return true;
                return false;
            }

        });

        registry.registerDecider(new DisplayBoundsProvider<SkillInfoScreen>() {

            @Override
            public <R extends Screen> ActionResult shouldScreenBeOverlaid(R screen) {
                return ActionResult.SUCCESS;
            }

            @Override
            public @Nullable Rectangle getScreenBounds(SkillInfoScreen screen) {
                return new Rectangle(screen.width / 2 - (screen.getWidth() / 2), screen.height / 2 - (screen.getHeight() / 2), screen.getWidth(), screen.getHeight());
            }

            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
                if (screen.equals(SkillInfoScreen.class))
                    return true;
                return false;
            }

        });

        registry.registerDecider(new DisplayBoundsProvider<SkillListScreen>() {

            @Override
            public <R extends Screen> ActionResult shouldScreenBeOverlaid(R screen) {
                return ActionResult.SUCCESS;
            }

            @Override
            public @Nullable Rectangle getScreenBounds(SkillListScreen screen) {
                return new Rectangle(screen.width / 2 - (screen.getWidth() / 2), screen.height / 2 - (screen.getHeight() / 2), screen.getWidth(), screen.getHeight());
            }

            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
                if (screen.equals(SkillListScreen.class))
                    return true;
                return false;
            }

        });
    }
}
