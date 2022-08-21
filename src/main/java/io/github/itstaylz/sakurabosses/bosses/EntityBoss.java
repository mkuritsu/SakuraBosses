package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.hexlib.utils.RandomUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.abilities.RandomAbility;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import io.github.itstaylz.sakurabosses.bosses.data.BossEquipmentItem;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import io.github.itstaylz.sakurabosses.bosses.effects.IBossEffect;
import io.github.itstaylz.sakurabosses.utils.HealthBarUtils;
import io.github.itstaylz.sakurabosses.utils.MobEntityUtils;
import io.github.itstaylz.sakurabosses.utils.TargetUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class EntityBoss {

    private Mob mobEntity;
    private BossData bossData;

    private final Queue<BossPhase> phases = new ArrayDeque<>();

    private final HashMap<Class<? extends IBossEffect>, IBossEffect> activeEffects = new HashMap<>();

    private BukkitTask task;

    public EntityBoss(BossData bossData) {
        this(bossData, null);
    }

    public EntityBoss(BossData bossData, Mob entity) {
        this.mobEntity = entity;
        this.bossData = bossData;
        //reloadData(bossData);
    }

    public void activateEffect(IBossEffect effect, int duration) {
        activeEffects.put(effect.getClass(), effect);
        if (duration > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    IBossEffect storedEffect = activeEffects.get(effect.getClass());
                    if (storedEffect == effect)
                        activeEffects.remove(effect.getClass());
                }
            }.runTaskLater(JavaPlugin.getProvidingPlugin(SakuraBossesPlugin.class), duration);
        }
    }

    public <T extends IBossEffect> boolean hasEffect(Class<T> clazz) {
        return activeEffects.containsKey(clazz);
    }

    public <T extends IBossEffect> T getActiveEffect(Class<T> clazz) {
        if (hasEffect(clazz))
            return (T) activeEffects.get(clazz);
        return null;
    }

    public void reloadData(BossData data) {
        this.activeEffects.clear();
        this.bossData = data;
        this.phases.clear();
        this.phases.addAll(this.bossData.phases());
        while (!this.phases.isEmpty() && this.phases.peek().minHealth() > this.bossData.settings().maxHealth())
            this.phases.poll();
        if (this.mobEntity != null) {
            this.mobEntity.setCustomNameVisible(true);
            this.mobEntity.setCustomName(this.bossData.settings().displayName());
            MobEntityUtils.setEquipment(this.mobEntity, this.bossData.equipment());
            MobEntityUtils.setMaxHealth(this.mobEntity, this.bossData.settings().maxHealth());
            updateHealth();
            if (this.task != null && !this.task.isCancelled())
                this.task.cancel();
            startAbilityTask();
        }
    }

    public void spawn(Location location) {
        this.mobEntity = (Mob) location.getWorld().spawnEntity(location, this.bossData.settings().entityType());
        reloadData(this.bossData);
    }

    public void updateHealth() {
        HealthBarUtils.updateHealthBar(this.mobEntity);
        if (!this.phases.isEmpty()) {
            BossPhase nextPhase = this.phases.peek();
            if (this.mobEntity.getHealth() <= nextPhase.minHealth()) {
                this.phases.poll();
                if (!this.mobEntity.isDead())
                    nextPhase.start(this);
            }
        }
    }

    public List<Player> getPlayersInRadius() {
        return getPlayersInRadius(this.bossData.settings().radius());
    }

    public List<Player> getPlayersInRadius(double radius) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : this.mobEntity.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player player && TargetUtils.canBeTarget(player))
                players.add(player);
        }
        return players;
    }

    public void onDeath() {
        for (BossEquipmentItem equipment : this.bossData.equipment()) {
            if (equipment.itemStack().getType() != Material.AIR && RandomUtils.isChanceSuccessful(equipment.dropChance())) {
                this.mobEntity.getWorld().dropItemNaturally(this.mobEntity.getLocation(), equipment.itemStack());
            }
        }
        BossManager.removeEntityBoss(getUniqueId());
    }

    public Entity spawnMinion(Location location, EntityType type) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        EntityUtils.setPDCValue(entity, BossDataKeys.BOSS_MINION_KEY, PersistentDataType.STRING, this.mobEntity.getUniqueId().toString());
        return entity;
    }

    public void updateTarget() {
        TargetType targetType = this.bossData.settings().targetType();
        double radius = this.bossData.settings().radius();
        Player target = null;
        double targetDistance = 0;
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Entity entity : this.mobEntity.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player player && TargetUtils.canBeTarget(player)) {

                if (target == null) {
                    target = player;
                    targetDistance = player.getLocation().distance(this.mobEntity.getLocation());
                    continue;
                }

                switch (targetType) {
                    case RANDOM -> nearbyPlayers.add(player);
                    case HIGHEST_HEALTH -> target = player.getHealth() > target.getHealth() ? player : target;
                    case LOWEST_HEALTH -> target = player.getHealth() < target.getHealth() ? player : target;
                    default -> {
                        double distance = player.getLocation().distance(this.mobEntity.getLocation());
                        if (distance < targetDistance) {
                            target = player;
                            targetDistance = distance;
                        }
                    }
                }
            }
        }
        if (targetType == TargetType.RANDOM && !nearbyPlayers.isEmpty())
            target = nearbyPlayers.get(RandomUtils.RANDOM.nextInt(0, nearbyPlayers.size()));
        this.mobEntity.setTarget(target);
    }

    public void startAbilityTask() {
        if (this.bossData.settings().abilityTimer() <= 0 || this.bossData.abilities().isEmpty())
            return;
        EntityBoss boss = this;
        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                if (mobEntity.isDead()) {
                    task = null;
                    cancel();
                    return;
                }
                for (RandomAbility ability : bossData.abilities()) {
                    if (RandomUtils.isChanceSuccessful(ability.activationChance())) {
                        ability.activate(boss);
                        break;
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(SakuraBossesPlugin.class), 20L, this.bossData.settings().abilityTimer());
    }

    public UUID getUniqueId() {
        return this.mobEntity != null ? this.mobEntity.getUniqueId() : null;
    }

    public Mob getMobEntity() {
        return this.mobEntity;
    }

    public BossData getBossData() {
        return this.bossData;
    }

}
