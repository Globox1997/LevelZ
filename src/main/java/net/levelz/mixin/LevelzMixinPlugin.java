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

        if (!FabricLoader.getInstance().isModLoaded("inventorio") && mixinClassName.contains("InventorioScreenMixin"))
            return false;

        if (!FabricLoader.getInstance().isModLoaded("create") && mixinClassName.contains("CreatePlayerStatsManagerMixin"))
            return false;

        if (FabricLoader.getInstance().isModLoaded("go-fish") && mixinClassName.contains("FishingRodItemMixin"))
            return false;

        if (FabricLoader.getInstance().isModLoaded("cardboard") && mixinClassName.contains("ArmorItemMixin"))
            return false;

        if (FabricLoader.getInstance().isModLoaded("limitless") && mixinClassName.contains("AnvilScreenHandler"))
            return false;

        if (!FabricLoader.getInstance().isModLoaded("identity") && mixinClassName.contains("PlayerIdentityMixin"))
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