package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.RandomGenerator;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ATargetAbility<T extends IBossAbility<T>> implements IBossAbility<T> {

    private final TargetType targetType;
    private static final RandomGenerator RANDOM_GENERATOR = new RandomGenerator();

    ATargetAbility(TargetType targetType) {
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
                int index = RANDOM_GENERATOR.getRandom().nextInt(0, players.size());
                activate(entityBoss, players.get(index));
            } else {
                Player target = null;
                for (Player player : players) {
                    if (targetType == TargetType.ALL_PLAYERS) {
                        activate(entityBoss, player);
                    } else {
                        if (target == null)
                            target = player;
                        else if ((targetType == TargetType.CLOSEST &&
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

    protected TargetType getTargetType() {
        return targetType;
    }

    public abstract void activate(EntityBoss entityBoss, LivingEntity target);
}
