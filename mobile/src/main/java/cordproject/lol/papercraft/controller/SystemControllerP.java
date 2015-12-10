package cordproject.lol.papercraft.controller;

/**
 * Created by matthewlim on 11/16/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class SystemControllerP extends ControllerP {

    public boolean gameServicesConnected = false;
    private int currentHighScore;
    public void notifyOnGameServicesConnectSuccess() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onGameServicesConnectSuccess();
        }
    }

    public void notifyOnGameServicesConnectFailure() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onGameServicesConnectFailure();
        }
    }

    public void notifyOnGameServicesDisconnected() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onGameServicesDisconnected();
        }
    }

    public void notifyOnLeaderboardsRequested() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onLeaderboardsRequested();
        }
    }

    public void notifyOnAchievementsRequested() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onAchievementsRequested();
        }
    }

    public void notifyOnHighScoreUpdated(int score) {
        currentHighScore = score;
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onHighScoreUpdated(score);
        }
    }

    public int getCurrentHighScore() {
        return currentHighScore;
    }

    public void setCurrentHighScore(int currentHighScore) {
        this.currentHighScore = currentHighScore;
    }

    public boolean isGameServicesConnected() {
        return gameServicesConnected;
    }

    public void setGameServicesConnected(boolean gameServicesConnected) {
        this.gameServicesConnected = gameServicesConnected;
    }

    public void notifyOnGameServicesSignInRequested() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onGameServicesSignInRequested();
        }
    }

    public void notifyOnGameServicesSignInCancelled() {
        for (ControllerListenerP listener : listenerMap) {
            ((SystemControllerListenerP)listener).onGameServicesSignInCancelled();
        }
    }

    public static abstract class SystemControllerListenerP extends ControllerListenerP {
        public void onGameServicesConnectSuccess(){ }
        public void onGameServicesConnectFailure(){ }
        public void onGameServicesDisconnected(){ }
        public void onLeaderboardsRequested(){ }
        public void onAchievementsRequested(){ }
        public void onHighScoreUpdated(int score){ }
        public void onGameServicesSignInCancelled(){ }
        public void onGameServicesSignInRequested(){ }

    }
}



