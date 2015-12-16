package cordproject.lol.papercraftshared.graphics;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

public class PaperGen {


    public static Bitmap makeEnemyC(Bitmap textureBitmap, float enemyRadius, float blurRadius) {
        Path enemyPath = new Path();
        enemyPath.addCircle(enemyRadius, enemyRadius, enemyRadius, Path.Direction.CW);
        enemyPath.close();

        return makeEnemyShape(enemyPath, textureBitmap, enemyRadius, blurRadius);
    }

    public static Bitmap makeEnemyCAlert(float enemyRadius, int alertColor) {
        Path enemyPath = new Path();
        enemyPath.addCircle(enemyRadius, enemyRadius, enemyRadius, Path.Direction.CW);
        enemyPath.close();

        return makeEnemyShapeAlert(enemyPath, enemyRadius, alertColor);
    }

    public static Bitmap makeEnemyP(Bitmap textureBitmap, float enemyRadius, float blurRadius) {
        Path enemyPath = new Path();

        enemyPath.moveTo(enemyRadius, 0);
        enemyPath.lineTo((float) (enemyRadius + enemyRadius * Math.cos(Math.toRadians(18.f))),
                enemyRadius - (float) (enemyRadius * Math.sin(Math.toRadians(18.f))));
        enemyPath.lineTo((float) (enemyRadius + enemyRadius * Math.sin(Math.toRadians(36.f))),
                enemyRadius + (float) (enemyRadius * Math.cos(Math.toRadians(36.f))));
        enemyPath.lineTo((float) (enemyRadius - enemyRadius * Math.sin(Math.toRadians(36.f))),
                enemyRadius + (float) (enemyRadius * Math.cos(Math.toRadians(36.f))));
        enemyPath.lineTo((float) (enemyRadius - enemyRadius * Math.cos(Math.toRadians(18.f))),
                enemyRadius - (float) (enemyRadius * Math.sin(Math.toRadians(18.f))));
        enemyPath.close();

        return makeEnemyShape(enemyPath, textureBitmap, enemyRadius, blurRadius);
    }

    public static Bitmap makeEnemyPAlert(float enemyRadius, int alertColor) {
        Path enemyPath = new Path();

        enemyPath.moveTo(enemyRadius, 0);
        enemyPath.lineTo((float) (enemyRadius + enemyRadius * Math.cos(Math.toRadians(18.f))),
                enemyRadius - (float) (enemyRadius * Math.sin(Math.toRadians(18.f))));
        enemyPath.lineTo((float) (enemyRadius + enemyRadius * Math.sin(Math.toRadians(36.f))),
                enemyRadius + (float) (enemyRadius * Math.cos(Math.toRadians(36.f))));
        enemyPath.lineTo((float) (enemyRadius - enemyRadius * Math.sin(Math.toRadians(36.f))),
                enemyRadius + (float) (enemyRadius * Math.cos(Math.toRadians(36.f))));
        enemyPath.lineTo((float) (enemyRadius - enemyRadius * Math.cos(Math.toRadians(18.f))),
                enemyRadius - (float) (enemyRadius * Math.sin(Math.toRadians(18.f))));
        enemyPath.close();

        return makeEnemyShapeAlert(enemyPath, enemyRadius, alertColor);
    }

    public static Bitmap makeEnemyD(Bitmap textureBitmap, float enemyRadius, float blurRadius) {
        Path enemyPath = new Path();
        // top
        enemyPath.moveTo(enemyRadius, 0);
        enemyPath.lineTo(enemyRadius * 2, enemyRadius);
        enemyPath.lineTo(enemyRadius, enemyRadius * 2);
        enemyPath.lineTo(0, enemyRadius);
        enemyPath.close();

        return makeEnemyShape(enemyPath, textureBitmap, enemyRadius, blurRadius);
    }

    public static Bitmap makeEnemyShape(Path path, Bitmap textureBitmap, float enemyRadius, float blurRadius) {
        Bitmap enemyBitmap = Bitmap.createBitmap((int) (enemyRadius * 2f), (int) (enemyRadius * 2f),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(enemyBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        int layer = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null,
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setARGB(255, 255, 255, 255);

        canvas.drawPath(path, paint);
        Paint bitmapPaint = new Paint();
        bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        int cropX = (int) Math.round(Math.random()*textureBitmap.getWidth()/2);
        int cropY = (int) Math.round(Math.random() * textureBitmap.getHeight() / 2);
        canvas.drawBitmap(textureBitmap, new Rect(cropX, cropY, (int) (cropX + enemyRadius * 2),
                        (int) (cropY + enemyRadius * 2)),
                new Rect(0, 0, (int) (enemyRadius * 2), (int) (enemyRadius * 2)), bitmapPaint);
        canvas.restoreToCount(layer);

        Bitmap shadow = makeShadow(path, enemyRadius * 2, enemyRadius * 2, blurRadius);

        return makeComposite(enemyBitmap, shadow, blurRadius);

    }

    public static Bitmap makeEnemyShapeAlert(Path path, float enemyRadius, int alertColor) {

        Bitmap enemyBitmap = Bitmap.createBitmap((int) (enemyRadius * 2f), (int) (enemyRadius * 2f),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(enemyBitmap);

        int layer = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null,
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setARGB(255, 255, 255, 255);

        canvas.drawPath(path, paint);
        Paint bitmapPaint = new Paint();
        bitmapPaint.setColor(alertColor);
        bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawRect(0, 0, enemyBitmap.getWidth(), enemyBitmap.getHeight(), bitmapPaint);
        canvas.restoreToCount(layer);

        return enemyBitmap;
    }

    public static Bitmap makeEnemyDAlert(float enemyRadius, int alertColor) {
        Path enemyPath = new Path();
        // top
        enemyPath.moveTo(enemyRadius, 0);
        enemyPath.lineTo(enemyRadius * 2, enemyRadius);
        enemyPath.lineTo(enemyRadius, enemyRadius * 2);
        enemyPath.lineTo(0, enemyRadius);
        enemyPath.close();

        return makeEnemyShapeAlert(enemyPath, enemyRadius, alertColor);
    }

    public static Bitmap createScrollingSectionBitmap(int mainColor, int shadowColor,
                                                      int sectionWidth, int screenHeight,
                                                      int screenWidth) {
        Bitmap background = Bitmap.createBitmap(sectionWidth, (int) (screenHeight * 1.75f),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(background);
        int blurRadius = (int) (screenWidth*.025f);

        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(mainColor);
        canvas.drawRect(blurRadius, 0, background.getWidth() - blurRadius,
                background.getHeight(), backgroundPaint);

        LinearGradient gradient = new LinearGradient(blurRadius, 0, 0, 0,
                new int[]{shadowColor, Color.TRANSPARENT},
                new float[]{0, 1.f}, Shader.TileMode.CLAMP);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setShader(gradient);
        canvas.drawRect(0, 0, blurRadius, background.getHeight(), paint);

        LinearGradient backGradient = new LinearGradient(background.getWidth()-blurRadius, 0,
                background.getWidth(), 0, new int[]{shadowColor, Color.TRANSPARENT},
                new float[]{0, 1.f}, Shader.TileMode.CLAMP);

        Paint backShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backShadowPaint.setShader(backGradient);


        canvas.drawRect(background.getWidth() - blurRadius, 0, background.getWidth(),
                background.getHeight(), backShadowPaint);
        return background;
    }

    public static Bitmap makeShadow(Path inputPath, float length, float height, float blurRadius) {
        Bitmap enemyBitmap = Bitmap.createBitmap((int) (length + blurRadius * 2),
                (int) (height + blurRadius * 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(enemyBitmap);

        Path shadowPath = inputPath;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(.96f, .96f);
        Matrix transMatrix = new Matrix();
        transMatrix.setTranslate(blurRadius, blurRadius);
        scaleMatrix.preConcat(transMatrix);
        shadowPath.transform(scaleMatrix);

        MaskFilter filter = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(0x88252525);
        paint.setMaskFilter(filter);

        canvas.drawPath(shadowPath, paint);
        return enemyBitmap;
    }

    public static Bitmap makeComposite(Bitmap bitmap, Bitmap shadowBitmap, float blurRadius) {

        Canvas canvas = new Canvas(shadowBitmap);
        canvas.drawBitmap(bitmap, blurRadius, 0, null);
        return shadowBitmap;
    }

    public static Bitmap makeBulletBitmap(int strokeWidth, int bulletLength, RectF bulletRect,
                                          float blurRadius) {

        Bitmap bulletBitmap = Bitmap.createBitmap(bulletLength, strokeWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bulletBitmap);
        Path bulletPath = new Path();
        bulletPath.moveTo(0, strokeWidth / 2);
        bulletPath.lineTo(bulletBitmap.getWidth(), strokeWidth / 2);
        bulletPath.close();

        Paint bulletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bulletPaint.setColor(Color.WHITE);
        bulletPaint.setStyle(Paint.Style.STROKE);

        bulletPaint.setStrokeWidth(strokeWidth);

        bulletPath.computeBounds(bulletRect, true);
        Bitmap bulletShadowBitmap = makeShadowForLine(bulletPath, bulletLength, strokeWidth, blurRadius);
        canvas.drawPath(bulletPath, bulletPaint);
        return makeComposite(bulletBitmap, bulletShadowBitmap, blurRadius);

    }

    public static Bitmap makeShadowForLine(Path inputPath, float length, float height, float blurRadius) {
        Bitmap enemyBitmap = Bitmap.createBitmap((int) (length + blurRadius * 2), (int) (height + blurRadius * 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(enemyBitmap);

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(1.2f, 1.2f);
        Path shadowPath = new Path(inputPath);
        shadowPath.transform(scaleMatrix);

        MaskFilter filter = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x88252525);
        paint.setMaskFilter(filter);

        canvas.drawPath(shadowPath, paint);
        return enemyBitmap;
    }

    public static Bitmap makeShieldBitmap(float enemyRadius, int strokeWidth, float blurRadius) {
        Bitmap shieldBitmap = Bitmap.createBitmap((int) (enemyRadius * 4), (int) (enemyRadius * 4), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shieldBitmap);

        Paint shieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shieldPaint.setColor(Color.WHITE);
        shieldPaint.setStyle(Paint.Style.STROKE);
        shieldPaint.setStrokeWidth(strokeWidth);
        Path path = new Path();
        path.addArc(new RectF(enemyRadius * 0.25f, enemyRadius * 0.25f, enemyRadius * 3.75f, enemyRadius * 3.75f), 140, 80);
        canvas.drawPath(path, shieldPaint);

        Bitmap shieldShadow = makeShadow(path, enemyRadius*4, enemyRadius*4, blurRadius);

        return makeComposite(shieldBitmap, shieldShadow, blurRadius);
    }

    public static Bitmap makeShipBitmap(Path shipPath, Bitmap shipTexture, float shipLength, float shipHeight) {
        Bitmap shipBitmap = Bitmap.createBitmap((int) shipLength, (int) shipHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(shipBitmap);

        int layer = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null,
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setARGB(255, 255, 255, 255);

        canvas.drawPath(shipPath, paint);

        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        int cropX = Math.round(shipTexture.getWidth()/4);
        int cropY = Math.round(shipTexture.getHeight()/4);
        canvas.drawBitmap(shipTexture, new Rect(cropX, cropY, (int) (cropX + shipLength), (int) (cropY + shipHeight)), new Rect(0, 0,
                (int) shipLength, (int) shipHeight), bitmapPaint);
        canvas.restoreToCount(layer);

        return shipBitmap;
    }



}
