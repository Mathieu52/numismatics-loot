package net.xzera.config;

public enum RewardMode {
    HEALTH_REWARD_RATIO(1), DEFAULT_REWARD(0);

    private final float defaultValue;

    RewardMode(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public float getDefault() {
        return defaultValue;
    }
}
