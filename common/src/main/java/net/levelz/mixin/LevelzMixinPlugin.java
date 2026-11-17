package net.levelz.mixin;

import net.levelz.platform.Services;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;


public class LevelzMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!Services.PLATFORM.isModLoaded("trinkets")
                && (mixinClassName.equals("net.levelz.mixin.compat.TrinketItemMixin") || mixinClassName.equals("net.levelz.mixin.compat.SurvivalTrinketSlotMixin")))
            return false;

        if (mixinClassName.contains("LevelManagerCompatMixin") && !Services.PLATFORM.isModLoaded("create") && !Services.PLATFORM.isModLoaded("computercraft"))
            return false;

        if (mixinClassName.contains("FishingRodItemMixin") && Services.PLATFORM.isModLoaded("go-fish"))
            return false;

        if (mixinClassName.contains("ArmorItemMixin") && Services.PLATFORM.isModLoaded("cardboard"))
            return false;

        if (mixinClassName.contains("AnvilScreenHandlerMixin") && Services.PLATFORM.isModLoaded("limitless"))
            return false;

        if (mixinClassName.contains("BackpackItemMixin") && !Services.PLATFORM.isModLoaded("inmis"))
            return false;

        if (mixinClassName.contains("EasyMagicEnchantingTableMixin") && !Services.PLATFORM.isModLoaded("easymagic"))
            return false;

        if (mixinClassName.contains("EasyAnvilsAnvilMixin") && !Services.PLATFORM.isModLoaded("easyanvils"))
            return false;

        if (mixinClassName.contains("CosmeticArmorMixin") && !Services.PLATFORM.isModLoaded("cosmetic-armor"))
            return false;

        if (mixinClassName.contains("ChopResultMixin") && !Services.PLATFORM.isModLoaded("treechop"))
            return false;

        if (mixinClassName.contains("SmithingAnvilScreenHandlerMixin") && !Services.PLATFORM.isModLoaded("alloygery"))
            return false;

        if (mixinClassName.contains("TieredCompatMixin") && Services.PLATFORM.isModLoaded("tiered"))
            return false;

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}