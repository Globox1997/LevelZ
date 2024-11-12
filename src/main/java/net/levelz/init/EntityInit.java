package net.levelz.init;

import net.fabricmc.loader.api.FabricLoader;
import net.levelz.LevelzMain;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityInit {

    public static final boolean isRedstoneBitsLoaded = FabricLoader.getInstance().isModLoaded("redstonebits");

    public static final EntityType<LevelExperienceOrbEntity> LEVEL_EXPERIENCE_ORB = EntityType.Builder.<LevelExperienceOrbEntity>create(LevelExperienceOrbEntity::new, SpawnGroup.MISC)
            .dimensions(0.5F, 0.5F).build();

    public static void init() {
        Registry.register(Registries.ENTITY_TYPE, LevelzMain.identifierOf("level_experience_orb"), LEVEL_EXPERIENCE_ORB);
    }

}
