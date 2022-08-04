package io.github.itstaylz.sakurabosses.bosses.abilities;

import com.earth2me.essentials.Essentials;
import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.hexlib.utils.RandomUtils;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class TargetAbility<T extends IBossAbility<T>> implements IBossAbility<T> {

    private static final Essentials ESSENTIALS;

    static {
        ESSENTIALS = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    private final TargetType targetType;

    TargetAbility(TargetType targetType) {
        this.targetType = targetType;
    }

    protected TargetType loadTargetType(YamlFile yaml, String path) {
        return TargetType.valueOf(yaml.getOrDefault(path + ".target", "CURRENT_TARGET"));
    }

    @Override
    public void activate(EntityBoss entityBoss) {

        if (targetType == TargetType.SELF) {
            activate(entityBoss, entityBoss.getMobEntity());
        } else if (targetType == TargetType.CURRENT_TARGET) {
            LivingEntity target = entityBoss.getMobEntity().getTarget();
            if (target != null)
                activate(entityBoss, target);
        } else {
            List<Player> players = entityBoss.getPlayersInRadius();
            if (players.isEmpty())
                return;
            if (targetType == TargetType.RANDOM) {
                int index = RandomUtils.RANDOM.nextInt(0, players.size());
                activate(entityBoss, players.get(index));
            } else {
                Player target = null;
                for (Player player : players) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR ||
                            ESSENTIALS.getUser(player).isVanished() || player.isDead())
                        continue;
                    if (targetType == TargetType.ALL_PLAYERS) {
                        activate(entityBoss, player);
                    } else {
                        if (target == null)
                            target = player;
                        if ((targetType == TargetType.CLOSEST &&
                                player.getLocation().distance(entityBoss.getMobEntity().getLocation()) < target.getLocation().distance(entityBoss.getMobEntity().getLocation())) ||
                                (targetType == TargetType.HIGHEST_HEALTH && player.getHealth() > target.getHealth()) ||
                                (targetType == TargetType.LOWEST_HEALTH && player.getHealth() < target.getHealth())) {
                            target = player;
                        }
                    }
                }
                if (target != null)
                    activate(entityBoss, target);
            }
        }
    }

    public abstract void activate(EntityBoss entityBoss, LivingEntity target);
}
