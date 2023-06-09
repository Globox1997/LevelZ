package net.levelz.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

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
        if (!FabricLoader.getInstance().isModLoaded("trinkets")
                && (mixinClassName.equals("net.levelz.mixin.compat.TrinketItemMixin") || mixinClassName.equals("net.levelz.mixin.compat.SurvivalTrinketSlotMixin")))
            return false;

        if (!FabricLoader.getInstance().isModLoaded("create") && !FabricLoader.getInstance().isModLoaded("computercraft") && mixinClassName.contains("PlayerStatsManagerCompatMixin"))
            return false;

        if (mixinClassName.contains("FishingRodItemMixin") && FabricLoader.getInstance().isModLoaded("go-fish"))
            return false;

        if (mixinClassName.contains("ArmorItemMixin") && FabricLoader.getInstance().isModLoaded("cardboard"))
            return false;

        if (mixinClassName.contains("AnvilScreenHandlerMixin") && FabricLoader.getInstance().isModLoaded("limitless"))
            return false;

        if (mixinClassName.contains("DualWieldingOffhandAttackMixin") && !FabricLoader.getInstance().isModLoaded("dualwielding"))
            return false;

        if (mixinClassName.contains("BackpackItemMixin") && !FabricLoader.getInstance().isModLoaded("inmis"))
            return false;

        if (mixinClassName.contains("EasyMagicEnchantingTableMixin") && !FabricLoader.getInstance().isModLoaded("easymagic"))
            return false;

        if (mixinClassName.contains("EasyAnvilsAnvilMixin") && !FabricLoader.getInstance().isModLoaded("easyanvils"))
            return false;

        if (mixinClassName.contains("CosmeticArmorMixin") && !FabricLoader.getInstance().isModLoaded("cosmetic-armor"))
            return false;

        if (mixinClassName.contains("ChopResultMixin") && !FabricLoader.getInstance().isModLoaded("treechop"))
            return false;

        if (mixinClassName.contains("SmithingAnvilScreenHandlerMixin") && !FabricLoader.getInstance().isModLoaded("alloygery"))
            return false;

        if (mixinClassName.contains("BlockBreakerMixin") && !FabricLoader.getInstance().isModLoaded("magna"))
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