package net.levelz.screen;

import java.util.ArrayList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.screen.SkillInfoScreens.*;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkillInfoScreen extends Screen implements Tab {

    public static final Identifier BACKGROUND_TEXTURE = new Identifier("levelz:textures/gui/skill_info_background.png");

    private final int backgroundWidth = 200;
    private final int backgroundHeight = 215;
    private int x;
    private int y;

    private final String title;

    private static final ArrayList<ISkillInfoScreen> infoScreens = new ArrayList<>();
    static {
        infoScreens.add(new HealthInfoScreen());
        infoScreens.add(new StrengthInfoScreen());
        infoScreens.add(new AgilityInfoScreen());
        infoScreens.add(new DefenseInfoScreen());
        infoScreens.add(new StaminaInfoScreen());
        infoScreens.add(new LuckInfoScreen());
        infoScreens.add(new ArcheryInfoScreen());
        infoScreens.add(new TradeInfoScreen());
        infoScreens.add(new SmithingInfoScreen());
        infoScreens.add(new MiningInfoScreen());
        infoScreens.add(new FarmingInfoScreen());
        infoScreens.add(new AlchemyInfoScreen());
    }

    public static void addScreen(ISkillInfoScreen screen){
        infoScreens.add(screen);
    }

    public SkillInfoScreen(String title) {
        super(Text.of(title));
        this.title = title;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        for(ISkillInfoScreen screen : infoScreens){
            if(screen.getStat().equals(title)){
                var widgets = screen.getWidgets(x, y, textRenderer);
                var listButton = screen.getSkillList(x, y, textRenderer, client);
                if(listButton != null){
                    this.addDrawableChild(listButton);
                }
                for(var widget : widgets){
                    this.addDrawableChild(widget);
                }
                return;
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(BACKGROUND_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        context.drawText(this.textRenderer, Text.translatable("text.levelz.info", Text.translatable(String.format("spritetip.levelz.%s_skill", this.title))), this.x + 6, this.y + 7, 0x3F3F3F, false);
        DrawTabHelper.drawTab(client, context, this, x, y, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            if (ConfigInit.CONFIG.switch_screen) {
                this.client.setScreen(new SkillScreen());
            } else {
                this.close();
            }
            return true;

        } else if (KeyInit.screenKey.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(new SkillScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DrawTabHelper.onTabButtonClick(client, this, this.x, this.y, mouseX, mouseY, false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public int getWidth() {
        return this.backgroundWidth;
    }

    public int getHeight() {
        return this.backgroundHeight;
    }

}
