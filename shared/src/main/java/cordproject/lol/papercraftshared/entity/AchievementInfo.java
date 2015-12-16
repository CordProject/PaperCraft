package cordproject.lol.papercraftshared.entity;


public class AchievementInfo {
    public int descriptionResId;
    public int achievementResId;
    public int type;
    public boolean alreadyAchieved;
    public boolean sentToBackend;
    public int maxValue = 0;

    public AchievementInfo(int descriptionResId, int achievementResId, int type) {
        this.descriptionResId = descriptionResId;
        this.achievementResId = achievementResId;
        this.type = type;

    }

    public AchievementInfo(int descriptionResId, int achievementResId, int type, int maxValue) {
        this.descriptionResId = descriptionResId;
        this.achievementResId = achievementResId;
        this.type = type;
        this.maxValue = maxValue;

    }
}
