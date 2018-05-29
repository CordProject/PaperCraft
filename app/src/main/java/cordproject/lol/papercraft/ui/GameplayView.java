package cordproject.lol.papercraft.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.TimerTask;

import cordproject.lol.papercraft.controller.GameController;
import cordproject.lol.papercraft.util.MathUtil;
import pro.rudloff.papercraft.R;
import cordproject.lol.papercraft.entity.BulletData;
import cordproject.lol.papercraft.entity.EnemyData;

public class GameplayView extends MainView {

    private float screenRadius;

    private float currentManeuverDist;

    private ValueAnimator maneuver;
    private ValueAnimator maneuverBack;


    public GameplayView(Context context) {
        this(context, null);
    }

    public GameplayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initLanes();
        initFrameTask();
    }

    @Override
    protected void initFrameTask() {
        frameTask = new TimerTask() {
            @Override
            public void run() {
                // this fires every 16th of a second...

                // 60 ticks = 1 second
                // 30 ticks = 1/2 second


                updateScrollingSectionPosition();
                synchronized (enemyLock) {
                    switch (gameController.getGameState()) {

                        case GameController.TITLE:
                            updateGameTitleTransition();
                            break;

                        case GameController.END_CREDITS:

                            break;

                        case GameController.TRANSITIONING_TO_NEXT_LEVEL:
                            updateBullets();
                            updateScoreTextScale();
                            updateLevelTitleTransition();
                            enemyDeployIntervalTicker = (int) gameController.getEnemyInterval();
                            break;

                        case GameController.SHOWING_LEVEL_WIN:
                            updateBullets();
                            updateBoosterPulse();
                            explodeAllEnemies();
                            updateExplosions();
                            updateShipWinTransition();
                            updateScoreTextScale();
                            break;

                        case GameController.PLAYING:
                            if (!shipOnScreen && startX != 0) {
                                currentX = MathUtil.lerp(currentX, startX, 0.1f);
                                if (startX - currentX <= 0.05f) {
                                    currentX = startX;
                                    shipOnScreen = true;
                                }
                            }
                            if (!dead) {
                                if (!shouldNotFire) {
                                    if (!shouldFireBackwards && bulletIntervalTicker == 15) {
                                        BulletData data = new BulletData();
                                        data.x = muzzleRect.centerX();

                                        data.destination = getWidth()*7/8;
                                        data.y = muzzleRect.centerY();
                                        data.speedX = (float) (3 * Math.cos(Math.toRadians(radiusAngle)));
                                        data.speedY = (float) (3 * Math.sin(Math.toRadians(radiusAngle)));
                                        data.length = getWidth() / 32;

                                        data.rotAngle = rotAngle + radiusAngle;
                                        data.rotMat.setRotate(data.rotAngle, bulletRect.centerX(), bulletRect.centerY());
                                        bullets.add(data);
                                        bulletIntervalTicker = 0;
                                    } else if (shouldFireBackwards && bulletIntervalTicker == 3) {
                                        BulletData data = new BulletData();
                                        data.x = muzzleRect.centerX();
                                        data.destination = -shipLength * 1.5f;
                                        data.y = muzzleRect.centerY();
                                        data.speedX = (float) (9 * Math.cos(Math.toRadians(radiusAngle)));
                                        data.speedY = (float) (9 * Math.sin(Math.toRadians(radiusAngle)));
                                        data.length = getWidth() / 32;
                                        data.rotAngle = rotAngle + radiusAngle;
                                        data.rotMat.setRotate(data.rotAngle, bulletRect.centerX(), bulletRect.centerY());
                                        bullets.add(data);
                                        bulletIntervalTicker = 0;
                                    }
                                    bulletIntervalTicker++;
                                }

                                if (invulnerabilityTicker == 0) {
                                    if (!shouldAddMoreEnemies && enemyDeployIntervalTicker == gameController.getEnemyInterval()) {
                                        if (enemiesToDeploy.size() < gameController.getMaxConcurrentEnemies()) {
                                            if (!lane1.isEmpty()) {
                                                enemiesToDeploy.add(lane1.remove(lane1.size() - 1));
                                            }
                                            if (!lane2.isEmpty()) {
                                                enemiesToDeploy.add(lane2.remove(lane2.size() - 1));
                                            }
                                            if (!lane3.isEmpty()) {
                                                enemiesToDeploy.add(lane3.remove(lane3.size() - 1));
                                            }
                                        }

                                        if (lane1.isEmpty()) {
                                            shouldAddMoreEnemies = true;
                                        }
                                        enemyDeployIntervalTicker = 0;
                                    } else if (shouldAddMoreEnemies && getHeight() != 0) {
                                        addMoreEnemies(lane1, enemyCount, getHeight() / 2, -8 + gameController.getRotAngleRand());
                                        addMoreEnemies(lane2, enemyCount, getHeight() / 2, gameController.getRotAngleRand());
                                        addMoreEnemies(lane3, enemyCount, getHeight() / 2, 8 + gameController.getRotAngleRand());
                                        shouldAddMoreEnemies = false;
                                        enemyDeployIntervalTicker = 0;
                                    }

                                    enemyDeployIntervalTicker++;
                                }

                            }
                            if (dead) {
                                deadTicker++;
                                if (deadTicker < 60) {
                                    restartCount = 3;
                                }
                                if (deadTicker == 60) {
                                    restartCount = 2;
                                }
                                if (deadTicker == 120) {
                                    restartCount = 1;
                                }
                                if (deadTicker == 180) {
                                    if (gameController.getRemainingLives() == 0) {
                                        gameTitleTransitionPct = 2.f;
                                        levelTitleTransitionPct = 0;
                                        gameController.restartGame();
                                        clearEnemies();
                                        clearBullets();
                                        createEnemyBitmaps();
                                    }

                                    restartCount = 0;
                                    placeShipOffscreen();
                                    maneuverX = 0;
                                    currentY = getMeasuredHeight() / 2;
                                    maneuverY = 0;
                                    rotAngle = 0;
                                    radiusAngle = 0;
                                    paint.setAlpha(255);
                                    shouldNotFire = false;
                                    dead = false;
                                    deadTicker = 0;
                                }
                            }
                            updateBullets();

                            updateEnemies();

                            updateExplosions();
                            updateBoosterPulse();
                            updateHeartbeat();
                            updateScoreTextScale();
                            updatePlayer();
                            break;
                    } // end switch(gameState)
                } // end synchronized(enemyLock)
                updateProgressAndLivesLayout();
                postInvalidate();
            }
        };
    }

    private void initLanes() {
        lane1 = new ArrayList<>();
        lane2 = new ArrayList<>();
        lane3 = new ArrayList<>();
    }

    @Override
    protected float getEnemyOrigin() {
        return screenRadius*3.f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);

        screenRadius = MeasureSpec.getSize(widthMeasureSpec)/2;
        maneuverDistance = MeasureSpec.getSize(widthMeasureSpec)/3;
        startX = screenRadius*.3f;
        currentX = -shipLength;
        currentY = getMeasuredHeight()/2;
        int marginLeft = (int) ((getMeasuredWidth() - levelCompletionDiameter)/2);
        int marginTop = (int) (getMeasuredHeight()/8 - levelCompletionDiameter/2);

        levelCompletionRect.set(marginLeft, marginTop, marginLeft + levelCompletionDiameter,
                marginTop + levelCompletionDiameter);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (enemyLock) {

            transMatrix.reset();
            rotMatrix.reset();

            transMatrix.setTranslate(currentX + maneuverX, currentY + maneuverY - shipHeight / 2);
            rotMatrix.setRotate(rotAngle + radiusAngle, currentX + maneuverX + shipLength / 2, currentY + maneuverY);
            transMatrix.postConcat(rotMatrix);

            shadowMatrix.reset();
            shadowRotMatrix.reset();

            shadowMatrix.setTranslate(currentX + maneuverX - shipBlurRadius, currentY + maneuverY - shipHeight * .4f);
            shadowRotMatrix.setRotate(rotAngle + radiusAngle, shipShadow.getWidth() / 2, shipShadow.getHeight() / 2);
            shadowMatrix.preConcat(shadowRotMatrix);

            if (shipBitmap != null && !dead) {

                drawBullets(canvas);
                if (invulnerabilityTicker > 10 && invulnerabilityTicker < 20 ||
                        invulnerabilityTicker > 30 && invulnerabilityTicker < 40 ||
                        invulnerabilityTicker > 50 && invulnerabilityTicker < 60 ||
                        invulnerabilityTicker > 70 && invulnerabilityTicker < 80 ||
                        invulnerabilityTicker > 90 && invulnerabilityTicker < 100 ||
                        invulnerabilityTicker > 110 && invulnerabilityTicker < 120) {
                    drawBooster(canvas);
                    canvas.drawBitmap(shipShadow, shadowMatrix, null);
                    canvas.drawBitmap(shipBitmap, transMatrix, null);
                } else if (invulnerabilityTicker == 0){
                    canvas.drawBitmap(shipShadow, shadowMatrix, null);
                    drawBooster(canvas);
                    canvas.drawBitmap(shipBitmap, transMatrix, null);
                }
            }

            shipRect.set(0, 0, shipLength*3/4, shipHeight);
            muzzleRect.set(shipLength * 3 / 4, 0, shipLength, shipHeight);
            transMatrix.mapRect(shipRect);
            transMatrix.mapRect(muzzleRect);
            drawExplosions(canvas);
            drawLevelTitleTransition(canvas);
            drawTitle(canvas);

            drawEnemies(canvas);
            if (gameController.getGameState() != GameController.TITLE) {
                drawProgressScoreAndLives(canvas);
            }
            drawCombo(canvas);
        }
    }

    private void drawExplosions(Canvas canvas) {
        for (ExplosionData data : explosions) {
            explosionPaint.setColor(data.color);
            explosionPaint.setAlpha(data.alpha);
            canvas.drawCircle(data.x, data.y, data.radius, explosionPaint);
        }
    }

    @Override
    public void handleEnemyCollision(EnemyData enemy) {

        if (Math.abs(enemy.x - (shipRect.centerX())) <= enemyRadius &&
                Math.abs(enemy.y - (shipRect.centerY())) <= enemyRadius && !dead && invulnerabilityTicker == 0) {

            addExplosion(shipRect.centerX(), shipRect.centerY(),
                    getResources().getColor(R.color.ship_color));
            gameController.decrementRemainingLives();
            dead = true;
            gameController.recordDeath();
            invulnerabilityTicker = 120;
            paint.setAlpha(0);
            shouldNotFire = true;
        }
    }

    private void drawBooster(Canvas canvas) {
        boosterPath.reset();

        boosterPath.moveTo(0, 0);
        boosterPath.lineTo(0, boosterHeight);
        boosterPath.lineTo(-boosterLength + boosterPointX, boosterHeight/2 + boosterPointY);

        boosterPath.close();
        boosterTransMat.setTranslate(currentX + maneuverX, currentY + maneuverY - boosterHeight*5/8);

        boosterRotMat.setRotate(rotAngle + radiusAngle, shipLength / 2, boosterHeight/2);
        boosterTransMat.preConcat(boosterRotMat);
        boosterPath.transform(boosterTransMat);
        canvas.drawPath(boosterPath, boosterPaint);

        boosterPathSmall.reset();

        boosterPathSmall.moveTo(0, 0);
        boosterPathSmall.lineTo(0,boosterHeight*0.75f);
        boosterPathSmall.lineTo(-boosterLength*0.75f + boosterPointSmallX, boosterHeight*0.375f + boosterPointSmallY);

        boosterPathSmall.close();
        boosterTransMatSmall.setTranslate(currentX + maneuverX, currentY + maneuverY - (boosterHeight*0.75f)/4);

        boosterRotMatSmall.setRotate(rotAngle + radiusAngle, shipLength/2, boosterHeight*0.375f);
        boosterTransMatSmall.preConcat(boosterRotMatSmall);
        boosterPathSmall.transform(boosterTransMatSmall);
        canvas.drawPath(boosterPathSmall, boosterPaintSmall);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        synchronized (enemyLock) {
            if (gameController.getGameState() == GameController.TRANSITIONING_TO_NEXT_LEVEL) {
                return false;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    postDelayed(quitRunnable, 500);
                    break;
                case MotionEvent.ACTION_DOWN:

                    lastY = downY = event.getY();
                    lastX = downX = event.getX();
                    downTime = event.getDownTime();
                    removeCallbacks(quitRunnable);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (gameController.getGameState() == GameController.PLAYING) {
                        deltaY = event.getY() - lastY;
                        deltaX = event.getX() - lastX;
                        downDeltaY = event.getY() - downY;
                        downDeltaX = event.getX() - downX;
                        lastX = event.getX();
                        lastY = event.getY();

                        radiusAngle = MathUtil.lerp(radiusAngle, MathUtil.toDegrees(Math.asin((screenRadius - (currentY + deltaY)) / (screenRadius * 3.f))), 0.9f);
                        radiusAngle = Math.min(11, Math.max(radiusAngle, -11));

                        currentY = (float) (screenRadius * 3.f * -Math.sin(MathUtil.toRadians(radiusAngle))) + screenRadius;
                        maneuverY = (float) (currentManeuverDist * Math.sin(Math.toRadians(radiusAngle)));
                        maneuverX = (float) (currentManeuverDist * Math.cos(Math.toRadians(radiusAngle)));
                        currentX = (float) ((screenRadius * -Math.cos(MathUtil.toRadians(radiusAngle))) + 1.3f * screenRadius);
                        transMatrix.reset();

                        if (Math.abs(deltaY) >= touchSlop) {
                            lastDirection = deltaY > 0 ? BACKWARD : deltaY < 0 ? FORWARD : NEUTRAL;

                        }
                        if (event.getPointerCount() == 1) {
                            removeCallbacks(quitRunnable);
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    removeCallbacks(quitRunnable);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    removeCallbacks(quitRunnable);
                    break;
                case MotionEvent.ACTION_UP:

                    deltaX = event.getX() - lastX;
                    deltaY = event.getY() - lastY;
                    lastX = event.getX();
                    lastY = event.getY();
                    if (Math.abs(deltaY) >= touchSlop) {
                        lastDirection = deltaY > 0 ? BACKWARD : deltaY < 0 ? FORWARD : NEUTRAL;
                    }

                    if (Math.abs(deltaY) < touchSlop && event.getEventTime() - downTime < 250) {
                        switch (gameController.getGameState()) {
                            case GameController.PLAYING:
                                if (!dead) {
                                    startManeuver();
                                }
                                break;
                            case GameController.TITLE:
                                startGameTitleTransition();
                                break;

                            case GameController.END_STATS:
                                startStatsFadeOutTransition();
                                break;
                        }
                    }
                    removeCallbacks(quitRunnable);
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void startManeuver() {
        if (isManeuvering) {
            return;
        } else {
            maneuver = ValueAnimator.ofFloat(0, maneuverDistance);
            maneuver.setDuration(500);
            maneuver.setInterpolator(new DecelerateInterpolator());
            maneuver.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    synchronized (enemyLock) {
                        currentManeuverDist = (float) animation.getAnimatedValue();
                        maneuverX = (float) (currentManeuverDist * Math.cos(Math.toRadians(radiusAngle)));
                        maneuverY = (float) (currentManeuverDist * Math.sin(Math.toRadians(radiusAngle)));
                        boosterLerpRateX = Math.min(LERP_RATE_MAX, boosterLerpRateX +.1f);
                        boosterLerpRateSmallX = Math.min(LERP_RATE_MAX, boosterLerpRateSmallX +.1f);

                        if (animation.getAnimatedFraction() > 0.65f) {
                            int sign = 1;
                            switch (maneuverDirection) {
                                case FORWARD:
                                    sign = -1;
                                    break;

                                case NEUTRAL:
                                case BACKWARD:

                                    break;
                            }

                            rotAngle = sign * 180.f * Math.min(1.f, (animation.getAnimatedFraction() - 0.65f) / 0.25f);
                        } else {
                            maneuverDirection = lastDirection;
                        }
                    }
                }
            });

            maneuver.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                    synchronized (enemyLock) {
                        isManeuvering = true;
                        boosterLerpRateX = 0.5f;
                        shouldNotFire = true;
                    }

                    //stop bullets
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //rotate tank homie 180º
                    //shoot like 4 or 5 and then maneuver back
                    synchronized (enemyLock) {
                        shouldNotFire = false;
                        shouldFireBackwards = true;
                        bulletIntervalTicker = 0;
                    }
                    boosterPaint.setAlpha(0);
                    boosterPaintSmall.setAlpha(0);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endManeuver();
                        }
                    }, 600);
                    boosterPointX = 0;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //do nothing
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //do nothing
                }
            });
            maneuver.start();
        }
    }


    public void endManeuver() {
        maneuverBack = ValueAnimator.ofFloat(maneuverDistance, 0);
        maneuverBack.setDuration(600);
        maneuverBack.setInterpolator(new AccelerateInterpolator());
        maneuverBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                synchronized (enemyLock) {
                    currentManeuverDist = (float) animation.getAnimatedValue();
                    maneuverX = (float) (currentManeuverDist * Math.cos(Math.toRadians(radiusAngle)));
                    maneuverY = (float) (currentManeuverDist * Math.sin(Math.toRadians(radiusAngle)));

                    boosterLerpRateX = Math.min(LERP_RATE_MAX, boosterLerpRateX +.1f);
                    boosterLerpRateSmallX = Math.min(LERP_RATE_MAX, boosterLerpRateSmallX +.1f);


                    int sign = 1;
                    switch (maneuverDirection) {
                        case FORWARD:
                            sign = -1;
                            break;

                        case NEUTRAL:
                        case BACKWARD:

                            break;
                    }
                    if (animation.getAnimatedFraction() > 0.125f) {
                        shouldNotFire = true;
                    }
                    if (animation.getAnimatedFraction() > 0.75f) {
                        rotAngle = 180.f + sign * 180.f * (animation.getAnimatedFraction() - 0.75f) / 0.25f;
                    }
                }
            }
        });

        maneuverBack.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                synchronized (enemyLock) {
                    boosterPaint.setAlpha(255);
                    boosterPaintSmall.setAlpha(255);
                    boosterLerpRateX = 0.5f;

                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //start bullets again
                synchronized (enemyLock) {
                    shouldNotFire = false;
                    isManeuvering = false;
                    shouldFireBackwards = false;
                    boosterPointX = 10;
                    boosterLerpRateX = LERP_RATE_DEFAULT;
                    boosterLerpRateSmallX = LERP_RATE_DEFAULT;
                    bulletIntervalTicker = 0;

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        maneuverBack.start();
    }
}
