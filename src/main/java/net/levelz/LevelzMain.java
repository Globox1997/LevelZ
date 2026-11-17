package net.levelz;

import net.fabricmc.api.ModInitializer;
import net.levelz.init.*;
import net.levelz.network.LevelServerPacket;
import net.minecraft.resources.Identifier;

public class LevelzMain implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandInit.init();
        CompatInit.init();
        ConfigInit.init();
        CriteriaInit.init();
        EntityInit.init();
        EventInit.init();
        LoaderInit.init();
        LevelServerPacket.init();
        TagInit.init();
        ItemInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.fromNamespaceAndPath("levelz", name);
    }
}

// You are LOVED!!!
// Jesus loves you unconditional!
