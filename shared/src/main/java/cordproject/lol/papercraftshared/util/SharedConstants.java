package cordproject.lol.papercraftshared.util;

public class SharedConstants {
    public static final String PREFS_NAME = "prefs_name";
    public static final String KEY_HIGH_SCORE = "high_score";
    public static final String KEY_SOUND_PREFERENCE = "sound_preference";
    public static final String KEY_GPG_SIGNED_OUT = "gpg_signed_out";

    // achievements

    public static final String ACH_ONE_HUNDRED = "ach_one_hundred";
    public static final String ACH_ONE_THOUSAND = "ach_one_thousand";
    public static final String ACH_TEN_THOUSAND = "ach_ten_thousand";

    public static final String ACH_PACIFIST = "ach_pacifist";
    public static final String ACH_BACK_STABBER = "ach_back_stabber";
    public static final String ACH_FIN = "ach_fin";
    public static final String ACH_SITTING_DUCK = "ach_sitting_duck";
    public static final String ACH_BAD_PILOT = "ach_bad_pilot";

    public static final String ACH_CUATRO = "ach_cuatro";
    public static final String ACH_RE_OCHO = "ach_re_ocho";
    public static final String ACH_OCHO = "ach_ocho";
    public static final String ACH_PRETTY_AWESOME_MAN = "ach_pretty_awesome_man";

    public static final int STATUS_MASK = 0x00FF_0000;
    public static final int NOT_ACHIEVED = 0x0000_0000;
    public static final int ACHIEVED = 0x0001_0000;
    public static final int SENT_TO_SERVER = 0x0010_0000;
    public static final int NOT_SENT_TO_BACKEND = 0x0000_0000;

    public static final int TYPE_MASK = 0x0F00_0000;
    public static final int TYPE_INCREMENTAL = 0x0200_0000;
    public static final int TYPE_SINGLE = 0x0100_0000;

    public static final int VALUE_MASK = 0x0000_FFFF;

    public static final String DATAPATH_ALL_DATA = "/alldata";
    public static final String DATAPATH_HIGH_SCORE = "/high_score";
    public static final String MESSAGEPATH_ACHIEVEMENT_SENT = "/message/achievement";

    public static final String MAP_KEY_HIGH_SCORE = "high_score";
    public static final String MAP_KEY_TIMESTAMP = "timestamp";
    public static final String MAP_KEY_ACHIEVEMENT_DATA = "achievement_data";
    public static final String MAP_KEY_COMBINED_VALUE = "combined_value";
    public static final String MAP_KEY_ACHIEVEMENT_ID = "achievement_id";
    public static final String MAP_KEY_ACHIEVEMENT_KEY = "achievement_key";

}
