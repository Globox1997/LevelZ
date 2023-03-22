package net.levelz.gui.widget;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ZWButton extends WButton {

    private int iconSize = 13;

    /**
     * 0: LEFT 1: RIGHT 2: MIDDLE
     */
    private int clickedKey = -1;

    private final List<OrderedText> tooltip = Lists.newArrayList();

    public ZWButton() {
        super();
        this.width = iconSize;
        this.height = iconSize;
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

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        if (getLabel() != null)
            tooltip.add(getLabel());
        for (OrderedText orderedText : this.tooltip) {
            tooltip.add(orderedText);
        }
    }

    public void addTooltip(String... string) {
        for (String s : string) {
            tooltip.add(Text.of(s).asOrderedText());
        }
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        clickedKey = button;
        InputResult result = super.onClick(x, y, button);
        clickedKey = -1;
        return result;
    }

    public boolean wasLeftButtonClicked() {
        return clickedKey == 0;
    }

    public boolean wasMiddleButtonClicked() {
        return clickedKey == 2;
    }

    public boolean wasRightButtonClicked() {
        return clickedKey == 1;
    }
}
