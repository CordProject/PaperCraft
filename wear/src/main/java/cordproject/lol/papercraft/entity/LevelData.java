package cordproject.lol.papercraft.entity;


public class LevelData {
    private int scoreMinimum;
    private int killQuota;
    private float enemyTravelTime;
    private int scoreDecrement;
    private int shieldMode;
    private float enemyInterval;
    private float shieldProb;
    private float existenceProb;
    private boolean rotAngleRandom = false;
    private int maxConcurrentEnemies = 0;
    private int[] bitmapPalette;

    public LevelData(int scoreMinimum, int killQuota, float enemyTravelTime, int scoreDecrement,
                     int shieldMode, float enemyInterval, float shieldProb, float existenceProb,
                     boolean rotAngleRandom, int maxConcurrentEnemies) {
        this.scoreMinimum = scoreMinimum;
        this.killQuota = killQuota;
        this.enemyTravelTime = enemyTravelTime;
        this.scoreDecrement = scoreDecrement;
        this.shieldMode = shieldMode;
        this.enemyInterval = enemyInterval;
        this.shieldProb = shieldProb;
        this.existenceProb = existenceProb;
        this.rotAngleRandom = rotAngleRandom;
        this.maxConcurrentEnemies = maxConcurrentEnemies;
    }

    static final boolean TEST_ENDING = false;

    public int getScoreMinimum() {
        return scoreMinimum;
    }

    public int getKillQuota() {
        if (TEST_ENDING) {
            return 20;
        }
        return killQuota;
    }

    public float getEnemyTravelTime() {
        return enemyTravelTime;
    }

    public int getScoreDecrement() {
        return scoreDecrement;
    }

    public int getShieldMode() {
        return shieldMode;
    }

    public float getEnemyInterval() {
        return enemyInterval;
    }

    public float getShieldProb() {
        return shieldProb;
    }

    public float getExistenceProb() {
        return existenceProb;
    }

    public float getRotAngleRandom() {
        if (rotAngleRandom) {
            return (float) (-3.f + Math.random() * 6.f);
        }
        return 0;
    }

    public int getMaxConcurrentEnemies() {
        return maxConcurrentEnemies;
    }

    public int[] getBitmapPalette() {
        return bitmapPalette;
    }

    public void setBitmapPalette(int[] bitmapPalette) {
        this.bitmapPalette = bitmapPalette;
    }
}
