package io.github.itstaylz.sakurabosses.events;

import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BossDamagePlayerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private EntityDamageByEntityEvent entityDamageEvent;
    private Player player;
    private EntityBoss entityBoss;

    public BossDamagePlayerEvent(EntityDamageByEntityEvent event, Player player, EntityBoss boss) {
        this.entityDamageEvent = event;
        this.player = player;
        this.entityBoss = boss;
    }

    public EntityBoss getEntityBoss() {
        return entityBoss;
    }

    public EntityDamageByEntityEvent getEntityDamageEvent() {
        return entityDamageEvent;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.entityDamageEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.entityDamageEvent.setCancelled(cancel);
    }
}
