package net.levelz.waila;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;

public class LevelWailaPlugin implements IWailaPlugin {

    private static final List<LevelFeature> features = new ArrayList<>();

    @Override
    public void register(IRegistrar registrar) {
        features.add(new LevelWailaBlockInfo());
        features.forEach(feature -> feature.initialize(registrar));
    }

}
