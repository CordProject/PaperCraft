package cordproject.lol.papercraft.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cordproject.lol.papercraft.R;
import cordproject.lol.papercraft.entity.LevelData;
import cordproject.lol.papercraft.util.MathUtil;
import cordproject.lol.papercraftshared.entity.AchievementData;
import cordproject.lol.papercraftshared.util.AchievementsUtil;
import cordproject.lol.papercraftshared.util.SharedConstants;

/**
 * Created by matthewlim on 11/11/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class GameController extends Controller {

    public static final int L_0 = 0;


    public static final int L_8 = 8;


    public static final int START_LIVES = 3;

    private int currentLevel = L_0;
    private int remainingLives = START_LIVES;
    private boolean playerAlive = false;
    private int currentKills;
    private int currentScore;
    private int gameState = TITLE;

    private int comboCount = 0;
    private int maxCombo = 0;

    private int highScore = 0;
    private long lastOchoTime = 0;
    private boolean backStabber = true;

    private int escapedEnemies;
    private int cumulativeEscapedEnemies;
    private int cumulativeDeaths;
    private int cumulativeKills;

    public static final int PAUSED = -1;
    public static final int PLAYING = 0;
    public static final int RESTARTING = 1;
    public static final int TRANSITIONING_TO_NEXT_LEVEL = 2;
    public static final int TITLE = 3;
    public static final int SHOWING_LEVEL_WIN = 4;
    public static final int END_CREDITS = 5;
    public static final int END_STATS = 6;

    public static final int LOSE = -1;
    public static final int ALIVE = 0;
    public static final int WIN = 1;

    public static final int SHIELDS = 1;
    public static final int NO_SHIELDS = 0;

    public static final int ENEMY_POINT_VALUE = 10;

    private ArrayList<LevelData> levelsList = new ArrayList<>();

    public GameController() {
        generateLevelData();
    }

    public int getGameStateForCurrentKills() {
        if (currentKills >= levelsList.get(currentLevel).getKillQuota()){
            return WIN;
        }
        return ALIVE;
    }

    private void generateLevelData() {

        LevelData level0 = new LevelData(-10, 20, 3.5f, 1, NO_SHIELDS, 2.f, 0.f, 0.8f, false, 9);
        level0.setBitmapPalette(new int[] {
                R.mipmap.yellow1,
                R.mipmap.yellow2,
                R.mipmap.yellow3,
                R.mipmap.yellow4,
                R.mipmap.yellow5
        });
        LevelData level1 = new LevelData(10, 20, 3.5f, 2, NO_SHIELDS, 2.f, 0.f, 0.8f, false, 9);
        level1.setBitmapPalette(new int[] {
                R.mipmap.green1,
                R.mipmap.green2,
                R.mipmap.green3,
                R.mipmap.green4,
                R.mipmap.green5
        });
        LevelData level2 = new LevelData(30, 30, 3.5f, 4, SHIELDS, 2.25f, 0.3f, 0.7f, false, 9);
        level2.setBitmapPalette(new int[] {
                R.mipmap.orange3,
                R.mipmap.orange4,
                R.mipmap.orange5,
        });
        LevelData level3 = new LevelData(50, 40, 3.5f, 8, SHIELDS, 2.25f, 0.4f, 0.75f, false, 9);
        level3.setBitmapPalette(new int[] {
                R.mipmap.blue1,
                R.mipmap.blue2,
                R.mipmap.blue3,
        });
        LevelData level4 = new LevelData(80, 50, 3.5f, 16, SHIELDS, 2.25f, 0.7f, 0.8f, true, 12);
        level4.setBitmapPalette(new int[] {
                R.mipmap.pink1,
                R.mipmap.pink2,
                R.mipmap.pink3,
                R.mipmap.pink4,
        });

        LevelData level5 = new LevelData(120, 70, 3.5f, 32, SHIELDS, 2.f, 0.8f, 1.f, true, 12);
        level5.setBitmapPalette(new int[] {
                R.mipmap.purple1,
                R.mipmap.purple2,
                R.mipmap.purple3,
                R.mipmap.purple4,
        });
        LevelData level6 = new LevelData(120, 90, 3.5f, 64, SHIELDS, 2.f, 0.8f, 1.f, true, 15);
        level6.setBitmapPalette(new int[] {
                R.mipmap.orange4,
                R.mipmap.yellow1,
                R.mipmap.blue2,
                R.mipmap.pink3,
        });
        LevelData level7 = new LevelData(120, 120, 3.5f, 96, SHIELDS, 1.75f, 0.8f, 1.f, true, 15);
        level7.setBitmapPalette(new int[] {
                R.mipmap.green2,
                R.mipmap.blue1,
                R.mipmap.yellow3,
                R.mipmap.purple5,
        });
        LevelData level8 = new LevelData(120, 150, 3.5f, 192, SHIELDS, 1.5f, 0.8f, 1.f, true, 15);
        level8.setBitmapPalette(new int[] {
                R.mipmap.pink1,
                R.mipmap.green3,
                R.mipmap.orange5,
                R.mipmap.blue2,
        });

        levelsList.add(level0);
        levelsList.add(level1);
        levelsList.add(level2);
        levelsList.add(level3);
        levelsList.add(level4);
        levelsList.add(level5);
        levelsList.add(level6);
        levelsList.add(level7);
        levelsList.add(level8);
    }

    public void restartGame() {

        if (currentScore > highScore){
            highScore = currentScore;
        }
        resetCurrentScore();
        currentLevel = L_0;
        setGameState(TITLE);
        backStabber = true;
        cumulativeDeaths = 0;
        cumulativeEscapedEnemies = 0;
        cumulativeKills = 0;
        escapedEnemies = 0;
        currentKills = 0;
        remainingLives = START_LIVES;
        maxCombo = 0;
        notifyOnGameRestarted();
    }

    public LevelData getDataForLevel(int level) {
        return levelsList.get(level);
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void resetCurrentScore() {
        currentScore = 0;
    }
    public void incrementKillCount() {
        currentKills++;
        cumulativeKills++;
        incrementCurrentScore(ENEMY_POINT_VALUE);
        notifyOnAchievementIncrement(SharedConstants.ACH_ONE_HUNDRED, 1);
        notifyOnAchievementIncrement(SharedConstants.ACH_ONE_THOUSAND, 1);
        notifyOnAchievementIncrement(SharedConstants.ACH_TEN_THOUSAND, 1);
    }


    public void incrementCurrentScore() {
        currentScore++;
    }

    public void incrementCurrentScore(int increment) {
        currentScore+=increment;
    }

    public long getEnemyTravelTime() {
        return MathUtil.framesForSec(levelsList.get(getCurrentLevel()).getEnemyTravelTime());
    }

    public void decrementCurrentScore() {
        currentScore -= levelsList.get(getCurrentLevel()).getScoreDecrement();
    }

    public void decrementRemainingLives() {
        remainingLives--;
    }

    public void setRemainingLives(int remaining) {
        remainingLives = remaining;
    }

    public boolean isPlayerAlive() {
        return playerAlive;
    }

    public void setPlayerAlive(boolean playerAlive) {
        this.playerAlive = playerAlive;
    }

    public void proceedToNextLevel() {
        if (currentLevel == levelsList.size() -1 &&
                cumulativeDeaths == 0 && !AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_PRETTY_AWESOME_MAN)){
            AchievementData awesomeData = new AchievementData(SharedConstants.ACH_PRETTY_AWESOME_MAN);
            awesomeData.updateStatus(SharedConstants.ACHIEVED);
            notifyOnAchievementMet(awesomeData);
        }

        if (currentLevel == levelsList.size() -1) {
            cumulativeDeaths = 0;
            cumulativeKills = 0;
            maxCombo = 0;
        }

        currentLevel = (currentLevel + 1) % levelsList.size();

        if (currentLevel == levelsList.size() -1 &&
                !AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_FIN)) {
            AchievementData finData = new AchievementData(SharedConstants.ACH_FIN);
            finData.updateStatus(SharedConstants.ACHIEVED);
            notifyOnAchievementMet(finData);
        }
        if (backStabber && currentKills > 0 && !AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_BACK_STABBER)) {
            AchievementData backStabData = new AchievementData(SharedConstants.ACH_BACK_STABBER);
            backStabData.updateStatus(SharedConstants.ACHIEVED);
            notifyOnAchievementMet(backStabData);
        }
        currentKills = 0;

    }

    public int getGameState() {
        return gameState;
    }

    public int getShieldMode() {
        return levelsList.get(currentLevel).getShieldMode();
    }

    public long getEnemyInterval() {
        return MathUtil.framesForSec(levelsList.get(currentLevel).getEnemyInterval());
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
        if (gameState == TRANSITIONING_TO_NEXT_LEVEL) {

            if (escapedEnemies >= 50 && !AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_PACIFIST)) {
                AchievementData pacifistData = new AchievementData(SharedConstants.ACH_PACIFIST);
                pacifistData.updateStatus(SharedConstants.ACHIEVED);
                notifyOnAchievementMet(pacifistData);
            }
            escapedEnemies = 0;
            backStabber = true;

            notifyOnLevelTransition();
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getRemainingLives() {
        return remainingLives;
    }

    public float getExistenceProb() {
        return levelsList.get(currentLevel).getExistenceProb();
    }

    public float getShieldProb() {
        return levelsList.get(currentLevel).getShieldProb();
    }

    public float getRotAngleRand() {
        return levelsList.get(currentLevel).getRotAngleRandom();
    }

    public void incrementComboCount(){
        comboCount++;
        if (comboCount >= 8) {
            if (!AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_OCHO)) {
                AchievementData ochoData = new AchievementData(SharedConstants.ACH_OCHO);
                ochoData.updateStatus(SharedConstants.ACHIEVED);
                notifyOnAchievementMet(ochoData);

            }
            if (lastOchoTime == 0) {
                lastOchoTime = new Date().getTime();
            } else {
                long now = new Date().getTime();
                if (TimeUnit.SECONDS.convert(now - lastOchoTime, TimeUnit.MILLISECONDS) < 10 &&
                        !AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_RE_OCHO)) {
                    AchievementData reOchoData = new AchievementData(SharedConstants.ACH_RE_OCHO);
                    reOchoData.updateStatus(SharedConstants.ACHIEVED);
                    notifyOnAchievementMet(reOchoData);
                }
                lastOchoTime = now;
            }
        } else if (comboCount >= 4) {
            if (!AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_CUATRO)) {
                AchievementData cuatroData = new AchievementData(SharedConstants.ACH_CUATRO);
                cuatroData.updateStatus(SharedConstants.ACHIEVED);
                notifyOnAchievementMet(cuatroData);
            }
        }
    }

    public void resetComboCount(){

        if (comboCount > 1) {
            if (comboCount > maxCombo) {
                maxCombo = comboCount;
            }
            incrementCurrentScore(comboCount * comboCount * comboCount);
        }

        comboCount = 0;
    }

    public int getComboCount() {
        return comboCount;
    }

    public int getMaxConcurrentEnemies() {
        return levelsList.get(currentLevel).getMaxConcurrentEnemies();
    }

    public int getScoreDecrement() {
        return levelsList.get(currentLevel).getScoreDecrement();
    }

    public int[] getBitmapPalette() {
        return levelsList.get(currentLevel).getBitmapPalette();
    }

    public float getCompletionPercent() {
        return ((float)currentKills)/((float)levelsList.get(currentLevel).getKillQuota());
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void recordDeath() {
        cumulativeDeaths++;
        if (!AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_BAD_PILOT)) {
            notifyOnAchievementIncrement(SharedConstants.ACH_BAD_PILOT, 1);
        }

        if (remainingLives == 0 && currentKills == 0 && currentScore <= 0 &&
                !AchievementsUtil.achievementAlreadyAchieved(SharedConstants.ACH_SITTING_DUCK)) {
            AchievementData sittingData = new AchievementData(SharedConstants.ACH_SITTING_DUCK);
            sittingData.updateStatus(SharedConstants.ACHIEVED);
            notifyOnAchievementMet(sittingData);
        }
    }

    public void notifyOnAchievementMet(AchievementData data){

        for (ControllerListener listener : listenerMap) {
            ((GameControllerListener)listener).onAchievementMet(data);
        }
    }

    public void notifyOnAchievementIncrement(String acheivementKey, int increment){
        if (AchievementsUtil.achievementAlreadyAchieved(acheivementKey)) {
            return;
        }
        for (ControllerListener listener : listenerMap) {
            ((GameControllerListener)listener).onAchievementIncrement(acheivementKey, increment);
        }
    }

    public void notifyOnGameRestarted(){
        for (ControllerListener listener : listenerMap) {
            ((GameControllerListener)listener).onGameRestarted();
        }
    }

    public void notifyOnLevelTransition(){
        for (ControllerListener listener : listenerMap) {
            ((GameControllerListener)listener).onLevelTransition();
        }
    }

    public boolean isBackStabber() {
        return backStabber;
    }

    public void setBackStabber(boolean allBackStabs) {
        this.backStabber = allBackStabs;
    }

    public int getCumulativeEscapedEnemies() {
        return cumulativeEscapedEnemies;
    }

    public int getEscapedEnemies() {
        return escapedEnemies;
    }

    public void incrementEscapedEnemies() {
        escapedEnemies++;
        cumulativeEscapedEnemies++;
    }

    public int getCumulativeDeaths() {
        return cumulativeDeaths;
    }

    public int getCumulativeKills() {
        return cumulativeKills;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public static abstract class GameControllerListener extends ControllerListener{
        public void onAchievementMet(AchievementData achievementData){}
        public void onAchievementIncrement(String achievementKey, int increment){}
        public void onGameRestarted(){}
        public void onLevelTransition(){}
    }

}
