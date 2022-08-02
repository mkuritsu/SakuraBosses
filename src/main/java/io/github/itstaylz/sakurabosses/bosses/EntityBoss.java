package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.hexlib.utils.RandomUtils;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class EntityBoss {

    private Mob mobEntity;
    private BossData bossData;

    private final Deque<BossPhase> phases = new ArrayDeque<>();

    public EntityBoss(BossData bossData) {
        this(bossData, null);
    }

    public EntityBoss(BossData bossData, Mob entity) {
        this.mobEntity = entity;
        reloadData(bossData);
    }

    public void reloadData(BossData data) {
        this.bossData = data;
        this.phases.clear();
        this.phases.addAll(this.bossData.phases());
        if (this.mobEntity != null) {
            while (!this.phases.isEmpty() && this.phases.peek().minHealth() > this.mobEntity.getHealth()) {
                BossPhase phase = this.phases.pop();
                phase.start(this);
            }
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
                this.phases.pop();
                nextPhase.start(this);
            }
        }
    }

    public List<Player> getPlayersInRadius() {
        List<Player> players = new ArrayList<>();
        for (Entity entity : this.mobEntity.getNearbyEntities(bossData.settings().radius(), bossData.settings().radius(), bossData.settings().radius())) {
            if (entity instanceof Player player)
                players.add(player);
        }
        return players;
    }

    public void onDeath() {
        if (RandomUtils.isChanceSuccessful(this.bossData.weapon().dropChance())) {
            this.mobEntity.getWorld().dropItemNaturally(this.mobEntity.getLocation(), this.bossData.weapon().itemStack());
        }
    }

    public void updateTarget() {

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
