package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.hexlib.utils.EntityUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class BossManager {

    private static final File BOSS_FOLDER;

    public static final NamespacedKey ENTITY_BOSS_KEY;

    public static final NamespacedKey BOSS_SPAWN_EGG_KEY;

    public static final NamespacedKey BOSS_SPAWNED_ENTITY_KEY;

    static {
        JavaPlugin plugin = JavaPlugin.getPlugin(SakuraBossesPlugin.class);
        BOSS_FOLDER = new File(plugin.getDataFolder(), "bosses");
        ENTITY_BOSS_KEY = new NamespacedKey(plugin, "entity_boss");
        BOSS_SPAWN_EGG_KEY = new NamespacedKey(plugin, "boss_spawn_egg");
        BOSS_SPAWNED_ENTITY_KEY = new NamespacedKey(plugin, "boss_spawned");
    }

    private static final HashMap<String, BossData> BOSS_REGISTRY = new HashMap<>();
    private static final HashMap<UUID, EntityBoss> ENTITY_BOSS_REGISTRY = new HashMap<>();

    public static List<BossData> getAllBossData() {
        return new ArrayList<>(BOSS_REGISTRY.values());
    }

    public static void loadBosses() {
        BOSS_REGISTRY.clear();
        File[] files = BOSS_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                BossData data = BossData.loadFromFile(file);
                if (data != null)
                    BOSS_REGISTRY.put(data.id(), data);
            }
        }
        for (EntityBoss entityBoss : ENTITY_BOSS_REGISTRY.values()) {
            BossData newData = BOSS_REGISTRY.get(entityBoss.getBossData().id());
            if (newData != null)
                entityBoss.reloadData(newData);
        }
    }

    public static void setBossSpawnedEntity(Entity entity) {
        EntityUtils.setPDCValue(entity, BOSS_SPAWNED_ENTITY_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isBossSpawnedEntity(Entity entity) {
        return EntityUtils.hasPDCValue(entity, BOSS_SPAWNED_ENTITY_KEY, PersistentDataType.BYTE);
    }

    public static void loadEntityBoss(Mob entity, String id) {
        if (ENTITY_BOSS_REGISTRY.containsKey(entity.getUniqueId()))
            return;
        BossData data = BOSS_REGISTRY.get(id);
        EntityBoss entityBoss = new EntityBoss(data, entity);
        ENTITY_BOSS_REGISTRY.put(entity.getUniqueId(), entityBoss);
    }

    public static void spawnBoss(String id, Location location) {
        BossData data = getBossData(id);
        if (data == null)
            throw new RuntimeException("No boss with the id '" + id + "'");
        EntityBoss entityBoss = new EntityBoss(data);
        entityBoss.spawn(location);
        ENTITY_BOSS_REGISTRY.put(entityBoss.getUniqueId(), entityBoss);
        EntityUtils.setPDCValue(entityBoss.getMobEntity(), BossManager.ENTITY_BOSS_KEY, PersistentDataType.STRING, entityBoss.getBossData().id());
    }

    public static boolean isBoss(Entity entity) {
        return ENTITY_BOSS_REGISTRY.containsKey(entity.getUniqueId());
    }

    public static BossData getBossData(String id) {
        return BOSS_REGISTRY.get(id);
    }

    public static EntityBoss getEntityBoss(UUID entityUuid) {
        return ENTITY_BOSS_REGISTRY.get(entityUuid);
    }

    public static void removeTarget(Player player) {
        for (EntityBoss boss : ENTITY_BOSS_REGISTRY.values()) {
            if (boss.getMobEntity() != null && boss.getMobEntity().getTarget() != null && boss.getMobEntity().getTarget().equals(player)) {
                boss.getMobEntity().setTarget(null);
            }
        }
    }

    public static void removeEntityBoss(UUID uuid) {
        ENTITY_BOSS_REGISTRY.remove(uuid);
    }
}
