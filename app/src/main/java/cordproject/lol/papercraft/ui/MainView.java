package cordproject.lol.papercraft.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cordproject.lol.papercraft.PaperCraftApplication;
import pro.rudloff.papercraft.R;
import cordproject.lol.papercraft.controller.Controller;
import cordproject.lol.papercraft.controller.GameController;
import cordproject.lol.papercraft.controller.SystemController;
import cordproject.lol.papercraft.entity.BulletData;
import cordproject.lol.papercraft.entity.EnemyData;
import cordproject.lol.papercraft.util.MathUtil;
import cordproject.lol.papercraftshared.entity.AchievementData;
import cordproject.lol.papercraftshared.graphics.PaperGen;

public class MainView extends FrameLayout implements GameView{

    // touch variables
    protected float downX;
    protected float downY;
    protected float lastX, lastY;
    protected float deltaX, deltaY, downDeltaX, downDeltaY;
    protected float touchSlop;

    protected float currentX, currentY;
    protected float radiusAngle = 0;
    protected float startX;


    protected Paint paint;
    protected Paint bulletPaint;


    protected int dpSize;
    protected boolean isManeuvering = false;
    protected float enemyRadius;
    protected boolean shouldFireBackwards = false;
    protected boolean dead = false;


    protected int lastDirection = NEUTRAL;
    protected int maneuverDirection = NEUTRAL;
    protected int maneuverDistance;

    protected long downTime = 0;
    protected float rotAngle = 0;

    public static final int FORWARD = 1;
    public static final int NEUTRAL = 0;
    public static final int BACKWARD = -1;

    protected Bitmap shieldBitmap;

    // dimensions
    protected float shipLength;
    protected float shipHeight;
    protected int bulletLength;

    protected RectF shipRect = new RectF();
    protected RectF muzzleRect = new RectF();

    protected int restartCount;

    protected boolean shipOnScreen = false;

    protected float shipBlurRadius;

    protected int bulletIntervalTicker = 0;
    protected int enemyDeployIntervalTicker = 0;

    protected RectF bulletRect;
    protected Bitmap bulletBitmap;

    protected Bitmap shipBitmap;
    protected Bitmap shipShadow;

    protected Bitmap[] enemyBitmaps = new Bitmap[3];
    protected Bitmap[] alertEnemyBitmaps = new Bitmap[3];

    protected ArrayList<BulletData> bullets= new ArrayList<>();
    protected ArrayList<BulletData> bulletsToDelete= new ArrayList<>();

    protected SystemController systemController;
    protected GameController gameController;
    protected Paint explosionPaint;

    protected Timer frameTimer;
    protected TimerTask frameTask;

    int screenWidth, screenHeight;

    // scrolling background
    protected Bitmap scrollingSectionBitmap;
    protected Matrix scrollingRotMatrix1;
    protected Matrix scrollingTransMatrix1;
    protected float scrollingX1;
    protected float scrollingX1Start;
    protected Bitmap scrollingSectionBitmap2;
    protected Matrix scrollingRotMatrix2;
    protected Matrix scrollingTransMatrix2;
    protected float scrollingX2;

    protected float scrollSpeed1;
    protected float scrollSpeed2;

    protected float scrollingX2Start;

    // player state
    protected int deadTicker = 0;
    protected boolean shouldNotFire = false;
    protected boolean shouldAddMoreEnemies = false;

    // ship matrices
    protected Matrix transMatrix;
    protected Matrix rotMatrix;
    protected Matrix shadowMatrix;
    protected Matrix shadowRotMatrix;
    static final Object enemyLock = new Object();
    protected float heartbeat;

    // booster
    protected Paint boosterPaint;
    protected Paint boosterPaintSmall;
    protected Matrix boosterTransMat;
    protected Matrix boosterRotMat;
    protected Matrix boosterTransMatSmall;
    protected Matrix boosterRotMatSmall;

    protected Path boosterPath;
    protected Path boosterPathSmall;

    protected float boosterLerpRateSmallX = LERP_RATE_DEFAULT;
    protected float boosterLerpRateSmallY = LERP_RATE_DEFAULT;

    protected float boosterLerpRateX = LERP_RATE_DEFAULT;
    protected float boosterLerpRateY = LERP_RATE_DEFAULT;

    protected float boosterLerpTargetSmallX = 0.25f;
    protected float boosterLerpTargetSmallY = 0.25f;

    protected float boosterLerpTargetX = 0.25f;
    protected float boosterLerpTargetY = 0.25f;

    protected float boosterPointX;
    protected float boosterPointY;

    protected float boosterPointSmallX;
    protected float boosterPointSmallY;

    protected float boosterRangeY = 0;
    protected float boosterRangeX = 0;
    protected float boosterRangeXManeuver = 0;
    protected float boosterLength = 0;
    protected float boosterHeight = 0;

    static final int UP = 1;
    static final int DOWN = 0;
    protected int heartbeatDirection = UP;

    protected int enemyCount;

    protected float maneuverX;
    protected float maneuverY;

    private Paint alertPaint;
    private float scoreScale;
    private final int alertColor = 0xffdd0022;
    private TextPaint highScorePaint;
    private TextPaint scorePaint;
    private TextPaint comboPaint;
    private int textSizePixels;

    private TextPaint titlePaint;
    private Paint iconPaint;
    private Paint livesPaint;
    protected Paint levelCompletionPaint;
    protected RectF levelCompletionRect = new RectF();
    protected float levelCompletionDiameter;
    protected float drawnCompletionPercent;
    protected int invulnerabilityTicker = 120;

    private int levelTitleTransitionTicker = 0;
    protected float levelTitleTransitionPct = 0;
    protected float gameTitleTransitionPct = 2.f;

    protected ArrayList<EnemyData> lane1;
    protected ArrayList<EnemyData> lane2;
    protected ArrayList<EnemyData> lane3;

    protected ArrayList<EnemyData> enemiesToDeploy;

    protected ArrayList<EnemyData> enemiesToDelete;
    protected ArrayList<ExplosionData> explosions;
    protected ArrayList<ExplosionData> explosionsToDelete;
    protected DecimalFormat scoreFormatter = new DecimalFormat("#,###,###");

    private float scoreSkew;
    protected Bitmap titleIcon;
    private Bitmap lifeBitmap;
    private float livesMarginLeft;

    private float achievementTicker;
    private String currentAchievementString;
    protected ImageView soundToggle;

    protected TextView creditsText;
    protected TextView statsText;

    public static final int BOOSTER_COLOR = 0xffff9933;
    public static final int COMBO_HUGE_COLOR = 0xff3399ff;
    public static final float LERP_RATE_DEFAULT = 0.45f;
    public static final float LERP_RATE_MAX = 0.96f;

    protected static final boolean DEBUG_TAPS = false;

    private GameController.GameControllerListener gameListener = new GameController.GameControllerListener() {
        @Override
        public void onAchievementMet(AchievementData achievementData) {
            showAchievement(achievementData);
        }

        @Override
        public void onGameRestarted() {
            post(new Runnable() {
                @Override
                public void run() {
                    if (soundToggle != null) {
                        soundToggle.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    };
    private int textShadowRadius;
    private int textShadowOffset;

    private void showAchievement(AchievementData achievementData) {
        synchronized (enemyLock) {
            scoreScale = 3.f;
            achievementTicker = 60.f;
            currentAchievementString = getResources().getString(achievementData.descriptionResId);
        }
    }

    protected Runnable quitRunnable = new Runnable() {
        @Override
        public void run() {
            systemController.notifyOnQuitRequested();
        }
    };

    protected Runnable comboCancelRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (enemyLock) {
                if (gameController.getComboCount() > 1) {
                    setScoreScaleForEvent(3.f);
                }
                gameController.resetComboCount();
                comboPaint.setColor(Color.WHITE);
            }
        }
    };

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rotMatrix = new Matrix();
        transMatrix = new Matrix();
        shadowMatrix = new Matrix();
        shadowRotMatrix = new Matrix();

        boosterPath = new Path();
        boosterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        boosterPathSmall = new Path();
        boosterPaintSmall = new Paint(Paint.ANTI_ALIAS_FLAG);


        boosterRangeY = getResources().getDimensionPixelSize(R.dimen.booster_vertical_range);
        boosterRangeX = getResources().getDimensionPixelSize(R.dimen.booster_horizontal_range);
        boosterRangeXManeuver = getResources().getDimensionPixelSize(R.dimen.booster_horizontal_range_maneuver);
        boosterLength = getResources().getDimensionPixelSize(R.dimen.booster_length);
        boosterHeight = getResources().getDimensionPixelSize(R.dimen.booster_height);
        boosterPointX = boosterLength;

        boosterPaint.setColor(BOOSTER_COLOR);
        boosterPaintSmall.setColor(0xffff3333);

        boosterTransMat = new Matrix();
        boosterRotMat = new Matrix();
        boosterTransMatSmall = new Matrix();
        boosterRotMatSmall = new Matrix();

        isManeuvering = false;

        systemController = (SystemController)
                PaperCraftApplication.getController(Controller.SYSTEM_CONTROLLER);
        gameController = (GameController)
                PaperCraftApplication.getController(Controller.GAME_CONTROLLER);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        shipLength = getResources().getDimensionPixelSize(R.dimen.ship_length);
        shipHeight = getResources().getDimensionPixelSize(R.dimen.ship_height);
        enemyRadius = getResources().getDimensionPixelSize(R.dimen.enemy_radius);
        bulletLength = getResources().getDimensionPixelSize(R.dimen.bullet_length);

        shipRect.set(0, 0, shipLength*3/4, shipHeight);
        shipBlurRadius = (shipHeight*.125f);
        explosionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        explosionPaint.setColor(0x5d5d5d);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        int shadowColor = getResources().getColor(R.color.shadow_color);
        scrollingSectionBitmap = PaperGen.createScrollingSectionBitmap(getResources().getColor(R.color.scrolling_section_color_1),
                shadowColor, (int) (screenWidth * 0.8f), screenHeight, screenWidth);
        scrollingSectionBitmap2 = PaperGen.createScrollingSectionBitmap(getResources().getColor(R.color.scrolling_section_color_2),
                shadowColor, (int) (screenWidth * 0.6f), screenHeight, screenWidth);
        scrollingRotMatrix1 = new Matrix();
        scrollingTransMatrix1 = new Matrix();

        scrollingX1Start = screenWidth*1.25f;
        scrollSpeed1 = 0.5f;

        scrollingX1 = scrollingX1Start/2;
        scrollingTransMatrix1.setTranslate(scrollingX1, (scrollingSectionBitmap.getHeight() - screenHeight) / 2);
        scrollingRotMatrix1.setRotate(15, scrollingSectionBitmap.getWidth() / 2,
                scrollingSectionBitmap.getHeight() / 2);

        scrollingRotMatrix2 = new Matrix();
        scrollingTransMatrix2 = new Matrix();
        scrollingX2Start = screenWidth*1.25f;

        scrollingX2 = scrollingX2Start/2;
        scrollSpeed2 = 0.8f;
        scrollingTransMatrix2.setTranslate(scrollingX2, (scrollingSectionBitmap2.getHeight() - screenHeight) / 2);
        scrollingRotMatrix2.setRotate(-17, scrollingSectionBitmap2.getWidth() / 2,
                scrollingSectionBitmap2.getHeight() / 2);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xff3399ff);

        alertPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bulletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        makeShip();

        if (lifeBitmap == null) {
            lifeBitmap = makeLifeBitmap();
        }

        createEnemyBitmaps();

        alertEnemyBitmaps[EnemyData.CIRCLE] = PaperGen.makeEnemyCAlert(enemyRadius, alertColor);
        alertEnemyBitmaps[EnemyData.DIAMOND] = PaperGen.makeEnemyDAlert(enemyRadius, alertColor);
        alertEnemyBitmaps[EnemyData.PENTAGON] = PaperGen.makeEnemyPAlert(enemyRadius, alertColor);
        shieldBitmap = PaperGen.makeShieldBitmap(enemyRadius, getResources().getDimensionPixelSize(R.dimen.dp_size), shipBlurRadius);
        if (bulletBitmap == null) {
            bulletRect = new RectF();
            bulletBitmap = PaperGen.makeBulletBitmap(getResources().getDimensionPixelSize(R.dimen.dp_size), bulletLength, bulletRect, shipBlurRadius);
        }

        enemiesToDeploy = new ArrayList<>();
        enemiesToDelete = new ArrayList<>();

        explosions = new ArrayList<>();
        explosionsToDelete = new ArrayList<>();
        shouldAddMoreEnemies = true;
        enemyCount = 2;

        textSizePixels = getResources().getDimensionPixelSize(R.dimen.text_size);

        levelCompletionDiameter = getResources().getDimension(R.dimen.completion_circle_diameter);
        dpSize = getResources().getDimensionPixelSize(R.dimen.dp_size);
        titleIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.landingscreen);

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {

            soundToggle = new ImageView(context);
            soundToggle.setAlpha(0.5f);
            if (systemController.isSoundPreferenceOn()) {
                soundToggle.setImageResource(R.mipmap.ic_volume_up_white_18dp);
            } else {
                soundToggle.setImageResource(R.mipmap.ic_volume_off_white_18dp);
            }
            soundToggle.setLayoutParams(new LayoutParams(dpSize*34, dpSize*34));
            soundToggle.setScaleType(ImageView.ScaleType.CENTER);
            addView(soundToggle);
            soundToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (systemController.isSoundPreferenceOn()) {
                        soundToggle.setImageResource(R.mipmap.ic_volume_off_white_18dp);
                        systemController.setSoundPreferenceOn(false);
                        systemController.notifyOnMusicStopRequested();
                    } else {
                        soundToggle.setImageResource(R.mipmap.ic_volume_up_white_18dp);
                        systemController.setSoundPreferenceOn(true);
                        systemController.notifyOnMusicStartRequested();
                    }
                }
            });
        }

        creditsText = new TextView(context);
        creditsText.setGravity(Gravity.CENTER);
        statsText = new TextView(context);
        statsText.setGravity(Gravity.CENTER);

        creditsText.setText(R.string.credits);
        statsText.setText("");
        creditsText.setVisibility(View.INVISIBLE);
        textShadowRadius = getResources().getDimensionPixelSize(R.dimen.text_shadow_radius);
        textShadowOffset = getResources().getDimensionPixelSize(R.dimen.text_shadow_offset);

        creditsText.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset, getResources().getColor(R.color.shadow_color));
        statsText.setVisibility(View.INVISIBLE);
        statsText.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset, getResources().getColor(R.color.shadow_color));

        addView(creditsText);
        addView(statsText);
        initPaints();
    }

    private void initPaints() {
        Typeface robotoLight = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Light.ttf");

        highScorePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        highScorePaint.setStyle(Paint.Style.STROKE);
        highScorePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.high_score_size));
        highScorePaint.setTextAlign(Paint.Align.CENTER);

        highScorePaint.setTypeface(robotoLight);
        highScorePaint.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset, getResources().getColor(R.color.shadow_color));
        highScorePaint.setColor(getResources().getColor(R.color.lightest_grey));

        comboPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        comboPaint.setStyle(Paint.Style.STROKE);
        comboPaint.setTextSize(textSizePixels);
        comboPaint.setTextAlign(Paint.Align.CENTER);
        comboPaint.setTypeface(Typeface.DEFAULT);
        comboPaint.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset, getResources().getColor(R.color.shadow_color));
        comboPaint.setColor(0xffffffff);

        scorePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        scorePaint.setStyle(Paint.Style.STROKE);
        scorePaint.setTextSize(textSizePixels);
        scorePaint.setTextAlign(TextPaint.Align.CENTER);
        scorePaint.setTypeface(Typeface.DEFAULT);
        scorePaint.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset, getResources().getColor(R.color.shadow_color));
        scorePaint.setColor(0xffffffff);


        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setStyle(Paint.Style.STROKE);
        titlePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.title_size));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(robotoLight);
        titlePaint.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset, getResources().getColor(R.color.shadow_color));
        titlePaint.setColor(0xffffffff);

        levelCompletionPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        levelCompletionPaint.setStyle(Paint.Style.STROKE);
        levelCompletionPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.dp_size));
        levelCompletionPaint.setColor(getResources().getColor(R.color.lightest_grey));

        iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        livesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    public void clearEnemies() {
        enemiesToDelete.clear();
        enemiesToDeploy.clear();
        lane1.clear();
        lane2.clear();
        lane3.clear();
    }

    public void clearBullets() {
        bullets.clear();
        bulletsToDelete.clear();
    }

    protected void destroyEnemy(BulletData bullet, EnemyData enemy) {

        enemy.shouldDelete = true;
        if (bullet != null) {
            if (bullet.destination > 0) {
                gameController.setBackStabber(false);
            }
            gameController.incrementKillCount();
            bullet.shouldDelete = true;
            bulletsToDelete.add(bullet);
            setScoreScaleForEvent(3.f);
            if (gameController.getGameStateForCurrentKills() == GameController.WIN) {
                gameController.setGameState(GameController.SHOWING_LEVEL_WIN);
            }
            invulnerabilityTicker = 0;
            removeCallbacks(comboCancelRunnable);
            gameController.incrementComboCount();
            postDelayed(comboCancelRunnable, 750);
        }
        addExplosion(enemy.x, enemy.y, 0x5d5d5d);
    }

    protected void updateExplosions() {
        for (ExplosionData data : explosions) {
            if (data.shouldDelete) {
                explosionsToDelete.add(data);
            }
        }
        explosions.removeAll(explosionsToDelete);
        explosionsToDelete.clear();
    }

    protected void updatePlayer() {
        if (!dead && invulnerabilityTicker > 0) {
            invulnerabilityTicker--;
        }
    }

    protected void explodeAllEnemies() {

        if (enemiesToDeploy != null && !enemiesToDeploy.isEmpty()) {
            for (EnemyData enemy : enemiesToDeploy) {
                if (enemy != null) {
                    destroyEnemy(null, enemy);
                    enemiesToDelete.add(enemy);
                }
            }
            if (!enemiesToDelete.isEmpty()) {
                enemiesToDeploy.removeAll(enemiesToDelete);
                enemiesToDelete.clear();
            }
        }
    }

    protected void updateShipWinTransition() {
        if (!isManeuvering) {
            currentY = MathUtil.lerp(currentY, getHeight() / 2, .1f);
            radiusAngle = MathUtil.lerp(radiusAngle, 0, .1f);

            if (Math.abs(currentY - getHeight()/2) <= 1.f) {
                currentY = getHeight()/2;
                radiusAngle = 0.f;
                currentX = MathUtil.lerp(currentX, getWidth()*1.2f, currentX/(getWidth()*1.2f)*0.1f);
                boosterLerpRateX = LERP_RATE_MAX;
            }
            if (Math.abs(currentX - getWidth()*1.2f) <= 1.f) {
                clearEnemies();
                if (gameController.getCurrentLevel() == GameController.L_8){
                    post(new Runnable() {
                        @Override
                        public void run() {
                            startCreditsTransition();
                        }
                    });

                    gameController.setGameState(GameController.END_CREDITS);

                } else {

                    gameController.proceedToNextLevel();
                    gameController.setGameState(GameController.TRANSITIONING_TO_NEXT_LEVEL);
                }


                placeShipOffscreen();
                createEnemyBitmaps();
                boosterLerpRateX = LERP_RATE_DEFAULT;
            }
        }
    }

    protected void createEnemyBitmaps() {
        int startIndex = (int) (Math.random() * gameController.getBitmapPalette().length);
        int length = gameController.getBitmapPalette().length;
        Bitmap circleBitmap = BitmapFactory.decodeResource(getResources(), gameController.getBitmapPalette()[startIndex]);
        enemyBitmaps[EnemyData.CIRCLE] = PaperGen.makeEnemyC(circleBitmap, enemyRadius, shipBlurRadius);
        Bitmap diamondBitmap = BitmapFactory.decodeResource(getResources(), gameController.getBitmapPalette()[(startIndex + 1) % length]);
        enemyBitmaps[EnemyData.DIAMOND] = PaperGen.makeEnemyD(diamondBitmap, enemyRadius, shipBlurRadius);
        Bitmap pentagonBitmap = BitmapFactory.decodeResource(getResources(), gameController.getBitmapPalette()[(startIndex + 2) % length]);
        enemyBitmaps[EnemyData.PENTAGON] = PaperGen.makeEnemyP(pentagonBitmap, enemyRadius, shipBlurRadius);
    }

    protected void placeShipOffscreen() {
        shipOnScreen = false;
        currentX = -shipLength;
    }

    protected void updateEnemies() {

        if (enemiesToDeploy != null && !enemiesToDeploy.isEmpty()) {
            for (EnemyData enemy : enemiesToDeploy) {
                if (enemy != null) {
                    if (enemy.reachedOffscreen && !dead) {
                        if (gameController.getCurrentScore() - gameController.getScoreDecrement() < -10) {
                            addExplosion(currentX + maneuverX + shipLength / 2, currentY + maneuverY,
                                    getResources().getColor(R.color.ship_color));
                            gameController.setRemainingLives(0);
                            dead = true;
                            gameController.recordDeath();
                        }
                        gameController.incrementEscapedEnemies();
                        gameController.decrementCurrentScore();
                        setScoreScaleForEvent(2.f);
                        if (achievementTicker == 0) {
                            scoreSkew = -0.5f;
                        }
                    }
                    if (enemy.shouldDelete) {
                        enemiesToDelete.add(enemy);
                        continue;
                    }
                    handleEnemyCollision(enemy);
                    if (invulnerabilityTicker == 0) {
                        enemy.move(enemy.speedX, enemy.speedY);
                    }
                }
            }
            if (!enemiesToDelete.isEmpty()) {
                enemiesToDeploy.removeAll(enemiesToDelete);
                enemiesToDelete.clear();
            }
        }
    }

    protected void drawEnemies(Canvas canvas) {

        if (enemiesToDeploy != null && !enemiesToDeploy.isEmpty()) {
            for (EnemyData enemy : enemiesToDeploy) {
                if (enemy != null) {

                    canvas.drawBitmap(enemyBitmaps[enemy.bitmapIndex], enemy.x - enemyRadius - shipBlurRadius, enemy.y - enemyRadius, null);
                    if (enemy.alert) {
                        canvas.drawBitmap(alertEnemyBitmaps[enemy.shape], enemy.x - enemyRadius, enemy.y - enemyRadius, alertPaint);
                    }

                    switch (enemy.shieldPosition) {
                        case EnemyData.FRONT:
                            canvas.drawBitmap(shieldBitmap, enemy.x - shieldBitmap.getWidth() / 2,
                                    enemy.y - enemyRadius*2, null);

                            break;
                        case EnemyData.BACK:

                            break;
                        case EnemyData.NONE:
                            break;
                    }
                }
            }
        }
    }

    protected void updateBullets() {

        for (BulletData bullet : bullets) {

            bullet.transMat.reset();

            bullet.transMat.setTranslate(bullet.x, bullet.y - bulletBitmap.getHeight() / 2 + shipBlurRadius);

            bullet.shadowTransMat.reset();
            bullet.shadowTransMat.setTranslate(bullet.x, bullet.y);
            bullet.transMat.preConcat(bullet.rotMat);
            bullet.shadowTransMat.preConcat(bullet.rotMat);

            if (bullet.shouldDelete || dead) {
                bullet.shouldDelete = true;
                bulletsToDelete.add(bullet);
                continue;
            }

            for (EnemyData enemy : enemiesToDeploy) {
                if (enemy == null) {
                    continue;
                }
                if (Math.abs((bullet.x + bullet.length/2) - enemy.x) <= enemyRadius &&
                        Math.abs(bullet.y - enemy.y) <= enemyRadius && bullet.destination > 0) {
                    if (enemy.shieldPosition != EnemyData.NONE) {
                        bullet.shouldDelete = true;
                        bulletsToDelete.add(bullet);
                    } else {
                        destroyEnemy(bullet, enemy);
                    }
                } else if (Math.abs((bullet.x + bullet.length/2) - enemy.x) <= enemyRadius &&
                        Math.abs(bullet.y - enemy.y) <= enemyRadius && bullet.destination < 0) {
                    destroyEnemy(bullet, enemy);
                }
            }
            bullet.move(bullet.speedX, bullet.speedY);
        }
        if (!bulletsToDelete.isEmpty()) {
            bullets.removeAll(bulletsToDelete);
            bulletsToDelete.clear();
        }

    }

    protected void drawBullets(Canvas canvas) {
        for (BulletData bullet : bullets) {
            //canvas.drawBitmap(bulletShadowBitmap, bullet.shadowTransMat, null);
            bulletPaint.setAlpha((int) (bullet.getLifeSpan() * 0xff));
            canvas.drawBitmap(bulletBitmap, bullet.transMat, bulletPaint);
        }
    }

    public void updateScrollingSectionPosition() {
        scrollingX1 -= scrollSpeed1;
        scrollingX2 -= scrollSpeed2;
        if (scrollingX1 <= -scrollingX1Start) {
            scrollingX1 = scrollingX1Start;
            scrollingRotMatrix1.setRotate((float) (5 + Math.random()*23), scrollingSectionBitmap.getWidth() / 2,
                    scrollingSectionBitmap.getHeight() / 2);
            scrollSpeed1 = (float)(0.2f + Math.random()*0.6f);
        }

        if (scrollingX2 <= -scrollingX2Start) {
            scrollingX2 = scrollingX2Start;
            scrollingRotMatrix2.setRotate((float) (-5 + Math.random()*-23), scrollingSectionBitmap.getWidth() / 2,
                    scrollingSectionBitmap.getHeight() / 2);
            scrollSpeed2 = (float)(0.2f + Math.random()*0.6f);

        }

        scrollingTransMatrix1.setTranslate(scrollingX1, -(scrollingSectionBitmap.getHeight() - screenHeight) / 2);
        scrollingTransMatrix1.preConcat(scrollingRotMatrix1);
        scrollingTransMatrix2.setTranslate(scrollingX2, -(scrollingSectionBitmap2.getHeight() - screenHeight) / 2);
        scrollingTransMatrix2.preConcat(scrollingRotMatrix2);
    }

    public void startGameTitleTransition() {
        gameTitleTransitionPct = 1.f;
        if (soundToggle != null) {
            soundToggle.setVisibility(View.GONE);
        }
    }

    public void updateGameTitleTransition() {
        if (gameTitleTransitionPct < 2.f && gameTitleTransitionPct > 0.1f) {
            gameTitleTransitionPct = MathUtil.lerp(gameTitleTransitionPct, 0.f, 0.2f);
            titlePaint.setAlpha((int) (gameTitleTransitionPct * 0xff));
            highScorePaint.setAlpha((int) (gameTitleTransitionPct * 0xff));
            iconPaint.setAlpha((int) (gameTitleTransitionPct * 0xff));

        } else if (gameTitleTransitionPct <= 0.1f){
            gameController.setGameState(GameController.TRANSITIONING_TO_NEXT_LEVEL);
            gameTitleTransitionPct = 0.f;
            titlePaint.setAlpha(0xff);
            highScorePaint.setAlpha(0xff);
            iconPaint.setAlpha(0xff);

        }
    }

    public void startCreditsTransition() {

        ValueAnimator creditsAnimator = ValueAnimator.ofFloat(creditsText.getTranslationY(), -getHeight() +(getHeight()-creditsText.getHeight())/2);
        creditsAnimator.setInterpolator(new LinearInterpolator());
        creditsAnimator.setDuration(3000);
        creditsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                creditsText.setTranslationY((Float) animation.getAnimatedValue());
                scorePaint.setAlpha((int) (Math.max(0.f, 1.f - animation.getAnimatedFraction() * 4) * 0xff));

                levelCompletionPaint.setAlpha((int) (Math.max(0.f, 1.f - animation.getAnimatedFraction() * 4) * 0x88));
                livesPaint.setAlpha((int) (Math.max(0.f, 1.f - animation.getAnimatedFraction() * 4) * 0xff));
            }
        });

        creditsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                creditsText.setVisibility(View.VISIBLE);
                scorePaint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endCreditsTransition();
                    }
                }, 2000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        creditsAnimator.start();

    }

    public void endCreditsTransition() {

        ValueAnimator creditsAnimator = ValueAnimator.ofFloat(creditsText.getTranslationY(), -getHeight() - creditsText.getHeight());
        creditsAnimator.setInterpolator(new LinearInterpolator());
        creditsAnimator.setDuration(3000);
        creditsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                creditsText.setTranslationY((Float) animation.getAnimatedValue());
            }
        });

        creditsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {


            }

            @Override
            public void onAnimationEnd(Animator animation) {

                startStatsTransition();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        creditsAnimator.start();

    }

    public void startStatsTransition() {
        ValueAnimator statsAnimator = ValueAnimator.ofFloat(statsText.getTranslationY(), -getHeight() + ((getHeight() - statsText.getHeight()) / 2));
        statsAnimator.setInterpolator(new LinearInterpolator());
        statsAnimator.setDuration(2500);
        statsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                statsText.setTranslationY((Float) animation.getAnimatedValue());

            }
        });

        statsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                String stats = getResources().getString(R.string.stats_text);
                stats = String.format(stats, scoreFormatter.format(gameController.getCurrentScore()),
                        gameController.getCumulativeKills(), gameController.getCumulativeDeaths(),
                        gameController.getMaxCombo(), gameController.getCumulativeEscapedEnemies());
                statsText.setText(stats);
                statsText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                synchronized (enemyLock) {
                    gameController.setGameState(GameController.END_STATS);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        statsAnimator.start();
    }

    public void startStatsFadeOutTransition() {
        ValueAnimator statsAnimator = ValueAnimator.ofFloat(1.f, 0);
        statsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                statsText.setAlpha((Float) animation.getAnimatedValue());
                scorePaint.setAlpha((int) (animation.getAnimatedFraction() * 0xff));
                levelCompletionPaint.setAlpha((int) (animation.getAnimatedFraction()*0x88));
                livesPaint.setAlpha((int) (animation.getAnimatedFraction() * 0xff));
            }
        });

        statsAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                scorePaint.setShadowLayer(textShadowRadius, textShadowOffset, textShadowOffset,
                        getResources().getColor(R.color.shadow_color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                creditsText.setTranslationY(0);
                statsText.setTranslationY(0);
                statsText.setVisibility(View.INVISIBLE);
                creditsText.setVisibility(View.INVISIBLE);
                statsText.setAlpha(1.f);
                scorePaint.setAlpha(0xff);
                levelCompletionPaint.setAlpha(0x88);
                livesPaint.setAlpha(0xff);
                gameController.proceedToNextLevel();
                gameController.setGameState(GameController.TRANSITIONING_TO_NEXT_LEVEL);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        statsAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        creditsText.setMaxWidth((int) (width * 0.9f));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutSoundToggle();
        int marginLeft = (getWidth() - creditsText.getWidth()) / 2;
        int marginTop = getHeight();
        creditsText.layout(marginLeft, marginTop, marginLeft + creditsText.getWidth(),
                marginTop + creditsText.getHeight());
        marginLeft = (getWidth() - statsText.getWidth()) / 2;
        marginTop = getHeight();
        statsText.layout(marginLeft, marginTop, marginLeft + statsText.getWidth(),
                marginTop + statsText.getHeight());
    }

    protected void layoutSoundToggle() {
        if (soundToggle != null) {
            int marginLeft = (getWidth() - soundToggle.getWidth())/2;
            int marginTop = 0;
            soundToggle.layout(marginLeft, marginTop, marginLeft + soundToggle.getWidth(), marginTop + soundToggle.getHeight());
        }
    }

    public void updateLevelTitleTransition() {
        levelTitleTransitionTicker++;

        if (levelTitleTransitionTicker < 60) {
            levelTitleTransitionPct = MathUtil.lerp(levelTitleTransitionPct, 0.5f, 0.1f);
        } else if (levelTitleTransitionTicker > 60 && levelTitleTransitionTicker < 120) {
            levelTitleTransitionPct = MathUtil.lerp(levelTitleTransitionPct, 1.25f, levelTitleTransitionPct *0.1f);
        } else if (levelTitleTransitionTicker >= 120) {
            levelTitleTransitionPct = 0;
            levelTitleTransitionTicker = 0;
            gameController.setGameState(GameController.PLAYING);
            gameTitleTransitionPct = 0.f;
        }
    }

    public void drawLevelTitleTransition(Canvas canvas) {

        if (gameController.getGameState() == GameController.TRANSITIONING_TO_NEXT_LEVEL) {
            canvas.drawText(String.format("Level %d", gameController.getCurrentLevel()), (1.f - levelTitleTransitionPct) * getWidth(),
                    (getHeight() - (titlePaint.ascent() + titlePaint.descent()))/2, titlePaint);

        }

    }

    public void drawTitle(Canvas canvas) {
        if (gameController.getGameState() == GameController.TITLE) {

            canvas.drawBitmap(titleIcon, (getWidth() - titleIcon.getWidth()) / 2, getHeight() / 3 - titleIcon.getHeight() / 2, iconPaint);
            int marginTop = getHeight()/3 + titleIcon.getHeight();
            canvas.drawText(String.format("%s", getResources().getString(R.string.app_name)), getWidth()/2,
                    marginTop, titlePaint);

            marginTop += titlePaint.getTextSize() + getHeight()/32;
            if (gameController.getHighScore() > 0) {
                canvas.drawText("High Score", getWidth()/2, marginTop, highScorePaint);
                marginTop += highScorePaint.getTextSize() + getHeight()/32;
                canvas.drawText(String.format("%s", scoreFormatter.format(gameController.getHighScore())), getWidth()/2,
                        marginTop, highScorePaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        gameController.addListener(gameListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gameController.removeListener(gameListener);
    }

    public void handleEnemyCollision(EnemyData enemy) {
    }

    public void addExplosion(float explosionX, float explosionY, int color) {
        systemController.notifyOnExplosionSoundRequested();
        final ExplosionData data = new ExplosionData();
        data.x = explosionX;
        data.y = explosionY;
        data.color = color;
        final ValueAnimator circleAnim = ValueAnimator.ofFloat(0, 1);
        circleAnim.setDuration(800);
        circleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                data.radius = (int) (getHeight() / 2 * animation.getAnimatedFraction());
                data.alpha = (int) (255 * (1 - animation.getAnimatedFraction()));

            }
        });

        circleAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                data.radius = 0;
                data.alpha = 0;
                data.shouldDelete = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        explosions.add(data);
        post(new Runnable() {
            @Override
            public void run() {
                circleAnim.start();
            }
        });

    }

    protected void initFrameTask() {

    }

    protected void updateScoreTextScale() {

        if (scoreScale - 1.f > 0.1f) {
            scoreScale = MathUtil.lerp(scoreScale, 1.f, 0.3f);
        } else {
            scoreScale = 1.f;
        }
        if (scorePaint.getTextSize() != scoreScale*textSizePixels) {
            scorePaint.setTextSize(scoreScale * textSizePixels);
        }

        if (scoreSkew < -0.1f) {
            scoreSkew = MathUtil.lerp(scoreSkew, 0.f, 0.1f);
            scorePaint.setColor(alertColor);

        } else {
            scoreSkew = 0;
            scorePaint.setColor(Color.WHITE);
        }

        if (scorePaint.getTextSkewX() != scoreSkew) {
            scorePaint.setTextSkewX(scoreSkew);
        }
        if (achievementTicker > 0) {
            achievementTicker--;
        } else if (!TextUtils.isEmpty(currentAchievementString)){
            currentAchievementString = null;
        }
    }

    protected void updateHeartbeat() {
        switch (heartbeatDirection) {
            case UP:
                heartbeat = MathUtil.lerp(heartbeat, 1.f, 0.17f);
                if (1.f - heartbeat < 0.01) {
                    heartbeatDirection = DOWN;
                }

                break;
            case DOWN:
                heartbeat = MathUtil.lerp(heartbeat, 0.f, 0.17f);
                if (heartbeat < 0.01) {
                    heartbeatDirection = UP;
                }
                break;
        }

        alertPaint.setAlpha((int) (heartbeat * 0xff));
    }

    protected void updateBoosterPulse() {

        boosterPointX = MathUtil.lerp(boosterPointX, boosterLerpTargetX, boosterLerpRateX);

        if (Math.abs(boosterPointX - boosterLerpTargetX) < 0.1f) {
            if (isManeuvering || gameController.getGameState() == GameController.SHOWING_LEVEL_WIN){
                boosterLerpTargetX = (float) (-boosterRangeXManeuver + Math.random()*boosterRangeXManeuver*2);
            } else {
                boosterLerpTargetX = (float) (-boosterRangeX + Math.random() * boosterRangeX * 2);
            }
        }

        boosterPointY = MathUtil.lerp(boosterPointY, boosterLerpTargetY, boosterLerpRateY);

        if (Math.abs(boosterPointY - boosterLerpTargetY) < 0.1f) {
            boosterLerpTargetY = (float) (-boosterRangeY + Math.random()*boosterRangeY*2);
        }

        boosterPointSmallX = MathUtil.lerp(boosterPointSmallX, boosterLerpTargetSmallX, boosterLerpRateSmallX);

        if (Math.abs(boosterPointSmallX - boosterLerpTargetSmallX) < 0.1f) {
            if (isManeuvering || gameController.getGameState() == GameController.SHOWING_LEVEL_WIN){
                boosterLerpTargetSmallX = (float) (-boosterRangeXManeuver + Math.random()*boosterRangeXManeuver*2);
            } else {
                boosterLerpTargetSmallX = (float) (-boosterRangeX + Math.random() * boosterRangeX * 2);
            }

        }

        boosterPointSmallY = MathUtil.lerp(boosterPointSmallY, boosterLerpTargetSmallY, boosterLerpRateSmallY);

        if (Math.abs(boosterPointSmallY - boosterLerpTargetSmallY) < 0.1f) {
            boosterLerpTargetSmallY = (float) (-boosterRangeY + Math.random()*boosterRangeY*2);
        }
    }

    @Override
    public void onActivityPause() {
        if (frameTimer != null) {
            frameTimer.cancel();
            frameTimer.purge();
        }
        if (frameTask != null) {
            frameTask.cancel();
            frameTask = null;
            frameTimer = null;

        }
    }

    @Override
    public void onActivityResume() {
        if (frameTimer == null) {
            frameTimer = new Timer();

        }
        if (frameTask == null) {
            initFrameTask();
        } else {
            frameTask.cancel();
            frameTask = null;
            initFrameTask();
        }
        frameTimer.schedule(frameTask, 0, 16);
    }

    protected void logScreenData() {
        Configuration config = getResources().getConfiguration();
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        String densityString = "?";

        if (metrics.densityDpi == DisplayMetrics.DENSITY_LOW) {
            densityString = "ldpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
            densityString = "mdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
            densityString = "hdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
            densityString = "xhdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH) {
            densityString = "xxhdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {
            densityString = "xxxhdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_560) {
            densityString = "560";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_400) {
            densityString = "400";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_280) {
            densityString = "280";
        }

        Log.d("ScreenData", "px: " + metrics.widthPixels + " x " + metrics.heightPixels +

                "\ndp: " + config.screenWidthDp + " x " + config.screenHeightDp + "\n" +
                "density: " + densityString + " / " + metrics.densityDpi);

    }
    public static class ExplosionData {
        public float radius;
        public int alpha;
        public float x;
        public float y;
        public int color;
        public boolean shouldDelete;
    }

    public void makeShip() {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(shipLength, shipHeight / 2);
        path.lineTo(0, shipHeight);
        path.close();
        Bitmap shipTexture = BitmapFactory.decodeResource(getResources(), R.mipmap.blue4);
        shipBitmap = PaperGen.makeShipBitmap(path, shipTexture, shipLength, shipHeight);
        shipShadow = PaperGen.makeShadow(path, shipLength, shipHeight, shipBlurRadius);
    }

    public Bitmap makeLifeBitmap() {

        int bitmapWidth = getResources().getDimensionPixelSize(R.dimen.life_bitmap_width);
        float triangleSideLength = bitmapWidth*0.8f;

        Bitmap lifeBitmap = Bitmap.createBitmap(bitmapWidth, bitmapWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(lifeBitmap);

        Path path = new Path();
        float triangleHeight = triangleSideLength;
        float topMargin = (lifeBitmap.getHeight() - triangleHeight)/2;
        float leftMargin = (lifeBitmap.getWidth() - triangleSideLength)/2;

        path.moveTo(lifeBitmap.getWidth() / 2, topMargin);
        path.lineTo(leftMargin + triangleSideLength, topMargin + triangleHeight);
        path.lineTo(leftMargin, topMargin + triangleHeight);
        path.close();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setARGB(255, 0xa1, 0xa1, 0xa1);
        canvas.drawPath(path, paint);
        return lifeBitmap;
    }

    protected float getEnemyOrigin() { return -1; }

    public void addMoreEnemies(ArrayList<EnemyData> lane, int laneSize, int yPos, float rotAngle) {
        while (laneSize != 0) {
            if (Math.random() <= gameController.getExistenceProb()) {
                EnemyData enemy = new EnemyData();
                enemy.y = yPos;
                enemy.screenBoundary = getWidth();
                enemy.x = enemy.origin = getEnemyOrigin();
                if (gameController.getShieldMode() == GameController.SHIELDS && Math.random() <= gameController.getShieldProb()) {
                    enemy.shieldPosition = EnemyData.FRONT;
                }
                enemy.speed = ((float)getMeasuredWidth())/gameController.getEnemyTravelTime();
                enemy.speedX = (float)(enemy.speed * Math.cos(Math.toRadians(rotAngle)));
                enemy.speedY = (float)(enemy.speed * Math.sin(Math.toRadians(rotAngle)));
                enemy.bitmapIndex = enemy.shape = (int)(Math.floor(Math.random()*3));

                enemy.destination = -shipLength*1.5f;
                lane.add(enemy);
            } else {
                EnemyData enemy = new EnemyData();
                enemy.shouldDelete = true;
                lane.add(enemy);
            }
            laneSize--;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(scrollingSectionBitmap, scrollingTransMatrix1, null);
        canvas.drawBitmap(scrollingSectionBitmap2, scrollingTransMatrix2, null);
    }

    public void setScoreScaleForEvent(float scale) {
        if (achievementTicker == 0) {
            scoreScale = scale;
        }

    }

    public void updateProgressAndLivesLayout() {
        drawnCompletionPercent = MathUtil.lerp(drawnCompletionPercent, gameController.getCompletionPercent(), 0.2f);
        livesMarginLeft = MathUtil.lerp(livesMarginLeft, (getWidth() - (gameController.getRemainingLives()*(lifeBitmap.getWidth()))) / 2, 0.2f);
    }

    public void drawProgressScoreAndLives(Canvas canvas) {

        canvas.drawArc(levelCompletionRect, 270, drawnCompletionPercent * 360, false, levelCompletionPaint);
        if (achievementTicker == 0.f) {
            canvas.drawText(scoreFormatter.format(gameController.getCurrentScore()), getWidth() / 2, getHeight() / 8 - (scorePaint.ascent() + scorePaint.descent()) / 2, scorePaint);
        } else if (!TextUtils.isEmpty(currentAchievementString)){
            canvas.drawText(currentAchievementString, getWidth() / 2, getHeight() / 8 - (scorePaint.ascent() + scorePaint.descent()) / 2, scorePaint);
        }


        if (dead) {
            if (gameController.getRemainingLives() > 0) {
                canvas.drawText("New Ship in " + restartCount, getWidth() / 2, (getHeight() - (titlePaint.ascent() + titlePaint.descent())) / 2, titlePaint);
            } else {
                canvas.drawText("Game Over", getWidth() / 2, (getHeight() - (titlePaint.ascent() + titlePaint.descent())) / 2, titlePaint);
            }

        }

        for (int i = 0; i < gameController.getRemainingLives(); i++) {
            canvas.drawBitmap(lifeBitmap, livesMarginLeft + lifeBitmap.getWidth()*i, getHeight()*7/8, livesPaint);
        }
    }

    public void drawCombo(Canvas canvas) {
        if (gameController.getComboCount() > 1) {
            if (gameController.getComboCount() > 6) {
                comboPaint.setColor(COMBO_HUGE_COLOR);
            } else if (gameController.getComboCount() > 4) {
                comboPaint.setColor(BOOSTER_COLOR);
            }
            canvas.drawText(String.format("%d", gameController.getComboCount()), currentX+maneuverX + shipLength,
                    currentY+maneuverY+shipHeight, comboPaint);
        }
    }
}
