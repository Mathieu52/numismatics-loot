package net.xzera.registry;

public class DropInfo {
    private final int reward;
    private final float dropRate;

    public DropInfo(int reward, float dropRate) {
        this.reward = reward;
        this.dropRate = dropRate;
    }

    public int getReward() {
        return reward;
    }

    public float getDropRate() {
        return dropRate;
    }
}
