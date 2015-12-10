package cordproject.lol.papercraftshared.util;

import android.content.SharedPreferences;

import java.util.HashMap;

import cordproject.lol.papercraftshared.R;
import cordproject.lol.papercraftshared.entity.AchievementData;
import cordproject.lol.papercraftshared.entity.AchievementInfo;


/**
 * Created by matthewlim on 11/16/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class AchievementsUtil {

    private static HashMap<String, AchievementInfo> achievementsMap = new HashMap();

    static {
        achievementsMap.put(SharedConstants.ACH_ONE_HUNDRED, new AchievementInfo(R.string.ach_one_hundred,
                R.string.ach_id_one_hundred, SharedConstants.TYPE_INCREMENTAL, 100));
        achievementsMap.put(SharedConstants.ACH_ONE_THOUSAND, new AchievementInfo(R.string.ach_one_thousand,
                R.string.ach_id_one_thousand, SharedConstants.TYPE_INCREMENTAL, 1000));
        achievementsMap.put(SharedConstants.ACH_TEN_THOUSAND, new AchievementInfo(R.string.ach_ten_thousand,
                R.string.ach_id_ten_thousand, SharedConstants.TYPE_INCREMENTAL, 10000));

        achievementsMap.put(SharedConstants.ACH_PACIFIST, new AchievementInfo(R.string.ach_pacifist,
                R.string.ach_id_pacifist, SharedConstants.TYPE_SINGLE));
        achievementsMap.put(SharedConstants.ACH_BACK_STABBER, new AchievementInfo(R.string.ach_back_stabber,
                R.string.ach_id_back_stabber, SharedConstants.TYPE_SINGLE));
        achievementsMap.put(SharedConstants.ACH_FIN, new AchievementInfo(R.string.ach_fin, R.string.ach_id_fin,
                SharedConstants.TYPE_SINGLE));

        achievementsMap.put(SharedConstants.ACH_SITTING_DUCK, new AchievementInfo(R.string.ach_sitting_duck,
                R.string.ach_id_sitting_duck, SharedConstants.TYPE_SINGLE));
        achievementsMap.put(SharedConstants.ACH_BAD_PILOT, new AchievementInfo(R.string.ach_bad_pilot,
                R.string.ach_id_bad_pilot, SharedConstants.TYPE_INCREMENTAL, 100));
        achievementsMap.put(SharedConstants.ACH_CUATRO, new AchievementInfo(R.string.ach_cuatro,
                R.string.ach_id_cuatro, SharedConstants.TYPE_SINGLE));

        achievementsMap.put(SharedConstants.ACH_OCHO, new AchievementInfo(R.string.ach_ocho,
                R.string.ach_id_ocho, SharedConstants.TYPE_SINGLE));
        achievementsMap.put(SharedConstants.ACH_RE_OCHO, new AchievementInfo(R.string.ach_re_ocho,
                R.string.ach_id_re_ocho, SharedConstants.TYPE_SINGLE));

        achievementsMap.put(SharedConstants.ACH_PRETTY_AWESOME_MAN, new AchievementInfo(R.string.ach_pretty_awesome,
                R.string.ach_id_pretty_awesome, SharedConstants.TYPE_SINGLE));
    }

    public static AchievementInfo getResIdsForAchievement(String key) {
        return achievementsMap.get(key);
    }

    public static void markAchievementsAchieved(SharedPreferences prefs) {
        for (String key : achievementsMap.keySet()) {
            if(prefs.contains(key)) {
                int combinedValue = prefs.getInt(key, -1);
                if (AchievementData.getStatus(combinedValue) >= SharedConstants.ACHIEVED) {
                    achievementsMap.get(key).alreadyAchieved = true;
                }
            }
        }
    }

    public static void recordAchievementProgress(SharedPreferences prefs, HashMap<String, AchievementData> currentAchievements) {
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : currentAchievements.keySet()) {
            editor.putInt(key, currentAchievements.get(key).combinedValue);
        }
        editor.apply();
    }

    public static void unpackInProgressAchievements(SharedPreferences prefs, HashMap<String, AchievementData> currentAchievements) {

        for (String key : achievementsMap.keySet()) {
            if (prefs.contains(key)){
                AchievementData data = new AchievementData(key, prefs.getInt(key, SharedConstants.NOT_ACHIEVED));

                if (data.getStatus() >= SharedConstants.ACHIEVED){
                    achievementsMap.get(key).alreadyAchieved = true;

                }
                if (data.getStatus() == SharedConstants.SENT_TO_SERVER) {
                    achievementsMap.get(key).sentToBackend = true;
                } else {
                    currentAchievements.put(key, data);
                }
            }
        }
    }

    public static boolean reachedMaxValue(String key, int value) {
        return value >= achievementsMap.get(key).maxValue;
    }

    public static void markAchievementAchieved(String key) {
        achievementsMap.get(key).alreadyAchieved = true;
    }

    public static void markAchievementSentToServer(String key) {
        achievementsMap.get(key).sentToBackend = true;
    }

    public static boolean achievementAlreadyAchieved(String key) {
        return achievementsMap.get(key).alreadyAchieved;
    }

    public static boolean achievementSentToServer(String key) {
        return achievementsMap.get(key).sentToBackend;
    }
}
