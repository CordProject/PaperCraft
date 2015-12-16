package cordproject.lol.papercraftshared.entity;

import android.content.res.Resources;



import cordproject.lol.papercraftshared.util.AchievementsUtil;
import cordproject.lol.papercraftshared.util.SharedConstants;


public class AchievementData {

    public int achievementResId;
    public int descriptionResId;
    public String prefsKey;
    public int status = SharedConstants.NOT_ACHIEVED;
    public int type = SharedConstants.TYPE_SINGLE;
    public int value;

    public int combinedValue;

    public AchievementData(String prefsKey) {
        this.prefsKey = prefsKey;
        AchievementInfo info = AchievementsUtil.getResIdsForAchievement(prefsKey);
        descriptionResId = info.descriptionResId;
        achievementResId = info.achievementResId;
        type = info.type;
        setCombinedValue();
    }

    public AchievementData(String prefsKey, int combinedValue) {
        this(prefsKey);
        this.combinedValue = combinedValue;
        type = combinedValue & SharedConstants.TYPE_MASK;
        value = combinedValue & SharedConstants.VALUE_MASK;
        status = combinedValue & SharedConstants.STATUS_MASK;
    }

    public void setType(int type) {
        this.type = type;
        setCombinedValue();
    }

    public void setStatus(int status) {
        this.status = status;
        setCombinedValue();
    }

    public void updateStatus(int updateMask) {
        status |= updateMask;
        setCombinedValue();
    }

    public void incrementBy(int increment) {
        this.value += increment;
        setCombinedValue();
    }

    public void setValue(int value) {
        this.value = value;
        setCombinedValue();
    }

    public int getStatus() {
        return combinedValue & SharedConstants.STATUS_MASK;
    }

    public static int getStatus(int combinedValue) {
        return combinedValue & SharedConstants.STATUS_MASK;
    }

    public int getValue() {
        return combinedValue & SharedConstants.VALUE_MASK;
    }

    private void setCombinedValue() {
        combinedValue = type | status | value;
    }

    public int getCombinedValue() {
        return combinedValue;
    }

    public int getType() {
        return combinedValue & SharedConstants.TYPE_MASK;
    }

    public String toString(Resources res) {
        String formaString = "type: %s desc: %s status: %s val: %d";
        String type = getType() == SharedConstants.TYPE_INCREMENTAL ? "INCREMENTAL" : getType() == SharedConstants.TYPE_SINGLE ? "SINGLE" : null;
        String status = getStatus() == SharedConstants.ACHIEVED ? "ACHIEVED" : getStatus()== SharedConstants.NOT_ACHIEVED ? "NOT ACHIEVED" :
                getStatus() == SharedConstants.SENT_TO_SERVER ? "SENT TO SERVER" : null;
        int value = getValue();
        return String.format(formaString, type, res.getString(descriptionResId), status, value);
    }
}
