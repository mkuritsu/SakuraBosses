package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.hexlib.utils.RandomUtils;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import io.github.itstaylz.sakurabosses.bosses.effects.IBossEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EntityBoss {

    private Mob mobEntity;
    private BossData bossData;

    private final Queue<BossPhase> phases = new ArrayDeque<>();

    private final List<IBossEffect<?>> activeEffects = new ArrayList<>();

    public EntityBoss(BossData bossData) {
        this(bossData, null);
    }

    public EntityBoss(BossData bossData, Mob entity) {
        this.mobEntity = entity;
        reloadData(bossData);
    }

    public void activateEffect(IBossEffect<?> effect) {
        activateEffect(effect, -1);
    }

    public void activateEffect(IBossEffect<?> effect, int duration) {
        Bukkit.broadcastMessage("Activating effect: " + effect.getClass().getName() + " for: " + duration + " ticks!");
        activeEffects.add(effect);
        if (duration > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    deactivateEffect(effect);
                }
            }.runTaskLater(JavaPlugin.getProvidingPlugin(SakuraBossesPlugin.class), duration);
        }
    }

    public void deactivateEffect(IBossEffect<?> effect) {
        Bukkit.broadcastMessage("Deactivating effect: " + effect.getClass().getName());
        activeEffects.remove(effect);
    }

    public <TEvent extends Event> void triggerEffects(TEvent event) {
        for (IBossEffect<?> effect : activeEffects) {
            if (event.getClass().equals(effect.getEventClass()))
                ((IBossEffect<TEvent>) effect).activate(this, event);
        }
    }

    public void reloadData(BossData data) {
        this.activeEffects.clear();
        this.bossData = data;
        this.phases.clear();
        this.phases.addAll(this.bossData.phases());
        while (!this.phases.isEmpty() && this.phases.peek().minHealth() > this.bossData.settings().maxHealth())
            this.phases.poll();
        if (this.mobEntity != null) {
            this.mobEntity.setHealth(this.bossData.settings().maxHealth());
            updateHealthBar();
        }
    }

    public void spawn(Location location) {
        this.mobEntity = (Mob) location.getWorld().spawnEntity(location, this.bossData.settings().entityType());
        AttributeInstance healthAttribute = this.mobEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(bossData.settings().maxHealth());
        this.mobEntity.getEquipment().setItemInMainHand(this.bossData.weapon().itemStack());
        this.mobEntity.getEquipment().setHelmet(this.bossData.helmet().itemStack());
        this.mobEntity.getEquipment().setChestplate(this.bossData.chestplate().itemStack());
        this.mobEntity.getEquipment().setLeggings(this.bossData.leggings().itemStack());
        this.mobEntity.getEquipment().setBoots(this.bossData.boots().itemStack());
        this.mobEntity.setCustomNameVisible(true);
        this.mobEntity.setHealth(healthAttribute.getValue());
        EntityUtils.setPDCValue(this.mobEntity, BossManager.ENTITY_BOSS_KEY, PersistentDataType.STRING, this.bossData.id());
        updateHealthBar();
    }

    public void updateHealthBar() {
        String health = String.format("%.1f", this.mobEntity.getHealth());
        this.mobEntity.setCustomName(StringUtils.colorize(this.bossData.settings().displayName() + " &7| &c" + health + " &4♥"));
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
        List<Player> players = new ArrayList<>();
        for (Entity entity : this.mobEntity.getNearbyEntities(bossData.settings().radius(), bossData.settings().radius(), bossData.settings().radius())) {
            if (entity instanceof Player player && player.getGameMode() != GameMode.CREATIVE
                    && player.getGameMode() != GameMode.SURVIVAL && !SakuraBossesPlugin.essentials.getUser(player).isVanished()
                    && !player.isDead())
                players.add(player);
        }
        return players;
    }

    public void onDeath() {
        if (RandomUtils.isChanceSuccessful(this.bossData.weapon().dropChance())) {
            this.mobEntity.getWorld().dropItemNaturally(this.mobEntity.getLocation(), this.bossData.weapon().itemStack());
            BossManager.removeEntityBoss(getUniqueId());
        }
    }

    public void updateTarget() {
        TargetType targetType = this.bossData.settings().targetType();
        double radius = this.bossData.settings().radius();
        Player target = null;
        double targetDistance = 0;
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Entity entity : this.mobEntity.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player player && player.getGameMode() != GameMode.CREATIVE
                    && player.getGameMode() != GameMode.SPECTATOR && !SakuraBossesPlugin.essentials.getUser(player).isVanished()
                    && !player.isDead()) {

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
        //Bukkit.broadcastMessage(StringUtils.colorize("DEBUG > Target updated for " + this.mobEntity.getName() + "&r ->> " + target));
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
