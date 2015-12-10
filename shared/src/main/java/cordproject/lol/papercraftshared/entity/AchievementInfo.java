package cordproject.lol.papercraftshared.entity;

/**
 * Created by matthewlim on 11/16/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
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
