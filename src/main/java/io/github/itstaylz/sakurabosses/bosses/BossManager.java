package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.hexlib.utils.PDCUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class BossManager {

    private static final File BOSS_FOLDER;


    static {
        JavaPlugin plugin = JavaPlugin.getPlugin(SakuraBossesPlugin.class);
        BOSS_FOLDER = new File(plugin.getDataFolder(), "bosses");
        if (!BOSS_FOLDER.exists())
            BOSS_FOLDER.mkdirs();
    }

    private static final HashMap<String, BossData> BOSS_REGISTRY = new HashMap<>();
    private static final HashMap<UUID, EntityBoss> ENTITY_BOSS_REGISTRY = new HashMap<>();

    public static List<BossData> getAllBossData() {
        return new ArrayList<>(BOSS_REGISTRY.values());
    }

    public static void spawnBoss(String id, Location location) {
        BossData data = getBossData(id);
        if (data == null)
            throw new RuntimeException("No boss with the id '" + id + "'");
        EntityBoss entityBoss = new EntityBoss(data);
        entityBoss.spawn(location);
        ENTITY_BOSS_REGISTRY.put(entityBoss.getUniqueId(), entityBoss);
        PDCUtils.setPDCValue(entityBoss.getMobEntity(), BossDataKeys.ENTITY_BOSS_KEY, PersistentDataType.STRING, entityBoss.getBossData().id());
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

    public static void loadEntityBoss(Mob entity, String id) {
        if (ENTITY_BOSS_REGISTRY.containsKey(entity.getUniqueId()))
            return;
        BossData data = BOSS_REGISTRY.get(id);
        EntityBoss entityBoss = new EntityBoss(data, entity);
        ENTITY_BOSS_REGISTRY.put(entity.getUniqueId(), entityBoss);
    }

    public static boolean isMinion(Entity entity) {
        return PDCUtils.hasPDCValue(entity, BossDataKeys.BOSS_MINION_KEY, PersistentDataType.STRING);
    }

    public static EntityBoss getOwnedBoss(Entity entity) {
        UUID uuid = UUID.fromString(PDCUtils.getPDCValue(entity, BossDataKeys.BOSS_MINION_KEY, PersistentDataType.STRING));
        return ENTITY_BOSS_REGISTRY.get(uuid);
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

    public static Set<String> getAllIds() {
        return BOSS_REGISTRY.keySet();
    }

    public static Collection<EntityBoss> getAllActiveBossEntities() {
        return ENTITY_BOSS_REGISTRY.values();
    }

    public static void removeEntityBoss(UUID uuid) {
        ENTITY_BOSS_REGISTRY.remove(uuid);
    }
}
