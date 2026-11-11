package com.example.douanetemporelle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;
import java.util.Random;

/**
 * 1. عنصر عرض مخصص لخلفية سينمائية متحركة تتكيف تلقائياً مع صور ونمط حياة سكان سنة الوجهة
 * يدمج صورة سينمائية عالية الدقة تعبر عن مجتمع وسكان العصر مع:
 * - حركة كاميرا ديناميكية مستمرة (Ken Burns Zoom & Pan).
 * - مؤثرات بصرية تفاعلية حية بمعدل 60 إطار في الثانية (جزيئات، مركبات طائرة، نجوم القفزة الفضائية، بخار، مشاعل).
 * - تظليل سينمائي ناعم يضمن وضوح النصوص والبطاقات بنسبة 100%.
 */
public class AnimatedBackgroundView extends View {

    // 2. ثوابت العصور الزمنية وأنماط الحضارات
    public static final int MODE_REFUS_ARRESTATION = 0;
    public static final int MODE_ANTIQUE = 1;
    public static final int MODE_MEDIEVAL = 2;
    public static final int MODE_RENAISSANCE = 3;
    public static final int MODE_INDUSTRIEL = 4;
    public static final int MODE_GUERRE = 5;
    public static final int MODE_GUERRE_FROIDE = 6;
    public static final int MODE_CONTEMPORAIN = 7;
    public static final int MODE_CYBERNETIQUE = 8;
    public static final int MODE_INTERSTELLAIRE = 9;

    private int modeActuel = MODE_CYBERNETIQUE;

    // 3. إدارة الصورة السينمائية الحالية للحقبة الزمنية
    private Bitmap bitmapActuel = null;
    private final Matrix matriceCamera = new Matrix();
    private Paint paintBitmap;
    private Paint paintVignette;

    // 4. أدوات الرسم المهيأة مسبقاً لمنع أي استهلاك للذاكرة أثناء دورة الرسم 60fps
    private Paint paintFond;
    private Paint paintLigne;
    private Paint paintRemplissage;
    private Paint paintParticule;
    private Paint paintLueur;

    private final Path pathGenerique = new Path();
    private final RectF rectFGenerique = new RectF();

    // 5. محرك الحركة الزمني المستمر
    private ValueAnimator animateurBoucle;
    private float temps = 0f;
    private final Random random = new Random();

    // 6. مصفوفات الجزيئات الحركية العامة
    private static final int NOMBRE_PARTICULES = 38;
    private final float[] partX = new float[NOMBRE_PARTICULES];
    private final float[] partY = new float[NOMBRE_PARTICULES];
    private final float[] partVitesse = new float[NOMBRE_PARTICULES];
    private final float[] partRayon = new float[NOMBRE_PARTICULES];
    private final float[] partPhase = new float[NOMBRE_PARTICULES];

    // 7. مصفوفات المركبات الطائرة للعصر السيبراني (Flying Hovercars)
    private static final int NOMBRE_VEHICULES = 5;
    private final float[] vehiculeX = new float[NOMBRE_VEHICULES];
    private final float[] vehiculeYRatio = new float[NOMBRE_VEHICULES];
    private final float[] vehiculeVitesse = new float[NOMBRE_VEHICULES];
    private final int[] vehiculeSens = new int[NOMBRE_VEHICULES];

    // 8. مصفوفات حقل القفزة الفضائية للحضارة الكونية (Warp Starfield)
    private static final int NOMBRE_ETOILES_WARP = 45;
    private final float[] etoileAngle = new float[NOMBRE_ETOILES_WARP];
    private final float[] etoileDistRatio = new float[NOMBRE_ETOILES_WARP];
    private final float[] etoileVitesse = new float[NOMBRE_ETOILES_WARP];

    public AnimatedBackgroundView(Context context) {
        super(context);
        initialiserMoteurRendu();
    }

    public AnimatedBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialiserMoteurRendu();
    }

    public AnimatedBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiserMoteurRendu();
    }

    /**
     * 9. تهيئة أدوات الرسم ومحرك التحديث المستمر
     */
    private void initialiserMoteurRendu() {
        paintBitmap = new Paint(Paint.FILTER_BITMAP_FLAG);
        paintVignette = new Paint();
        paintVignette.setStyle(Paint.Style.FILL);

        paintFond = new Paint();
        paintFond.setStyle(Paint.Style.FILL);

        paintLigne = new Paint();
        paintLigne.setAntiAlias(true);
        paintLigne.setStyle(Paint.Style.STROKE);

        paintRemplissage = new Paint();
        paintRemplissage.setAntiAlias(true);
        paintRemplissage.setStyle(Paint.Style.FILL);

        paintParticule = new Paint();
        paintParticule.setAntiAlias(true);
        paintParticule.setStyle(Paint.Style.FILL);

        paintLueur = new Paint();
        paintLueur.setAntiAlias(true);
        paintLueur.setStyle(Paint.Style.FILL);

        // إنشاء محرك الحركة المستمر بمعدل 60 إطار في الثانية
        animateurBoucle = ValueAnimator.ofFloat(0f, 1f);
        animateurBoucle.setDuration(12000);
        animateurBoucle.setRepeatCount(ValueAnimator.INFINITE);
        animateurBoucle.setInterpolator(new LinearInterpolator());
        animateurBoucle.addUpdateListener(animation -> {
            temps = animation.getAnimatedFraction();
            invalidate();
        });

        // تحميل الصورة الافتراضية
        chargerBitmapPourMode(modeActuel);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bitmapActuel == null || bitmapActuel.isRecycled()) {
            chargerBitmapPourMode(modeActuel);
        }
        if (animateurBoucle != null && !animateurBoucle.isRunning()) {
            animateurBoucle.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animateurBoucle != null) {
            animateurBoucle.cancel();
        }
        if (bitmapActuel != null && !bitmapActuel.isRecycled()) {
            bitmapActuel.recycle();
            bitmapActuel = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) return;

        // تهيئة التظليل السينمائي المتدرج (Cinematic Vignette)
        LinearGradient gradVignette = new LinearGradient(0, 0, 0, h,
                new int[]{Color.parseColor("#90000000"), Color.parseColor("#35000000"), Color.parseColor("#B0000000")},
                new float[]{0f, 0.45f, 1f}, Shader.TileMode.CLAMP);
        paintVignette.setShader(gradVignette);

        // 10. تهيئة مواضع الجزيئات العامة العشوائية
        for (int i = 0; i < NOMBRE_PARTICULES; i++) {
            partX[i] = random.nextFloat() * w;
            partY[i] = random.nextFloat() * h;
            partVitesse[i] = 0.6f + random.nextFloat() * 1.8f;
            partRayon[i] = 1.5f + random.nextFloat() * 2.5f;
            partPhase[i] = random.nextFloat() * (float) Math.PI * 2f;
        }

        // 11. تهيئة مسارات المركبات الطائرة للعصر السيبراني
        for (int i = 0; i < NOMBRE_VEHICULES; i++) {
            vehiculeX[i] = random.nextFloat() * w;
            vehiculeYRatio[i] = 0.20f + (i * 0.14f);
            vehiculeVitesse[i] = 2.2f + random.nextFloat() * 3.5f;
            vehiculeSens[i] = (i % 2 == 0) ? 1 : -1;
        }

        // 12. تهيئة حقل القفزة الفضائية للحضارة الكونية
        for (int i = 0; i < NOMBRE_ETOILES_WARP; i++) {
            etoileAngle[i] = random.nextFloat() * 360f;
            etoileDistRatio[i] = random.nextFloat();
            etoileVitesse[i] = 0.003f + random.nextFloat() * 0.009f;
        }
    }

    /**
     * 13. ضبط وضع الخلفية بدقة تاريخية وفق نتيجة السفر وسنة الوجهة المدخلة
     *
     * @param approuve هل السفر مسموح أم مرفوض
     * @param annee    سنة الوجهة المحددة
     */
    public void configurerMode(boolean approuve, int annee) {
        int ancienMode = this.modeActuel;
        if (!approuve) {
            this.modeActuel = MODE_REFUS_ARRESTATION;
        } else {
            if (annee < 500) {
                this.modeActuel = MODE_ANTIQUE;
            } else if (annee < 1492) {
                this.modeActuel = MODE_MEDIEVAL;
            } else if (annee < 1789) {
                this.modeActuel = MODE_RENAISSANCE;
            } else if (annee < 1914) {
                this.modeActuel = MODE_INDUSTRIEL;
            } else if (annee <= 1945) {
                this.modeActuel = MODE_GUERRE;
            } else if (annee <= 1989) {
                this.modeActuel = MODE_GUERRE_FROIDE;
            } else if (annee <= 2026) {
                this.modeActuel = MODE_CONTEMPORAIN;
            } else if (annee <= 2150) {
                this.modeActuel = MODE_CYBERNETIQUE;
            } else {
                this.modeActuel = MODE_INTERSTELLAIRE;
            }
        }

        if (modeActuel != ancienMode || bitmapActuel == null || bitmapActuel.isRecycled()) {
            chargerBitmapPourMode(modeActuel);
        }
        invalidate();
    }

    /**
     * 14. تحميل صورة الحقبة السينمائية بصيغة RGB_565 الاقتصادية
     */
    private void chargerBitmapPourMode(int mode) {
        int resId;
        switch (mode) {
            case MODE_REFUS_ARRESTATION:
                resId = R.drawable.bg_era_arrestation;
                break;
            case MODE_ANTIQUE:
                resId = R.drawable.bg_era_antique;
                break;
            case MODE_MEDIEVAL:
                resId = R.drawable.bg_era_medieval;
                break;
            case MODE_RENAISSANCE:
            case MODE_INDUSTRIEL:
                resId = R.drawable.bg_era_industriel;
                break;
            case MODE_GUERRE:
                resId = R.drawable.bg_era_guerre;
                break;
            case MODE_GUERRE_FROIDE:
            case MODE_CONTEMPORAIN:
                resId = R.drawable.bg_era_contemporain;
                break;
            case MODE_INTERSTELLAIRE:
                resId = R.drawable.bg_era_interstellaire;
                break;
            case MODE_CYBERNETIQUE:
            default:
                resId = R.drawable.bg_era_cybernetique;
                break;
        }

        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap nouveau = BitmapFactory.decodeResource(getResources(), resId, opts);
            if (nouveau != null) {
                if (bitmapActuel != null && !bitmapActuel.isRecycled()) {
                    bitmapActuel.recycle();
                }
                bitmapActuel = nouveau;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // 15. رسم الصورة السينمائية للحقبة بحركة كاميرا متواصلة (Ken Burns Effect)
        if (bitmapActuel != null && !bitmapActuel.isRecycled()) {
            float bw = bitmapActuel.getWidth();
            float bh = bitmapActuel.getHeight();

            float baseScale = Math.max((float) w / bw, (float) h / bh);

            // تموج تكبير وتنقل بانورامي يحاكي تصوير سينمائي حي لشارع وسكان العصر
            float facteurRespiration = (float) (0.5 * (1.0 + Math.sin(temps * Math.PI * 2.0)));
            float zoomActuel = baseScale * (1.05f + (0.10f * facteurRespiration));

            float scaledW = bw * zoomActuel;
            float scaledH = bh * zoomActuel;

            float deltaX = (w - scaledW) * 0.5f + (float) (Math.sin(temps * Math.PI * 2.0) * (w * 0.035f));
            float deltaY = (h - scaledH) * 0.5f + (float) (Math.cos(temps * Math.PI * 2.0) * (h * 0.025f));

            matriceCamera.reset();
            matriceCamera.postScale(zoomActuel, zoomActuel);
            matriceCamera.postTranslate(deltaX, deltaY);

            canvas.drawBitmap(bitmapActuel, matriceCamera, paintBitmap);
        } else {
            paintFond.setColor(Color.parseColor("#0F172A"));
            canvas.drawRect(0, 0, w, h, paintFond);
        }

        // 16. تطبيق التظليل السينمائي لضمان وضوح النصوص والبطاقات
        canvas.drawRect(0, 0, w, h, paintVignette);

        // 17. رسم الطبقة الحركية التفاعلية الحية فوق صورة العصر
        switch (modeActuel) {
            case MODE_REFUS_ARRESTATION:
                dessinerCoucheArrestation(canvas, w, h);
                break;
            case MODE_ANTIQUE:
                dessinerCoucheAntique(canvas, w, h);
                break;
            case MODE_MEDIEVAL:
                dessinerCoucheMedieval(canvas, w, h);
                break;
            case MODE_RENAISSANCE:
            case MODE_INDUSTRIEL:
                dessinerCoucheIndustriel(canvas, w, h);
                break;
            case MODE_GUERRE:
                dessinerCoucheGuerre(canvas, w, h);
                break;
            case MODE_GUERRE_FROIDE:
            case MODE_CONTEMPORAIN:
                dessinerCoucheContemporain(canvas, w, h);
                break;
            case MODE_INTERSTELLAIRE:
                dessinerCoucheInterstellaire(canvas, w, h);
                break;
            case MODE_CYBERNETIQUE:
            default:
                dessinerCoucheCybernetique(canvas, w, h);
                break;
        }
    }

    /**
     * 18. رسم المؤثرات الحية لحالة التوقيف والحظر (كشافات أمنية دوارة وشبكة ليزر)
     */
    private void dessinerCoucheArrestation(Canvas canvas, int w, int h) {
        float cx = w * 0.5f;
        float cy = h * 0.28f;

        // كشاف أمني أول يدور بزاوية متغيرة
        float angle1 = temps * 360f * 1.5f;
        paintRemplissage.setColor(Color.parseColor("#28EF4444"));
        canvas.save();
        canvas.rotate(angle1, cx, cy);
        pathGenerique.reset();
        pathGenerique.moveTo(cx, cy);
        pathGenerique.lineTo(cx - 150f, cy + h);
        pathGenerique.lineTo(cx + 150f, cy + h);
        pathGenerique.close();
        canvas.drawPath(pathGenerique, paintRemplissage);
        canvas.restore();

        // كشاف أمني ثان يدور بالاتجاه المعاكس
        float angle2 = -temps * 360f * 1.2f + 50f;
        canvas.save();
        canvas.rotate(angle2, cx, cy);
        pathGenerique.reset();
        pathGenerique.moveTo(cx, cy);
        pathGenerique.lineTo(cx - 130f, cy + h);
        pathGenerique.lineTo(cx + 130f, cy + h);
        pathGenerique.close();
        canvas.drawPath(pathGenerique, paintRemplissage);
        canvas.restore();

        // حواجز ليزر أفقية متذبذبة
        paintLigne.setColor(Color.parseColor("#75EF4444"));
        paintLigne.setStrokeWidth(2f);
        for (int i = 1; i <= 4; i++) {
            float yLaser = (h * 0.18f) + (h * 0.16f * i) + (float) Math.sin((temps * Math.PI * 4) + i) * 12f;
            canvas.drawLine(0, yLaser, w, yLaser, paintLigne);
        }

        // جزيئات إنذار حمراء صاعدة بسرعة
        paintParticule.setColor(Color.parseColor("#90F87171"));
        dessinerParticulesFlottantes(canvas, w, h, -1.8f);
    }

    /**
     * 19. رسم المؤثرات الحية للعصور القديمة (أشعة شمسية دوارة وجزيئات ذهبية متطايرة)
     */
    private void dessinerCoucheAntique(Canvas canvas, int w, int h) {
        float cx = w * 0.5f;
        float cy = h * 0.22f;

        // أشعة شمس متوسطية دوارة خافتة
        canvas.save();
        canvas.rotate(temps * 360f * 0.2f, cx, cy);
        paintLigne.setColor(Color.parseColor("#18F59E0B"));
        paintLigne.setStrokeWidth(2.5f);
        for (int i = 0; i < 12; i++) {
            float rad = (float) Math.toRadians(i * (360f / 12f));
            float xRay = cx + (float) Math.cos(rad) * (w * 0.9f);
            float yRay = cy + (float) Math.sin(rad) * (w * 0.9f);
            canvas.drawLine(cx, cy, xRay, yRay, paintLigne);
        }
        canvas.restore();

        // جزيئات غبار ذهبي أثري تطفو بتموج هادئ
        paintParticule.setColor(Color.parseColor("#75F59E0B"));
        dessinerParticulesFlottantes(canvas, w, h, 0.45f);
    }

    /**
     * 20. رسم المؤثرات الحية للعصور الوسطى (وميض مشاعل وجمرات نارية صاعدة)
     */
    private void dessinerCoucheMedieval(Canvas canvas, int w, int h) {
        // وميض مشاعل قلاعية متوهج
        float flamme = (float) Math.sin(temps * Math.PI * 12) * 12f;
        paintLueur.setColor(Color.parseColor("#25F59E0B"));
        canvas.drawCircle(w * 0.5f, h * 0.35f, 130f + flamme, paintLueur);

        // جمرات وشرارات نار صاعدة في هواء القرية
        paintParticule.setColor(Color.parseColor("#80F59E0B"));
        dessinerParticulesFlottantes(canvas, w, h, -1.3f);
    }

    /**
     * 21. رسم المؤثرات الحية للعصر الصناعي (ترس ميكانيكي يدور وأبخرة مصانع)
     */
    private void dessinerCoucheIndustriel(Canvas canvas, int w, int h) {
        // ترس ميكانيكي نحاسي يدور في زاوية الشاشة
        float rot = temps * 360f * 0.7f;
        dessinerEngrenageSimple(canvas, w * 0.88f, h * 0.80f, 105f, 12, rot, Color.parseColor("#2EFB923C"));

        // جزيئات دخان وبخار رمادية ونحاسية متصاعدة
        paintParticule.setColor(Color.parseColor("#65FB923C"));
        dessinerParticulesFlottantes(canvas, w, h, -1.0f);
    }

    /**
     * 22. رسم ترس ميكانيكي بسيط للتفاعل البصري
     */
    private void dessinerEngrenageSimple(Canvas canvas, float cx, float cy, float rayon, int dents, float rotation, int couleur) {
        paintLigne.setColor(couleur);
        paintLigne.setStrokeWidth(2f);

        canvas.save();
        canvas.rotate(rotation, cx, cy);
        canvas.drawCircle(cx, cy, rayon, paintLigne);
        canvas.drawCircle(cx, cy, rayon * 0.35f, paintLigne);

        float anglePas = 360f / dents;
        for (int i = 0; i < dents; i++) {
            float rad = (float) Math.toRadians(i * anglePas);
            float x1 = cx + (float) Math.cos(rad) * (rayon - 6);
            float y1 = cy + (float) Math.sin(rad) * (rayon - 6);
            float x2 = cx + (float) Math.cos(rad) * (rayon + 14);
            float y2 = cy + (float) Math.sin(rad) * (rayon + 14);
            canvas.drawLine(x1, y1, x2, y2, paintLigne);
        }
        canvas.restore();
    }

    /**
     * 23. رسم المؤثرات الحية لحقبة الحروب العالمية وإعادة الإعمار (كشافات مضادة للطائرات ورماد متصاعد)
     */
    private void dessinerCoucheGuerre(Canvas canvas, int w, int h) {
        // كشاف دفاع جوي أول يمسح السماء الغائمة ببطء
        float cx1 = w * 0.35f;
        float cy1 = h * 0.88f;
        float angle1 = (float) Math.sin(temps * Math.PI * 2f) * 26f - 12f;

        paintRemplissage.setColor(Color.parseColor("#22FFFFFF"));
        canvas.save();
        canvas.rotate(angle1, cx1, cy1);
        pathGenerique.reset();
        pathGenerique.moveTo(cx1, cy1);
        pathGenerique.lineTo(cx1 - 50f, 0);
        pathGenerique.lineTo(cx1 + 50f, 0);
        pathGenerique.close();
        canvas.drawPath(pathGenerique, paintRemplissage);
        canvas.restore();

        // كشاف دفاع جوي ثان في الاتجاه المعاكس
        float cx2 = w * 0.75f;
        float cy2 = h * 0.90f;
        float angle2 = (float) Math.cos(temps * Math.PI * 2f) * 24f + 14f;

        canvas.save();
        canvas.rotate(angle2, cx2, cy2);
        pathGenerique.reset();
        pathGenerique.moveTo(cx2, cy2);
        pathGenerique.lineTo(cx2 - 45f, 0);
        pathGenerique.lineTo(cx2 + 45f, 0);
        pathGenerique.close();
        canvas.drawPath(pathGenerique, paintRemplissage);
        canvas.restore();

        // جزيئات رماد ودخان متصاعدة في هواء الحرب
        paintParticule.setColor(Color.parseColor("#75A1A1AA"));
        dessinerParticulesFlottantes(canvas, w, h, -1.1f);
    }

    /**
     * 24. رسم المؤثرات الحية للعصر المعاصر (موجات اتصالات راديوية وقمر صناعي مداري)
     */
    private void dessinerCoucheContemporain(Canvas canvas, int w, int h) {
        // قمر صناعي يمر مدارياً في السماء
        float satX = (temps * (w + 100f)) - 50f;
        float satY = h * 0.12f;
        paintRemplissage.setColor(Color.parseColor("#C038BDF8"));
        canvas.drawCircle(satX, satY, 4f, paintRemplissage);

        // جزيئات بيانات واتصالات عائمة
        paintParticule.setColor(Color.parseColor("#7538BDF8"));
        dessinerParticulesFlottantes(canvas, w, h, -1.0f);
    }

    /**
     * 25. رسم المؤثرات الحية للعصر السيبراني (سيارات طائرة بنفث نيون وشلالات كود رقمية)
     */
    private void dessinerCoucheCybernetique(Canvas canvas, int w, int h) {
        // سيارات طائرة ومركبات ليفيتاسيون مغناطيسية تتحرك في ممرات هوائية (Hovercars)
        for (int i = 0; i < NOMBRE_VEHICULES; i++) {
            float sens = vehiculeSens[i];
            vehiculeX[i] += sens * vehiculeVitesse[i];

            if (sens > 0 && vehiculeX[i] > w + 60f) {
                vehiculeX[i] = -60f;
            } else if (sens < 0 && vehiculeX[i] < -60f) {
                vehiculeX[i] = w + 60f;
            }

            float vY = h * vehiculeYRatio[i];
            float vX = vehiculeX[i];

            paintLigne.setStrokeWidth(3f);
            if (sens > 0) {
                paintLigne.setColor(Color.parseColor("#8000E5FF"));
                canvas.drawLine(vX, vY, vX - 50f, vY, paintLigne);
                paintRemplissage.setColor(Color.parseColor("#00E5FF"));
                canvas.drawCircle(vX, vY, 3.5f, paintRemplissage);
            } else {
                paintLigne.setColor(Color.parseColor("#80F43F5E"));
                canvas.drawLine(vX, vY, vX + 50f, vY, paintLigne);
                paintRemplissage.setColor(Color.parseColor("#F43F5E"));
                canvas.drawCircle(vX, vY, 3.5f, paintRemplissage);
            }
        }

        // جزيئات ذكاء اصطناعي خضراء متصاعدة
        paintParticule.setColor(Color.parseColor("#7534D399"));
        dessinerParticulesFlottantes(canvas, w, h, -1.5f);
    }

    /**
     * 26. رسم المؤثرات الحية للحضارة الكونية (حقل قفزة سرعة الضوء Warp Speed)
     */
    private void dessinerCoucheInterstellaire(Canvas canvas, int w, int h) {
        float cx = w * 0.5f;
        float cy = h * 0.35f;
        float rayonMax = (float) Math.hypot(w, h) * 0.65f;

        paintLigne.setStrokeWidth(1.8f);

        for (int i = 0; i < NOMBRE_ETOILES_WARP; i++) {
            etoileDistRatio[i] += etoileVitesse[i];
            if (etoileDistRatio[i] > 1.0f) {
                etoileDistRatio[i] = 0.05f;
                etoileAngle[i] = random.nextFloat() * 360f;
            }

            float rad = (float) Math.toRadians(etoileAngle[i]);
            float dist = etoileDistRatio[i] * rayonMax;
            float cos = (float) Math.cos(rad);
            float sin = (float) Math.sin(rad);

            float xFin = cx + cos * dist;
            float yFin = cy + sin * dist;

            float traineLongueur = 10f + (etoileDistRatio[i] * 35f);
            float xDebut = xFin - cos * traineLongueur;
            float yDebut = yFin - sin * traineLongueur;

            int alpha = (int) (etoileDistRatio[i] * 210);
            paintLigne.setColor(Color.parseColor("#C084FC"));
            paintLigne.setAlpha(alpha);
            canvas.drawLine(xDebut, yDebut, xFin, yFin, paintLigne);
        }

        // جزيئات سديم كوني عائمة
        paintParticule.setColor(Color.parseColor("#75E879F9"));
        dessinerParticulesFlottantes(canvas, w, h, 0.7f);
    }

    /**
     * 27. تحديث ورسم حركة الجزيئات العائمة التفاعلية في اتجاه عمودي وأفقي متموج
     */
    private void dessinerParticulesFlottantes(Canvas canvas, int w, int h, float vitesseY) {
        for (int i = 0; i < NOMBRE_PARTICULES; i++) {
            partY[i] += partVitesse[i] * vitesseY;

            float ondeX = (float) Math.sin((temps * Math.PI * 4f) + partPhase[i]) * 1.2f;
            float posX = partX[i] + ondeX;

            if (partY[i] < -10f) {
                partY[i] = h + 10f;
                partX[i] = random.nextFloat() * w;
            } else if (partY[i] > h + 10f) {
                partY[i] = -10f;
                partX[i] = random.nextFloat() * w;
            }

            canvas.drawCircle(posX, partY[i], partRayon[i], paintParticule);
        }
    }
}
