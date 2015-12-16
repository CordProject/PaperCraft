package cordproject.lol.papercraft.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import cordproject.lol.papercraft.PaperCraftApplicationP;
import cordproject.lol.papercraft.R;
import cordproject.lol.papercraft.controller.ControllerP;
import cordproject.lol.papercraft.controller.SystemControllerP;
import cordproject.lol.papercraftshared.graphics.PaperGen;
import cordproject.lol.papercraftshared.util.SharedConstants;

public class MainPhoneView extends FrameLayout{


    private int screenHeight;
    private int screenWidth;

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

    protected Timer frameTimer;
    protected TimerTask frameTask;
    private Bitmap titleIcon;
    private final TextPaint titlePaint;
    private ShadowSquareButton leaderboardsButton;
    private ShadowSquareButton achievementsButton;
    private ShadowSquareButton signInButton;
    private int buttonLeftMargin;
    private TextView highScoreTextView;
    private SystemControllerP systemController;
    protected DecimalFormat scoreFormatter = new DecimalFormat("#,###,###");
    private ValueAnimator buttonAnimator;
    private float translationPct = 0;

    private Bitmap androidExperimentsWatermark;
    private Paint watermarkPaint;

    private SystemControllerP.SystemControllerListenerP systemListener = new SystemControllerP.SystemControllerListenerP() {
        @Override
        public void onGameServicesConnectSuccess() {
            signInButton.setVisibility(View.GONE);
            animateButtonsOnscreen();
        }

        @Override
        public void onGameServicesConnectFailure() {
            animateButtonsOffscreen();
        }

        @Override
        public void onGameServicesDisconnected() {
            animateButtonsOffscreen();
        }

        @Override
        public void onGameServicesSignInCancelled() {
            animateButtonsOffscreen();
            signInButton.setVisibility(View.VISIBLE);

        }

        @Override
        public void onHighScoreUpdated(int score) {
            String formattedScore = scoreFormatter.format(score);
            highScoreTextView.setText(String.format(getResources().getString(R.string.high_score_text), formattedScore));
        }
    };

    public MainPhoneView(Context context) {
        this(context, null);
    }

    public MainPhoneView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainPhoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        systemController = (SystemControllerP) PaperCraftApplicationP.getController(ControllerP.SYSTEM_CONTROLLER);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        scrollingSectionBitmap = PaperGen.createScrollingSectionBitmap(getResources().getColor(R.color.scrolling_section_color_1),
                getResources().getColor(R.color.shadow_color),
                (int) (screenWidth * 0.8f), screenHeight, screenWidth);

        scrollingSectionBitmap2 = PaperGen.createScrollingSectionBitmap(getResources().getColor(R.color.scrolling_section_color_2),
                getResources().getColor(R.color.shadow_color),
                (int) (screenWidth * 0.6f), screenHeight, screenWidth);
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

        titleIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.landingscreenphone);
        Typeface robotoLight = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Light.ttf");
        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setStyle(Paint.Style.STROKE);
        titlePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.title_size));
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(robotoLight);
        titlePaint.setShadowLayer(4, 2, 2, getResources().getColor(R.color.shadow_color));
        titlePaint.setColor(0xffffffff);
        leaderboardsButton = new ShadowSquareButton(context);
        leaderboardsButton.setVisibility(View.INVISIBLE);
        leaderboardsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                systemController.notifyOnLeaderboardsRequested();
            }
        });
        addView(leaderboardsButton);

        achievementsButton = new ShadowSquareButton(context);
        achievementsButton.setVisibility(View.INVISIBLE);
        achievementsButton.setText("ACHIEVEMENTS");
        achievementsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                systemController.notifyOnAchievementsRequested();
            }
        });
        addView(achievementsButton);

        signInButton = new ShadowSquareButton(context);
        signInButton.setVisibility(View.GONE);
        signInButton.setText("SIGN IN");
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                systemController.notifyOnGameServicesSignInRequested();
            }
        });
        addView(signInButton);

        leaderboardsButton.post(new Runnable() {
            @Override
            public void run() {
                leaderboardsButton.setTranslationX(-getWidth());
            }
        });

        achievementsButton.post(new Runnable() {
            @Override
            public void run() {
                achievementsButton.setTranslationX(-getWidth());
            }
        });

        highScoreTextView = new TextView(context);
        highScoreTextView.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
        highScoreTextView.setTextColor(getResources().getColor(R.color.lightest_grey));
        highScoreTextView.setGravity(Gravity.CENTER);
        highScoreTextView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(highScoreTextView);
        setWillNotDraw(false);
        SharedPreferences prefs = getContext().getSharedPreferences(SharedConstants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean didSignOut = prefs.getBoolean(SharedConstants.KEY_GPG_SIGNED_OUT, false);
        if (didSignOut && !systemController.isGameServicesConnected()) {
            signInButton.setVisibility(View.VISIBLE);
        }

        androidExperimentsWatermark = BitmapFactory.decodeResource(getResources(), R.mipmap.aex_watermark);
        watermarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        watermarkPaint.setAlpha(0x7f);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        systemController.addListener(systemListener);
        if (systemController.gameServicesConnected) {
            post(new Runnable() {
                @Override
                public void run() {
                    animateButtonsOnscreen();
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        systemController.removeListener(systemListener);
    }

    public void animateButtonsOnscreen() {
        if (buttonAnimator != null) {
            buttonAnimator.cancel();
            buttonAnimator = null;
        }
        buttonAnimator = ValueAnimator.ofFloat(leaderboardsButton.getTranslationX(), 0);
        buttonAnimator.setInterpolator(new DecelerateInterpolator());
        buttonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                leaderboardsButton.setTranslationX(Math.min(0, (Float) animation.getAnimatedValue() + leaderboardsButton.getMeasuredWidth() / 16));
                achievementsButton.setTranslationX((Float) animation.getAnimatedValue());
                leaderboardsButton.setAlpha(animation.getAnimatedFraction());
                achievementsButton.setAlpha(animation.getAnimatedFraction());
            }
        });
        buttonAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                achievementsButton.setVisibility(View.VISIBLE);
                leaderboardsButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                buttonAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        buttonAnimator.start();
    }

    public void animateButtonsOffscreen() {
        if (buttonAnimator != null) {
            buttonAnimator.cancel();
            buttonAnimator = null;
        }
        buttonAnimator = ValueAnimator.ofFloat(leaderboardsButton.getTranslationX(), -getWidth());
        buttonAnimator.setInterpolator(new DecelerateInterpolator());
        buttonAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                leaderboardsButton.setTranslationX(Math.max(-getWidth(), (Float) animation.getAnimatedValue() - leaderboardsButton.getMeasuredWidth() / 16));
                achievementsButton.setTranslationX((Float) animation.getAnimatedValue());
                leaderboardsButton.setAlpha(1.f - animation.getAnimatedFraction());
                achievementsButton.setAlpha(1.f - animation.getAnimatedFraction());
            }
        });
        buttonAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                buttonAnimator = null;
                achievementsButton.setVisibility(View.INVISIBLE);
                leaderboardsButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        buttonAnimator.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


        int marginTop = (int) (getHeight()/4 + titleIcon.getHeight() + titlePaint.getTextSize()/2);
        int marginLeft = (getWidth() - highScoreTextView.getMeasuredWidth())/2;
        highScoreTextView.layout(marginLeft, marginTop, marginLeft + highScoreTextView.getMeasuredWidth(),
                marginTop + highScoreTextView.getMeasuredHeight());
        marginLeft = (getWidth() - leaderboardsButton.getMeasuredWidth())/2;
        marginTop = (getHeight() - buttonLeftMargin - leaderboardsButton.getMeasuredHeight()*2);
        leaderboardsButton.layout(marginLeft, marginTop, marginLeft + leaderboardsButton.getMeasuredWidth(), marginTop + leaderboardsButton.getMeasuredWidth());
        marginTop = (getHeight() - buttonLeftMargin - leaderboardsButton.getMeasuredHeight() * 2);
        signInButton.layout(marginLeft, marginTop, marginLeft + signInButton.getMeasuredWidth(), marginTop + signInButton.getMeasuredWidth());
        marginTop+= leaderboardsButton.getMeasuredHeight();
        achievementsButton.layout(marginLeft, marginTop, marginLeft + achievementsButton.getMeasuredWidth(), marginTop + achievementsButton.getMeasuredWidth());
    }

    public void onActivityResume() {
        if (frameTimer == null) {
            frameTimer = new Timer();

        }
        if (frameTask == null) {
            initFrameTask();
        }
        frameTimer.schedule(frameTask, 0, 16);
    }

    public void initFrameTask() {
        frameTask = new TimerTask() {
            @Override
            public void run() {
                // this fires every 16th of a second...

                // 60 ticks = 1 second
                // 30 ticks = 1/2 second
                updateScrollingSectionPosition();
                postInvalidate();
            }
        };
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(scrollingSectionBitmap, scrollingTransMatrix1, null);
        canvas.drawBitmap(scrollingSectionBitmap2, scrollingTransMatrix2, null);
        canvas.drawBitmap(titleIcon, (getWidth() - titleIcon.getWidth()) / 2, getHeight() / 4 - titleIcon.getHeight() / 2, null);
        int marginTop = (getHeight()/4 + titleIcon.getHeight());
        canvas.drawText(String.format("%s", getResources().getString(R.string.app_name)), getWidth() / 2,
                marginTop, titlePaint);
        int watermarkPadding = getWidth()/32;
        canvas.drawBitmap(androidExperimentsWatermark, watermarkPadding,
                getHeight() - androidExperimentsWatermark.getHeight() - watermarkPadding,
                watermarkPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        buttonLeftMargin = MeasureSpec.getSize(widthMeasureSpec)/6;
        leaderboardsButton.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 3/5, MeasureSpec.EXACTLY),
                heightMeasureSpec);
        signInButton.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 3/5, MeasureSpec.EXACTLY),
                heightMeasureSpec);
        achievementsButton.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 3/5, MeasureSpec.EXACTLY),
                heightMeasureSpec);
    }
}
