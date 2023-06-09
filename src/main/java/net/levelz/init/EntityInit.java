package net.levelz.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityInit {
    public static final boolean isRedstoneBitsLoaded = FabricLoader.getInstance().isModLoaded("redstonebits");

    public static final EntityType<LevelExperienceOrbEntity> LEVEL_EXPERIENCE_ORB = FabricEntityTypeBuilder.<LevelExperienceOrbEntity>create(SpawnGroup.MISC, LevelExperienceOrbEntity::new)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build();

    public static void init() {
        Registry.register(Registries.ENTITY_TYPE, new Identifier("levelz", "level_experience_orb"), LEVEL_EXPERIENCE_ORB);
    }

}
