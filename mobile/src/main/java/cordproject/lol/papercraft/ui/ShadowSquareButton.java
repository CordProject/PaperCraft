package cordproject.lol.papercraft.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cordproject.lol.papercraft.R;

/**
 * Created by matthewlim on 11/13/15.
 * PaperCraft
 * Copyright 2015 Cord Project Inc.
 */
public class ShadowSquareButton extends FrameLayout {

    private TextView labelText;
    private Paint pressedPaint;
    private Bitmap shadowBitmap;

    private Paint shadowPaint;
    private Paint backgroundPaint;
    private int blurRadius;
    private int buttonWidth;
    private int buttonHeight;
    private Rect shadowRect = new Rect();
    private boolean useDefaultSize = true;

    public ShadowSquareButton(Context context) {
        this(context, null);
    }

    public ShadowSquareButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowSquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.labelText = new TextView(context);
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        this.labelText.setLayoutParams(params);

        this.labelText.setGravity(Gravity.CENTER);
        Typeface robotoLight = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Light.ttf");
        addView(this.labelText);

        labelText.setText("LEADERBOARDS");

        labelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.leaderboard_text_size));
        labelText.setTypeface(robotoLight);
        this.labelText.setTextColor(getResources().getColor(R.color.background_color));
        labelText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        pressedPaint = new Paint();
        pressedPaint.setAlpha(0x33);

        blurRadius = (int) (metrics.widthPixels * 0.025f);
        BlurMaskFilter filter = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.OUTER);
        shadowPaint = new Paint();
        shadowPaint.setColor(getResources().getColor(R.color.shadow_color));
        shadowPaint.setAntiAlias(true);
        shadowPaint.setMaskFilter(filter);
        setWillNotDraw(false);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int marginLeft = 0;
        int marginTop = 0;

        marginLeft = (getMeasuredWidth() - labelText.getMeasuredWidth()) / 2;
        marginTop = (getMeasuredHeight() - labelText.getMeasuredHeight()) / 2;
        this.labelText.layout(marginLeft, marginTop, marginLeft + labelText.getMeasuredWidth(),
                marginTop + labelText.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (useDefaultSize) {
            int defaultButtonHeight = (int) (getResources().getDimensionPixelSize(R.dimen.button_height) * 1.1f + blurRadius * 2);
            height = defaultButtonHeight;
        }
        generateShadowBitmap(width, height);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    public void generateShadowBitmap(int width, int height) {

        buttonWidth = width - blurRadius * 2;
        buttonHeight = height - blurRadius * 2;
        int shadowRectWidth = buttonWidth;
        int shadowRectHeight = buttonHeight - blurRadius;
        shadowBitmap = Bitmap.createBitmap(width, shadowRectHeight + blurRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shadowBitmap);
        int rectTop = blurRadius;
        int rectLeft = blurRadius;
        shadowRect.set(rectLeft, rectTop, rectLeft + shadowRectWidth, rectTop + shadowRectHeight);
        canvas.drawRect(shadowRect, shadowPaint);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (shadowBitmap != null) {
            canvas.drawBitmap(shadowBitmap, 0, blurRadius, null);
        }
        canvas.drawRect(blurRadius, blurRadius, blurRadius + buttonWidth,
                blurRadius + buttonHeight, backgroundPaint);

        if (isPressed()) {
            canvas.drawRect(blurRadius, blurRadius, blurRadius + buttonWidth,
                    blurRadius + buttonHeight, pressedPaint);
        }
    }


    public void setText(String text) {
        this.labelText.setText(text);
    }

    public void setTextColor(int textColor) {
        this.labelText.setTextColor(textColor);
        pressedPaint.setColor(textColor & 0x33ffffff);
    }
}
