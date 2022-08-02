package io.github.itstaylz.sakurabosses.bosses.data;

public enum TargetType {

    RANDOM,
    CLOSEST,
    LOWEST_HEALTH,
    HIGHEST_HEALTH,

    // Used for abilities only

    SELF,

    CURRENT_TARGET,
    ALL_PLAYERS
}
