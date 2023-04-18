package net.levelz.gui.widget;

import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.RenderInit;
import net.levelz.network.PlayerStatsClientPacket;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class PlayerStatsPanel extends WPlainPanel {

    private final WSprite bar;
    private final WLabel xp;
    private final WLabel level;
    private final WLabel point;
    private ZWButton levelUp;
    private final PlayerStatsManager playerStatsManager;
    private long lastExperience = -1;

    public PlayerStatsPanel(MinecraftClient client, int width) {
        this.width = width;
        this.playerStatsManager = ((PlayerStatsManagerAccess) client.player).getPlayerStatsManager();
        level = new WLabel(Text.empty());
        this.add(level, 13, 2);
        point = new WLabel(Text.empty());
        this.add(point, 70, 2);
        WSprite barBg = new WSprite(RenderInit.GUI_ICONS, 0, 100 / 256F, 182 / 256F, 105 / 256F);
        this.add(barBg, 0, 14, width, 5);
        bar = new WSprite(RenderInit.GUI_ICONS, 0, 105 / 256F, 20 / 256F, 110 / 256F);
        this.add(bar, 0, 14, 0, 5);
        xp = new WLabel(Text.of("XP 0 / 0")).setHorizontalAlignment(HorizontalAlignment.CENTER);
        this.add(xp, 0, 24, width, 18);
        if (ConfigInit.CONFIG.useIndependentExp) {
            return;
        }
        levelUp = new ZWButton(Text.translatable("text.levelz.gui.level_up"), 10);
        levelUp.addTooltip(Text.translatable("text.levelz.gui.level_up.tooltip").getString().split("\n"));
        levelUp.setOnClick(() -> {
            int level = 1;
            if (levelUp.wasRightButtonClicked())
                level = 5;
            else if (levelUp.wasMiddleButtonClicked())
                level = 10;
            PlayerStatsClientPacket.writeC2SLevelUpPacket(level);
        });
        this.add(levelUp, 0, 0);
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.paint(matrices, x, y, mouseX, mouseY);
        point.setText(Text.translatable("text.levelz.gui.points", playerStatsManager.getSkillPoints()));
        int nextLevelExperience = playerStatsManager.getNextLevelExperience();
        float levelProgress = 0;
        long experience = 0;
        if (!ConfigInit.CONFIG.useIndependentExp) {
            experience = playerStatsManager.getNonIndependentExperience();
            levelProgress = Math.min((float) experience / nextLevelExperience, 1);
        } else {
            levelProgress = playerStatsManager.getLevelProgress();
            experience = (int) (nextLevelExperience * levelProgress);
        }
        if (lastExperience == experience)
            return;
        lastExperience = experience;

        level.setText(Text.translatable("text.levelz.gui.level", playerStatsManager.getOverallLevel()));
        if (levelUp != null) {
            levelUp.setEnabled(!playerStatsManager.isMaxLevel() && levelProgress >= 1);
        }
        bar.setSize((int) (levelProgress * (this.width - 1)), 5);
        // int w = (int) (this.width * levelProgress);
        bar.setFrames(new Texture(RenderInit.GUI_ICONS, 0, 105 / 256F, bar.getWidth() / 256F, 110 / 256F));
        xp.setText(Text.of("XP " + experience + " / " + nextLevelExperience));
    }
}
