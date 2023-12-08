package net.levelz.entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.levelz.access.PlayerSyncAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.EntityInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// Improvements inspiried by Clumps made by jaredlll08 which is licensed under MIT and can be found here:
// https://github.com/jaredlll08/Clumps/blob/1.19/Common/src/main/java/com/blamejared/clumps/mixin/MixinExperienceOrb.java

public class LevelExperienceOrbEntity extends Entity {

    private int orbAge;
    private int health = 5;
    private int amount;
    private int pickingCount = 1;
    private PlayerEntity target;
    private Map<Integer, Integer> clumpedMap;

    public LevelExperienceOrbEntity(World world, double x, double y, double z, int amount) {
        this(EntityInit.LEVEL_EXPERIENCE_ORB, world);
        this.setPosition(x, y, z);
        this.setYaw((float) (this.random.nextDouble() * 360.0));
        this.setVelocity((this.random.nextDouble() * (double) 0.2f - (double) 0.1f) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * (double) 0.2f - (double) 0.1f) * 2.0);
        this.amount = amount;
    }

    public LevelExperienceOrbEntity(EntityType<? extends LevelExperienceOrbEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void tick() {
        Vec3d vec3d;
        double d;
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        if (this.isSubmergedIn(FluidTags.WATER)) {
            this.applyWaterMovement();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
        }
        if (this.getWorld().getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA)) {
            this.setVelocity((this.random.nextFloat() - this.random.nextFloat()) * 0.2f, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        if (!this.getWorld().isSpaceEmpty(this.getBoundingBox())) {
            this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
        }
        if (this.age % 20 == 1) {
            this.expensiveUpdate();
        }
        if (this.target != null && (this.target.isSpectator() || this.target.isDead())) {
            this.target = null;
        }
        if (this.target != null
                && (d = (vec3d = new Vec3d(this.target.getX() - this.getX(), this.target.getY() + (double) this.target.getStandingEyeHeight() / 2.0 - this.getY(), this.target.getZ() - this.getZ()))
                        .lengthSquared()) < 64.0) {
            double e = 1.0 - Math.sqrt(d) / 8.0;
            this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(e * e * 0.1)));
        }
        this.move(MovementType.SELF, this.getVelocity());
        float vec3d2 = 0.98f;
        if (this.isOnGround()) {
            vec3d2 = this.getWorld().getBlockState(this.getBlockPos().down()).getBlock().getSlipperiness() * 0.98f;
        }
        this.setVelocity(this.getVelocity().multiply(vec3d2, 0.98, vec3d2));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
        }
        ++this.orbAge;
        if (this.orbAge >= 6000) {
            this.discard();
        }
    }

    private void expensiveUpdate() {
        if (this.target == null || this.target.squaredDistanceTo(this) > 64.0) {
            this.target = this.getWorld().getClosestPlayer(this, 8.0);
        }
        if (this.getWorld() instanceof ServerWorld) {
            List<LevelExperienceOrbEntity> list = this.getWorld().getEntitiesByType(TypeFilter.instanceOf(LevelExperienceOrbEntity.class), this.getBoundingBox().expand(0.5), this::isMergeable);
            for (LevelExperienceOrbEntity experienceOrbEntity : list) {
                this.merge(experienceOrbEntity);
            }
        }
    }

    public static void spawn(ServerWorld world, Vec3d pos, int amount) {
        if (!ConfigInit.CONFIG.useIndependentExp) {
            return;
        }
        while (amount > 0) {
            int i = LevelExperienceOrbEntity.roundToOrbSize(amount);
            amount -= i;
            if (LevelExperienceOrbEntity.wasMergedIntoExistingOrb(world, pos, i)) {
                continue;
            }
            world.spawnEntity(new LevelExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), i));
        }
    }

    private static boolean wasMergedIntoExistingOrb(ServerWorld world, Vec3d pos, int amount) {
        Box box = Box.of(pos, 1.0, 1.0, 1.0);
        int i = world.getRandom().nextInt(40);
        List<LevelExperienceOrbEntity> list = world.getEntitiesByType(TypeFilter.instanceOf(LevelExperienceOrbEntity.class), box, orb -> LevelExperienceOrbEntity.isMergeable(orb, i, amount));
        if (!list.isEmpty()) {
            LevelExperienceOrbEntity experienceOrbEntity = list.get(0);
            Map<Integer, Integer> clumpedMap = experienceOrbEntity.getClumpedMap();
            experienceOrbEntity.setClumpedMap(Stream.of(clumpedMap, Collections.singletonMap(amount, 1)).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
            experienceOrbEntity.pickingCount = clumpedMap.values().stream().reduce(Integer::sum).orElse(1);
            experienceOrbEntity.orbAge = 0;
            return true;
        }
        return false;
    }

    private boolean isMergeable(LevelExperienceOrbEntity other) {
        return other.isAlive() && other != this;
    }

    private static boolean isMergeable(LevelExperienceOrbEntity orb, int seed, int amount) {
        return orb.isAlive();
    }

    private void merge(LevelExperienceOrbEntity other) {
        Map<Integer, Integer> otherMap = other.getClumpedMap();
        setClumpedMap(Stream.of(getClumpedMap(), otherMap).flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
        this.pickingCount = getClumpedMap().values().stream().reduce(Integer::sum).orElse(1);
        this.orbAge = Math.min(this.orbAge, other.orbAge);
        other.discard();
    }

    private void applyWaterMovement() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double) 0.99f, Math.min(vec3d.y + (double) 5.0E-4f, (double) 0.06f), vec3d.z * (double) 0.99f);
    }

    @Override
    protected void onSwimmingStart() {
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.getWorld().isClient()) {
            return true;
        }
        this.scheduleVelocityUpdate();
        this.health = (int) ((float) this.health - amount);
        if (this.health <= 0) {
            this.discard();
        }
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Health", (short) this.health);
        nbt.putShort("Age", (short) this.orbAge);
        nbt.putShort("Value", (short) this.amount);
        nbt.putInt("Count", this.pickingCount);

        NbtCompound map = new NbtCompound();
        getClumpedMap().forEach((value, count) -> map.putInt(value + "", count));
        nbt.put("clumpedMap", map);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.health = nbt.getShort("Health");
        this.orbAge = nbt.getShort("Age");
        this.amount = nbt.getShort("Value");
        this.pickingCount = Math.max(nbt.getInt("Count"), 1);

        Map<Integer, Integer> map = new HashMap<>();
        if (nbt.contains("clumpedMap")) {
            NbtCompound clumpedMap = nbt.getCompound("clumpedMap");
            for (String s : clumpedMap.getKeys()) {
                map.put(Integer.parseInt(s), clumpedMap.getInt(s));
            }
        } else {
            map.put(this.amount, this.pickingCount);
        }
        setClumpedMap(map);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.getWorld().isClient() && player.experiencePickUpDelay == 0 && this.orbAge > 20) {
            player.experiencePickUpDelay = 2;
            player.sendPickup(this, 1);
            getClumpedMap().forEach((value, amount) -> {
                ((PlayerSyncAccess) player).addLevelExperience(value * amount);
            });
            this.discard();
        }

    }

    public int getExperienceAmount() {
        return this.amount;
    }

    public int getOrbSize() {
        if (this.amount >= 2477) {
            return 10;
        }
        if (this.amount >= 1237) {
            return 9;
        }
        if (this.amount >= 617) {
            return 8;
        }
        if (this.amount >= 307) {
            return 7;
        }
        if (this.amount >= 149) {
            return 6;
        }
        if (this.amount >= 73) {
            return 5;
        }
        if (this.amount >= 37) {
            return 4;
        }
        if (this.amount >= 17) {
            return 3;
        }
        if (this.amount >= 7) {
            return 2;
        }
        if (this.amount >= 3) {
            return 1;
        }
        return 0;
    }

    public static int roundToOrbSize(int value) {
        if (value >= 2477) {
            return 2477;
        }
        if (value >= 1237) {
            return 1237;
        }
        if (value >= 617) {
            return 617;
        }
        if (value >= 307) {
            return 307;
        }
        if (value >= 149) {
            return 149;
        }
        if (value >= 73) {
            return 73;
        }
        if (value >= 37) {
            return 37;
        }
        if (value >= 17) {
            return 17;
        }
        if (value >= 7) {
            return 7;
        }
        if (value >= 3) {
            return 3;
        }
        return 1;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new PlayerStatsServerPacket().createS2CLevelExperienceOrbPacket(this);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.AMBIENT;
    }

    private Map<Integer, Integer> getClumpedMap() {
        if (this.clumpedMap == null) {
            this.clumpedMap = new HashMap<>();
            this.clumpedMap.put(this.amount, 1);
        }
        return this.clumpedMap;
    }

    private void setClumpedMap(Map<Integer, Integer> map) {
        this.clumpedMap = map;
        this.amount = getClumpedMap().entrySet().stream().map(entry -> entry.getKey() * entry.getValue()).reduce(Integer::sum).orElse(1);
    }
}
