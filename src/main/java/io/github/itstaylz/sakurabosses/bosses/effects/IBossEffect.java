package io.github.itstaylz.sakurabosses.bosses.effects;

import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import org.bukkit.event.Event;

public interface IBossEffect<TEvent extends Event> {

    Class<TEvent> getEventClass();

    void activate(EntityBoss boss, TEvent event);
}
