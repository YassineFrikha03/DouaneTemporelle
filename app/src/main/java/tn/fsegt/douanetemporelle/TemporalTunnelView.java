package tn.fsegt.douanetemporelle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import androidx.annotation.Nullable;

/**
 * مكوّن مخصص لرسم النفق الزمني الحركي (دوامة الساعات) - نظام كرونوس
 * المرحلة 11: انتقال حركي سينمائي أحادي اللون (أبيض وأسود) بالاعتماد على لوحة الرسم Canvas
 */
public class TemporalTunnelView extends View {

    /**
     * الأرقام الرومانية المعتمدة على محيط كل ساعة
     */
    private static final String[] CHIFFRES_ROMAINS = {"XII", "III", "VI", "IX"};

    /**
     * واجهة استدعاء لإعلام النشاط بانتهاء حركة النفق الزمني
     */
    public interface OnTunnelFinishedListener {
        void onTunnelFinished();
    }

    private OnTunnelFinishedListener finishedListener;

    // أدوات الرسم المعاد استخدامها لمنع تخصيص الذاكرة داخل دالة onDraw
    private final Paint bgPaint = new Paint();
    private final Paint spiralPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint spiralGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint clockBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint clockRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint clockHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fadePaint = new Paint();

    private final Path spiralPath = new Path();
    private final RectF clockRect = new RectF();

    /**
     * هيكل بيانات محطة الساعة لتفادي أي تخصيص كائنات متكرر
     */
    private static class ClockStation {
        float angleOffset;      // زاوية التموضع على اللولب
        float depthOffset;      // إزاحة العمق الابتدائية (0 إلى 1)
        float baseRadiusRatio;  // نسبة الحجم الأساسي للساعة
        float handSpeed;        // سرعة دوران العقارب
    }

    private static final int NB_CLOCKS = 8;
    private final ClockStation[] clocks = new ClockStation[NB_CLOCKS];

    private ValueAnimator tunnelAnimator;
    private float animationProgress = 0f;

    public TemporalTunnelView(Context context) {
        super(context);
        init();
    }

    public TemporalTunnelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TemporalTunnelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * إعداد خصائص أدوات الرسم ومصفوفة الساعات
     */
    private void init() {
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);

        spiralPaint.setColor(Color.WHITE);
        spiralPaint.setStyle(Paint.Style.STROKE);
        spiralPaint.setStrokeWidth(2.5f);

        spiralGlowPaint.setColor(Color.WHITE);
        spiralGlowPaint.setStyle(Paint.Style.STROKE);
        spiralGlowPaint.setStrokeWidth(6.0f);
        spiralGlowPaint.setAlpha(45); // توهج ناعم شفاف

        clockBodyPaint.setColor(Color.BLACK);
        clockBodyPaint.setStyle(Paint.Style.FILL);

        clockRingPaint.setColor(Color.WHITE);
        clockRingPaint.setStyle(Paint.Style.STROKE);
        clockRingPaint.setStrokeWidth(2.0f);

        clockHandPaint.setColor(Color.WHITE);
        clockHandPaint.setStyle(Paint.Style.STROKE);
        clockHandPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));

        centerCorePaint.setColor(Color.WHITE);
        centerCorePaint.setStyle(Paint.Style.FILL);

        fadePaint.setColor(Color.BLACK);
        fadePaint.setStyle(Paint.Style.FILL);

        // توزيع 8 ساعات على مسار اللولب الزمني بسرعات وزوايا متناسقة
        for (int i = 0; i < NB_CLOCKS; i++) {
            ClockStation cs = new ClockStation();
            cs.angleOffset = (float) (i * (Math.PI * 2.0 / 3.2));
            cs.depthOffset = (float) i / (float) NB_CLOCKS;
            cs.baseRadiusRatio = 0.12f + (i % 3) * 0.03f;
            cs.handSpeed = 4f + (i % 4) * 2f;
            clocks[i] = cs;
        }
    }

    /**
     * تعيين مستمع انتهاء الحركة
     */
    public void setOnTunnelFinishedListener(OnTunnelFinishedListener listener) {
        this.finishedListener = listener;
    }

    /**
     * تشغيل محرك الحركة للدوامة الزمنية لمدة 2800 ميلي ثانية
     */
    public void startAnimation() {
        if (!ValueAnimator.areAnimatorsEnabled()) {
            if (finishedListener != null) {
                finishedListener.onTunnelFinished();
            }
            return;
        }

        stopAnimation();

        tunnelAnimator = ValueAnimator.ofFloat(0f, 1f);
        tunnelAnimator.setDuration(2800L);
        tunnelAnimator.setInterpolator(new AccelerateInterpolator(1.25f));
        tunnelAnimator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });

        tunnelAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (finishedListener != null) {
                    finishedListener.onTunnelFinished();
                }
            }
        });

        tunnelAnimator.start();
    }

    /**
     * إيقاف المحرك وإلغاؤه النظيف لتفادي تسريب الموارد
     */
    public void stopAnimation() {
        if (tunnelAnimator != null) {
            tunnelAnimator.cancel();
            tunnelAnimator = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        float cx = width / 2f;
        float cy = height / 2f;
        float maxRadius = (float) Math.hypot(cx, cy);

        // 1. رسم خلفية سوداء مطلقة
        canvas.drawRect(0, 0, width, height, bgPaint);

        // حساب زاوية الدوران الكلية للنفق
        float globalRotation = animationProgress * 540f; // دورة ونصف مع تسارع

        // 2. رسم خيوط اللولب المضيئة (حلزون أرخميدس في العمق)
        drawSpiralArms(canvas, cx, cy, maxRadius, globalRotation);

        // 3. رسم الساعات المتحركة في عمق النفق
        drawClockVortex(canvas, cx, cy, maxRadius, globalRotation);

        // 4. نواة المركز المضيئة (ثقب العبور الزمني)
        drawCenterCore(canvas, cx, cy);

        // 5. تأثير التلاشي النهائي نحو السواد التام في الـ 10% الأخيرة من الحركة
        if (animationProgress > 0.88f) {
            float fadeAlpha = (animationProgress - 0.88f) / 0.12f;
            if (fadeAlpha > 1f) fadeAlpha = 1f;
            fadePaint.setAlpha((int) (fadeAlpha * 255));
            canvas.drawRect(0, 0, width, height, fadePaint);
        }
    }

    /**
     * رسم الأذرع اللولبية البيضاء الملتفة نحو المركز
     */
    private void drawSpiralArms(Canvas canvas, float cx, float cy, float maxRadius, float globalRotation) {
        // رسم 3 أذرع متناظرة للدوامة
        for (int arm = 0; arm < 3; arm++) {
            float armAngleOffset = (float) (arm * (2.0 * Math.PI / 3.0));
            spiralPath.reset();

            int steps = 60;
            for (int s = 0; s <= steps; s++) {
                float t = (float) s / (float) steps;
                float r = maxRadius * (1f - (float) Math.pow(t, 0.7));
                float theta = (float) (armAngleOffset + (globalRotation * Math.PI / 180.0) + (t * Math.PI * 4.0));

                float x = cx + (float) (r * Math.cos(theta));
                float y = cy + (float) (r * Math.sin(theta));

                if (s == 0) {
                    spiralPath.moveTo(x, y);
                } else {
                    spiralPath.lineTo(x, y);
                }
            }

            // رسم التوهج ثم الخط الرئيسي
            canvas.drawPath(spiralPath, spiralGlowPaint);
            canvas.drawPath(spiralPath, spiralPaint);
        }
    }

    /**
     * رسم الساعات مع الأرقام الرومانية والعقارب الدوارة بتأثير البعد والعمق
     */
    private void drawClockVortex(Canvas canvas, float cx, float cy, float maxRadius, float globalRotation) {
        for (int i = 0; i < NB_CLOCKS; i++) {
            ClockStation cs = clocks[i];

            // حساب التقدم النسبي للساعة نحو المركز مع إعادة التدوير الحركي
            float clockProgress = (cs.depthOffset + animationProgress * 1.35f) % 1.0f;

            // مسافة الساعة من المركز (تقترب من المركز كلما تقدمت)
            float distance = maxRadius * (1f - clockProgress);
            float angle = (float) (cs.angleOffset + (globalRotation * Math.PI / 180.0) + (clockProgress * Math.PI * 2.5));

            float clockX = cx + (float) (distance * Math.cos(angle));
            float clockY = cy + (float) (distance * Math.sin(angle));

            // حجم الساعة يتضاءل نحو العمق ليعطي منظور ثلاثي الأبعاد مقنع
            float clockRadius = maxRadius * cs.baseRadiusRatio * (0.35f + 0.65f * (1f - clockProgress));
            if (clockRadius < 8f) {
                continue;
            }

            // شفافية الساعة (أكثر وضوحاً بالقرب، وخافتة في العمق)
            int alpha = (int) (255 * (0.25f + 0.75f * (1f - clockProgress)));
            if (alpha > 255) alpha = 255;
            if (alpha < 0) alpha = 0;

            // أ. قرص الساعة الداخلي الأسود الشفاف
            clockBodyPaint.setAlpha((int) (alpha * 0.85f));
            canvas.drawCircle(clockX, clockY, clockRadius, clockBodyPaint);

            // ب. الإطار الدائري الخارجي الأبيض
            clockRingPaint.setAlpha(alpha);
            clockRingPaint.setStrokeWidth(Math.max(1.5f, clockRadius * 0.04f));
            canvas.drawCircle(clockX, clockY, clockRadius, clockRingPaint);

            // ج. إطار داخلي رقيق
            float innerRadius = clockRadius * 0.82f;
            clockRingPaint.setAlpha((int) (alpha * 0.5f));
            canvas.drawCircle(clockX, clockY, innerRadius, clockRingPaint);

            // د. الأرقام الرومانية (XII, III, VI, IX)
            if (clockRadius > 22f) {
                float textSize = Math.max(9f, clockRadius * 0.22f);
                textPaint.setTextSize(textSize);
                textPaint.setAlpha(alpha);

                float numDist = clockRadius * 0.62f;
                // XII (أعلى)
                canvas.drawText(CHIFFRES_ROMAINS[0], clockX, clockY - numDist + (textSize * 0.35f), textPaint);
                // III (يمين)
                canvas.drawText(CHIFFRES_ROMAINS[1], clockX + numDist, clockY + (textSize * 0.35f), textPaint);
                // VI (أسفل)
                canvas.drawText(CHIFFRES_ROMAINS[2], clockX, clockY + numDist + (textSize * 0.35f), textPaint);
                // IX (يسار)
                canvas.drawText(CHIFFRES_ROMAINS[3], clockX - numDist, clockY + (textSize * 0.35f), textPaint);
            }

            // هـ. عقارب الساعة الدوارة بسرعة لعكس الزمن
            float handAngle = (float) (animationProgress * 360f * cs.handSpeed + i * 45f);
            float radHand = (float) Math.toRadians(handAngle);

            // عقرب الدقائق (أطول)
            clockHandPaint.setAlpha(alpha);
            clockHandPaint.setStrokeWidth(Math.max(1.5f, clockRadius * 0.045f));
            float minLength = clockRadius * 0.55f;
            canvas.drawLine(clockX, clockY,
                    clockX + (float) (minLength * Math.sin(radHand)),
                    clockY - (float) (minLength * Math.cos(radHand)),
                    clockHandPaint);

            // عقرب الساعات (أقصر)
            float hourLength = clockRadius * 0.38f;
            float radHour = radHand * 0.5f;
            clockHandPaint.setStrokeWidth(Math.max(2.0f, clockRadius * 0.06f));
            canvas.drawLine(clockX, clockY,
                    clockX + (float) (hourLength * Math.sin(radHour)),
                    clockY - (float) (hourLength * Math.cos(radHour)),
                    clockHandPaint);

            // و. نقطة المحور المركزية
            centerCorePaint.setAlpha(alpha);
            canvas.drawCircle(clockX, clockY, Math.max(2f, clockRadius * 0.06f), centerCorePaint);
        }
    }

    /**
     * رسم بؤرة المركز المضيئة للدوامة الزمنية
     */
    private void drawCenterCore(Canvas canvas, float cx, float cy) {
        float coreRadius = 14f + (float) Math.sin(animationProgress * Math.PI) * 10f;
        int coreAlpha = 180 + (int) (75f * (float) Math.sin(animationProgress * Math.PI));
        if (coreAlpha > 255) coreAlpha = 255;

        centerCorePaint.setColor(Color.WHITE);
        centerCorePaint.setAlpha(coreAlpha);
        canvas.drawCircle(cx, cy, coreRadius, centerCorePaint);

        // هالة محيطة خافتة
        spiralGlowPaint.setAlpha(80);
        canvas.drawCircle(cx, cy, coreRadius * 2.2f, spiralGlowPaint);
    }
}
