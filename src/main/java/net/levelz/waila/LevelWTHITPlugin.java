package net.levelz.waila;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.levelz.init.RenderInit;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

public class LevelWTHITPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(RenderInit.MINEABLE_INFO, true);
        registrar.addConfig(RenderInit.MINEABLE_LEVEL_INFO, false);
        registrar.addComponent(new LevelWTHITProvider(), TooltipPosition.BODY, Block.class);
        registrar.addComponent(new LevelEntityWTHITProvider(), TooltipPosition.BODY, Entity.class);
    }

}
