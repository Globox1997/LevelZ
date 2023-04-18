package net.levelz.gui.widget;

import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.RenderInit;
import net.levelz.network.PlayerStatsClientPacket;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkillPanel extends WPlainPanel {

    private final WLabel label;
    private final ZWButton button;
    private final Skill skill;
    private final PlayerStatsManager playerStatsManager;

    public SkillPanel(MinecraftClient client, Skill skill, int width, int height) {
        this.width = width;
        this.height = height;
        this.skill = skill;
        String skillName = skill.name().toLowerCase();
        this.playerStatsManager = ((PlayerStatsManagerAccess) client.player).getPlayerStatsManager();
        ZWSprite icon = new ZWSprite(skillName, RenderInit.GUI_ICONS, client, (skill.getId() - 1) / 16F, 1F / 16F, skill.getId() / 16F, 2F / 16F);
        icon.addText(Text.translatable("spritetip.levelz." + skillName + "_skill").getString());
        for (int i = 1; i < 10; i++) {
            String key = "spritetip.levelz." + skillName + "_skill_info_" + i;
            String translatable = Text.translatable(key).getString();
            if (!key.equals(translatable)) {
                icon.addText(translatable);
            }
        }
        this.add(icon, 0, 0, height, height);
        button = new ZWButton();
        button.setOnClick(() -> {
            int level = 1;
            if (button.wasRightButtonClicked()) {
                level = 5;
            } else if (button.wasMiddleButtonClicked()) {
                level = 10;
            }
            PlayerStatsClientPacket.writeC2SIncreaseLevelPacket(this.playerStatsManager, this.skill, level);
        });
        this.add(button, width - button.getWidth(), (height - button.getHeight()) / 2);
        label = new WLabel(Text.of("0/0")).setHorizontalAlignment(HorizontalAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.CENTER);
        this.add(label, icon.getWidth(), 0, (int) ((width - icon.getWidth() - button.getWidth()) * 0.8), height + 2);
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.paint(matrices, x, y, mouseX, mouseY);
        this.label.setText(Text.of(playerStatsManager.getSkillLevel(this.skill) + "/" + ConfigInit.CONFIG.maxLevel));
        boolean enoughPoints = playerStatsManager.getSkillPoints() > 0;
        this.button.setEnabled(enoughPoints && playerStatsManager.getSkillLevel(this.skill) < ConfigInit.CONFIG.maxLevel);
    }
}
