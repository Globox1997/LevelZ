package net.levelz;

import net.levelz.init.ConfigInit;
import net.minecraft.resources.Identifier;

public class LevelzMain {

    public static final String MOD_ID = "levelz";

    public static void init() {
        ConfigInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.fromNamespaceAndPath("levelz", name);
    }
}

// You are LOVED!!!
// Jesus loves you unconditional!
