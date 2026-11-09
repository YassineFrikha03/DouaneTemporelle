package com.example.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 1. شاشة البداية الرسمية للتطبيق (Splash / Intro Activity)
 * تصميم مؤسساتي بسيط وراقٍ يحتوي حصرياً على الشعار الرسمي وشريط التحديث المركزي
 * الكود معلق باللغة العربية مع الأرقام، وتطابق المعرفات الأحرف الأولى للطالب (_yf)
 */
public class IntroActivity extends AppCompatActivity {

    // 2. التصريح عن عناصر الواجهة المعتمدة حصرياً (الشعار وشريط التحديث) المنتهية بـ _yf
    private ImageView ivLogoIntroYf;
    private ProgressBar pbLigneChargementYf;

    // 3. إدارة مؤقت الانتقال التلقائي للنشاط الرئيسي
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnableTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 4. ربط العناصر البرمجية بمكونات الواجهة الرسومية المنتهية بـ _yf
        ivLogoIntroYf = findViewById(R.id.iv_logo_intro_yf);
        pbLigneChargementYf = findViewById(R.id.pb_ligne_chargement_yf);

        // 5. تفعيل حركة ظهور تدريجية وناعمة للشعار الرسمي
        animerLogoOfficiel();

        // 6. برمجة الانتقال التلقائي إلى النشاط الرئيسي بعد اكتمال التحميل المؤسساتي (2 ثوانٍ)
        runnableTransition = new Runnable() {
            @Override
            public void run() {
                ouvrirApplicationPrincipale();
            }
        };
        handler.postDelayed(runnableTransition, 2000);
    }

    /**
     * 7. حركة ظهور سلسة للشعار تليق بالتطبيقات الحكومية والخدمية الكبرى
     */
    private void animerLogoOfficiel() {
        if (ivLogoIntroYf != null) {
            ivLogoIntroYf.setAlpha(0.0f);
            ivLogoIntroYf.setScaleX(0.92f);
            ivLogoIntroYf.setScaleY(0.92f);

            ivLogoIntroYf.animate()
                    .alpha(1.0f)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(800)
                    .start();
        }
    }

    /**
     * 8. دالة الانتقال إلى النشاط الأول (MainActivity) مع تأثير تلاشٍ ناعم
     */
    private void ouvrirApplicationPrincipale() {
        Intent intentMain = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intentMain);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); // إغلاق شاشة البداية نهائياً من المكدس
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 9. تنظيف المؤقتات لمنع أي تسريب في الذاكرة
        if (handler != null && runnableTransition != null) {
            handler.removeCallbacks(runnableTransition);
        }
    }
}
