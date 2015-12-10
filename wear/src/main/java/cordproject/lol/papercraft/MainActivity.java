package cordproject.lol.papercraft;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cordproject.lol.papercraft.controller.Controller;
import cordproject.lol.papercraft.controller.GameController;
import cordproject.lol.papercraft.controller.SystemController;
import cordproject.lol.papercraft.ui.GameView;
import cordproject.lol.papercraftshared.entity.AchievementData;
import cordproject.lol.papercraftshared.util.AchievementsUtil;
import cordproject.lol.papercraftshared.util.SharedConstants;



public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private GameView gameView;
    private DismissOverlayView mDismissOverlay;
    private SystemController systemController;
    private GameController gameController;
    private HashMap<String, AchievementData> currentAchievements = new HashMap<>();
    private MediaPlayer bgmPlayer;
    private SoundPool sfxPool;
    private ArrayList<Integer> soundIds = new ArrayList<>();

    private SystemController.SystemControllerListener systemListener = new SystemController.SystemControllerListener() {
        @Override
        public void onQuitRequested() {
               mDismissOverlay.show();
        }

        @Override
        public void onMusicStopRequested() {
            stopPlayingMusic();
        }

        @Override
        public void onMusicStartRequested() {
            startPlayingMusic();
        }

        @Override
        public void onExplosionSoundRequested() {
            playExplosionSound();
        }
    };
    private GoogleApiClient mGoogleApiClient;

    private GameController.GameControllerListener gameListener = new GameController.GameControllerListener() {
        @Override
        public void onAchievementMet(AchievementData achievementData) {
            if (!currentAchievements.containsKey(achievementData.prefsKey)) {
                currentAchievements.put(achievementData.prefsKey, achievementData);
            }
            AchievementsUtil.markAchievementAchieved(achievementData.prefsKey);
        }

        @Override
        public void onAchievementIncrement(String achievementKey, int increment) {
            if (currentAchievements.containsKey(achievementKey)) {
                AchievementData data = currentAchievements.get(achievementKey);
                data.incrementBy(increment);
                if (AchievementsUtil.reachedMaxValue(data.prefsKey, data.getValue())) {
                    data.setStatus(SharedConstants.ACHIEVED);
                    gameController.notifyOnAchievementMet(data);
                }
            } else {
                AchievementData data = new AchievementData(achievementKey);
                data.incrementBy(increment);
                currentAchievements.put(achievementKey, data);
                if (AchievementsUtil.reachedMaxValue(data.prefsKey, data.getValue())) {
                    data.setStatus(SharedConstants.ACHIEVED);
                    gameController.notifyOnAchievementMet(data);
                }
            }
        }

        @Override
        public void onGameRestarted() {
            if (mGoogleApiClient.isConnected()) {
                sendDataToPhone();
            }
        }

        @Override
        public void onLevelTransition() {
            if (mGoogleApiClient.isConnected()) {
                sendDataToPhone();
            }
        }
    };


    @Override
    protected void onPause() {
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        editor.putBoolean(SharedConstants.KEY_SOUND_PREFERENCE, systemController.isSoundPreferenceOn());
        editor.putInt(SharedConstants.KEY_HIGH_SCORE, gameController.getHighScore());
        editor.apply();
        stopPlayingMusic();

        /*Log.d("achieve", "PAUSE -> In progress achievements:");
        for (AchievementData data : currentAchievements.values()) {
            Log.d("achieve", data.toString(getResources()));
        }*/
        AchievementsUtil.recordAchievementProgress(prefs, currentAchievements);
        if (gameView != null){
            gameView.onActivityPause();
        }
        systemController.removeListener(systemListener);
        gameController.removeListener(gameListener);

        super.onPause();

    }

    private void stopPlayingMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.reset();
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (sfxPool != null){
            sfxPool.release();
            sfxPool = null;
        }
        soundIds.clear();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        systemController = (SystemController) PaperCraftApplication.getController(Controller.SYSTEM_CONTROLLER);
        gameController = (GameController) PaperCraftApplication.getController(Controller.GAME_CONTROLLER);
        gameController.restartGame();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        //mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                gameView = (GameView) stub.getChildAt(0);
                gameView.onActivityResume();
            }
        });
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.long_press_intro);
        mDismissOverlay.showIntroIfNecessary();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();


    }

    private Uri getUriForRawFile(int rawResource){
        return Uri.parse("android.resource://" + getPackageName() + "/" + rawResource);
    }

    private void sendDataToPhone() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(SharedConstants.DATAPATH_ALL_DATA);
        if (gameController.getHighScore() > 0 || !currentAchievements.isEmpty()) {
            putDataMapReq.getDataMap().putInt(SharedConstants.KEY_HIGH_SCORE, gameController.getHighScore());

            ArrayList<DataMap> newAchievements = new ArrayList<>();
            for (AchievementData data : this.currentAchievements.values()) {
                DataMap dataMap = new DataMap();
                boolean achieved = data.getStatus() >= SharedConstants.ACHIEVED;
                boolean notSentToBackend = data.getStatus() < SharedConstants.SENT_TO_SERVER;

                if ((notSentToBackend && data.type == SharedConstants.TYPE_INCREMENTAL) ||
                        (notSentToBackend && achieved && data.type == SharedConstants.TYPE_SINGLE)) {
                    dataMap.putInt(SharedConstants.MAP_KEY_COMBINED_VALUE, data.getCombinedValue());
                    dataMap.putString(SharedConstants.MAP_KEY_ACHIEVEMENT_ID, getResources().getString(data.achievementResId));
                    dataMap.putString(SharedConstants.MAP_KEY_ACHIEVEMENT_KEY, data.prefsKey);
                    newAchievements.add(dataMap);
                }
            }
            putDataMapReq.setUrgent();
            putDataMapReq.getDataMap().putDataMapArrayList(SharedConstants.MAP_KEY_ACHIEVEMENT_DATA, newAchievements);
            putDataMapReq.getDataMap().putLong(SharedConstants.MAP_KEY_TIMESTAMP, new Date().getTime());
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    if (dataItemResult.getStatus().isSuccess()) {
                        Log.d("achieve", "achievements/high score data sent!");
                    }
                }
            });
            /*Uri.Builder builder = new Uri.Builder();
            Uri allData = builder.scheme(PutDataRequest.WEAR_URI_SCHEME).path(SharedConstants.DATAPATH_ALL_DATA).build();
            Wearable.DataApi.deleteDataItems(mGoogleApiClient, allData);*/
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, MODE_PRIVATE);
        int lastHighScore = prefs.getInt(SharedConstants.KEY_HIGH_SCORE, -1);
        if (lastHighScore >= 0) {
            gameController.setHighScore(lastHighScore);
        }
        if (gameView != null) {
            gameView.onActivityResume();
        }
        AchievementsUtil.unpackInProgressAchievements(prefs, currentAchievements);
        AchievementsUtil.markAchievementsAchieved(prefs);
        Log.d("achieve", "START -> In progress achievements:");
        for (AchievementData data : currentAchievements.values()) {
            Log.d("achieve", data.toString(getResources()));
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, MODE_PRIVATE);
        systemController.setSoundPreferenceOn(prefs.getBoolean(SharedConstants.KEY_SOUND_PREFERENCE, true));

        startPlayingMusic();

        int lastHighScore = prefs.getInt(SharedConstants.KEY_HIGH_SCORE, -1);
        if (lastHighScore >= 0) {
            gameController.setHighScore(lastHighScore);
        }
        if (gameView != null) {
            gameView.onActivityResume();
        }

        AchievementsUtil.markAchievementsAchieved(prefs);
        /*Log.d("achieve", "RESUME -> In progress achievements:");
        for (AchievementData data : currentAchievements.values()) {
            Log.d("achieve", data.toString(getResources()));
        }*/
        systemController.addListener(systemListener);
        gameController.addListener(gameListener);
        super.onResume();
    }

    private void startPlayingMusic() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT) && systemController.isSoundPreferenceOn()) {
            bgmPlayer = MediaPlayer.create(this, getUriForRawFile(R.raw.paper));
            bgmPlayer.setLooping(true);
            bgmPlayer.start();


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                SoundPool.Builder builder = new SoundPool.Builder();
                AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
                attributesBuilder.setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
                builder.setAudioAttributes(attributesBuilder.build()).setMaxStreams(3);

                sfxPool = builder.build();

            } else {
                sfxPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

            }
            soundIds.add(sfxPool.load(this, R.raw.boom1, 1));
            soundIds.add(sfxPool.load(this, R.raw.boom2, 1));
            soundIds.add(sfxPool.load(this, R.raw.boom4, 1));
        }
    }

    private void playExplosionSound() {
        if (sfxPool != null && systemController.isSoundPreferenceOn()) {
            sfxPool.play(soundIds.get((int) (Math.random()*3)), .5f, .5f, 1, 0, 1.f);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        if (gameView != null) {
            gameView.onActivityPause();
        }
        stopPlayingMusic();
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();

        if (gameView != null) {
            gameView.onActivityResume();
        }

        super.onExitAmbient();
        startPlayingMusic();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mClockView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        sendDataToPhone();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String prefsKey = new String(messageEvent.getData());
        if (!TextUtils.isEmpty(prefsKey) && currentAchievements.containsKey(prefsKey)) {
            currentAchievements.get(prefsKey).setStatus(SharedConstants.SENT_TO_SERVER);
        }
        AchievementsUtil.markAchievementSentToServer(prefsKey);
    }
}
