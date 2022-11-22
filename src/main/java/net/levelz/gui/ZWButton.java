package net.levelz.gui;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZWButton extends WButton {

    private int iconSize = 13;

    public ZWButton() {
        super();
    }

    public ZWButton(Text text, int size) {
        super(text);
        this.width = size;
        this.height = size;
        this.iconSize = size;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        Icon icon;
        if (this.isEnabled()) {
            boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight());
            icon = new TextureIcon(new Identifier("levelz:textures/gui/plus_icon.png"));
            if (hovered) {
                icon = new TextureIcon(new Identifier("levelz:textures/gui/hovered_plus_icon.png"));
            }
        } else {
            icon = new TextureIcon(new Identifier("levelz:textures/gui/disabled_plus_icon.png"));
        }
        icon.paint(matrices, x + 1, y + 1, iconSize);
    }

    @Override
    public void setSize(int x, int y) {
        this.width = iconSize;
        this.height = iconSize;
    }

}
