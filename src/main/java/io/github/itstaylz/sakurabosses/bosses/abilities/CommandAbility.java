package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class CommandAbility extends ATargetAbility<CommandAbility> {

    private final String command;


    CommandAbility() {
        this(TargetType.CLOSEST, null);
    }

    CommandAbility(TargetType targetType, String command) {
        super(targetType);
        this.command = command;
    }

    @Override
    public CommandAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        String command = yaml.getConfig().getString(path + ".command");
        return new CommandAbility(targetType, command);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        String command = this.command.replace("{TARGET}", target.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
