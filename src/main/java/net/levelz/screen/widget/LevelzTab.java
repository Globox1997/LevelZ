package net.levelz.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.screen.LevelScreen;
import net.levelz.screen.SkillInfoScreen;
import net.levelz.screen.SkillRestrictionScreen;
import net.libz.api.InventoryTab;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class LevelzTab extends InventoryTab {

    public LevelzTab(Component title, ResourceLocation texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public boolean canClick(Class<?> screenClass, Minecraft client) {
        if (screenClass.equals(SkillInfoScreen.class) || screenClass.equals(SkillRestrictionScreen.class)) {
            return true;
        }
        return super.canClick(screenClass, client);
    }

    @Override
    public void onClick(Minecraft client) {
        client.setScreen(new LevelScreen());
    }

}
