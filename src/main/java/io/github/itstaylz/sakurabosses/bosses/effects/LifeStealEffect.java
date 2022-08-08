package io.github.itstaylz.sakurabosses.bosses.effects;

import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.events.BossDamagePlayerEvent;

public class LifeStealEffect implements IBossEffect<BossDamagePlayerEvent> {

    private double multiplier;

    public LifeStealEffect(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public Class<BossDamagePlayerEvent> getEventClass() {
        return BossDamagePlayerEvent.class;
    }

    @Override
    public void activate(EntityBoss boss, BossDamagePlayerEvent event) {
        double damage = event.getEntityDamageEvent().getDamage();
        double health = boss.getMobEntity().getHealth();
        double newHealth = Math.min(health + damage * this.multiplier, boss.getBossData().settings().maxHealth());
        boss.getMobEntity().setHealth(newHealth);
        boss.updateHealth();
    }
}
