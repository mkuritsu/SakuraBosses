package io.github.itstaylz.sakurabosses.bosses.effects;

import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.events.PlayerDamageBossEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ImmunityEffect implements IBossEffect<PlayerDamageBossEvent> {

    private final List<String> ignoreList;

    public ImmunityEffect(List<String> ignore) {
        this.ignoreList = ignore;
    }

    @Override
    public Class<PlayerDamageBossEvent> getEventClass() {
        return PlayerDamageBossEvent.class;
    }

    @Override
    public void activate(EntityBoss boss, PlayerDamageBossEvent event) {
        EntityDamageByEntityEvent entityDamageEvent = event.getEntityDamageEvent();
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        for (String ignore : this.ignoreList) {
            if (item.getType().name().contains(ignore)) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
