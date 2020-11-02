package edu.wm.cs.cs301.ShuhongWang.generation;

public class DataHolder {
    private Order.Builder builder;
    private int skillLevel;
    private boolean isPerfect;
    private int seed;
    private Maze mazeConfig;

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}

    public boolean isPerfect() {
        return isPerfect;
    }

    public void setPerfect(boolean perfect) {
        isPerfect = perfect;
    }

    public Order.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Order.Builder builder) {
        this.builder = builder;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public Maze getMazeConfig() {
        return mazeConfig;
    }

    public void setMazeConfig(Maze mazeConfig) {
        this.mazeConfig = mazeConfig;
    }
}
