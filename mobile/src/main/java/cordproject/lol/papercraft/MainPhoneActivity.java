package cordproject.lol.papercraft;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;

import cordproject.lol.papercraft.controller.ControllerP;
import cordproject.lol.papercraft.controller.SystemControllerP;
import cordproject.lol.papercraft.ui.MainPhoneView;
import cordproject.lol.papercraftshared.entity.AchievementData;
import cordproject.lol.papercraftshared.util.SharedConstants;


public class MainPhoneActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener{

    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    private static final int RC_RESOLVE = 5000;
    private static final int RC_LEADERBOARDS = 5001;
    private static final int RC_ACHIEVEMENTS = 5002;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "PaperCraft";


    private MainPhoneView phoneView;
    private SystemControllerP systemController;

    private ArrayList<AchievementData> outgoingAchievementData = new ArrayList<>();

    private SystemControllerP.SystemControllerListenerP systemListener = new SystemControllerP.SystemControllerListenerP() {
        @Override
        public void onLeaderboardsRequested() {
            showLeaderboard();
        }

        @Override
        public void onAchievementsRequested() {
            showAchievements();
        }

        @Override
        public void onGameServicesSignInRequested() {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        systemController = (SystemControllerP) PaperCraftApplicationP.getController(ControllerP.SYSTEM_CONTROLLER);
        phoneView = (MainPhoneView) findViewById(R.id.main_phone_view);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApiIfAvailable(Wearable.API)
                .build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean didSignOut = prefs.getBoolean(SharedConstants.KEY_GPG_SIGNED_OUT, false);
        if (!didSignOut) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            systemController.notifyOnGameServicesDisconnected();
        }
    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }

        saveUserSignedOutStatus(false);
        systemController.setGameServicesConnected(true);
        systemController.notifyOnGameServicesConnectSuccess();
        Wearable.DataApi.addListener(mGoogleApiClient, this);

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    String nodeId = node.getId();
                    Uri.Builder builder = new Uri.Builder();
                    Uri allData = builder.scheme(PutDataRequest.WEAR_URI_SCHEME).authority(nodeId).path(SharedConstants.DATAPATH_ALL_DATA).build();
                    Wearable.DataApi.getDataItems(mGoogleApiClient, allData).setResultCallback(new ResultCallback<DataItemBuffer>() {
                        @Override
                        public void onResult(DataItemBuffer dataItems) {
                            for (DataItem item : dataItems) {
                                handleIncomingData(item);
                            }
                        }
                    });
                }
            }
        });

        Toast.makeText(this, "Sup " +displayName, Toast.LENGTH_SHORT).show();
    }

    public void showLeaderboard() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
                    RC_LEADERBOARDS);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
    }

    public void showAchievements() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    RC_ACHIEVEMENTS);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    public void updateAchievements() {

        Games.Leaderboards.submitScore(mGoogleApiClient,
                getResources().getString(R.string.gpgs_leaderboard_id),
                systemController.getCurrentHighScore());

        for (final AchievementData data : outgoingAchievementData) {
            switch (data.type) {
                case SharedConstants.TYPE_INCREMENTAL:
                    if (data.getStatus() < SharedConstants.SENT_TO_SERVER) {
                        Games.Achievements.setStepsImmediate(mGoogleApiClient, getResources().getString(data.achievementResId), data.getValue())
                                .setResultCallback(new ResultCallback<Achievements.UpdateAchievementResult>() {
                                    @Override
                                    public void onResult(Achievements.UpdateAchievementResult updateAchievementResult) {
                                        switch (updateAchievementResult.getStatus().getStatusCode()) {
                                            case GamesStatusCodes.STATUS_OK:
                                                if (data.getStatus() == SharedConstants.ACHIEVED) {
                                                    data.setStatus(SharedConstants.SENT_TO_SERVER);
                                                    notifyWearAchievementSentToServer(data);
                                                }
                                                break;
                                        }
                                    }
                                });

                    }
                    break;
                case SharedConstants.TYPE_SINGLE:
                    if (data.getStatus() < SharedConstants.SENT_TO_SERVER) {
                        Games.Achievements.unlockImmediate(mGoogleApiClient, getResources().getString(data.achievementResId))
                                .setResultCallback(new ResultCallback<Achievements.UpdateAchievementResult>() {
                                    @Override
                                    public void onResult(Achievements.UpdateAchievementResult updateAchievementResult) {
                                        switch (updateAchievementResult.getStatus().getStatusCode()) {
                                            case GamesStatusCodes.STATUS_OK:
                                                data.setStatus(SharedConstants.SENT_TO_SERVER);
                                                notifyWearAchievementSentToServer(data);
                                                break;
                                        }
                                    }
                                });
                    }
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        phoneView.onActivityPause();
        systemController.removeListener(systemListener);
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(SharedConstants.KEY_HIGH_SCORE, systemController.getCurrentHighScore()).apply();
        super.onPause();
    }

    private void notifyWearAchievementSentToServer(final AchievementData data){
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    String nodeId = node.getId();
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, SharedConstants.MESSAGEPATH_ACHIEVEMENT_SENT, data.prefsKey.getBytes());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        phoneView.onActivityResume();
        systemController.addListener(systemListener);
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, MODE_PRIVATE);
        systemController.setCurrentHighScore(prefs.getInt(SharedConstants.KEY_HIGH_SCORE, 0));
        super.onResume();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
        systemController.setGameServicesConnected(false);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
        systemController.setGameServicesConnected(false);
        systemController.notifyOnGameServicesConnectFailure();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {

            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else if (resultCode != RESULT_CANCELED){
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
                mSignInClicked = true;
                mGoogleApiClient.connect();
            } else {
                systemController.notifyOnGameServicesSignInCancelled();
                saveUserSignedOutStatus(true);
            }
        } if ((requestCode == RC_ACHIEVEMENTS || requestCode == RC_LEADERBOARDS) && resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            mResolvingConnectionFailure = false;
            mGoogleApiClient.disconnect();
            systemController.notifyOnGameServicesSignInCancelled();
            systemController.setGameServicesConnected(false);
            saveUserSignedOutStatus(true);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            //Log.d("achieve", "Data changed");
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                handleIncomingData(item);
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private void saveUserSignedOutStatus(boolean signedOut) {
        SharedPreferences prefs = getSharedPreferences(SharedConstants.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(SharedConstants.KEY_GPG_SIGNED_OUT, signedOut).apply();
    }

    private void handleIncomingData(DataItem item) {
        if (item.getUri().getPath().compareTo(SharedConstants.DATAPATH_ALL_DATA) == 0) {
            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
            int highScore = dataMap.getInt(SharedConstants.MAP_KEY_HIGH_SCORE);
            if (highScore > 0) {
                systemController.notifyOnHighScoreUpdated(highScore);
            }
            ArrayList<DataMap> incomingAchievementData = dataMap.getDataMapArrayList(SharedConstants.MAP_KEY_ACHIEVEMENT_DATA);

            outgoingAchievementData.clear();
            for (DataMap map : incomingAchievementData){
                int combinedValue = map.get(SharedConstants.MAP_KEY_COMBINED_VALUE);
                String achievementKey = map.get(SharedConstants.MAP_KEY_ACHIEVEMENT_KEY);
                AchievementData data = new AchievementData(achievementKey, combinedValue);
                outgoingAchievementData.add(data);
            }
            updateAchievements();
        }
    }
}
