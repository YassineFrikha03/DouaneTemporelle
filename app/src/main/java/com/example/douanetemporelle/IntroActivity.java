package com.example.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * 1. شاشة البداية والمقدمة الرسمية للتطبيق (Intro / Splash Activity)
 * تعرض الشعار الرسمي وحركات الظهور التدريجي مع تحديث مراحل التحميل الزمني
 * الكود معلق باللغة العربية مع الأرقام، وتطابق المعرفات الأحرف الأولى للطالب (_yf)
 */
public class IntroActivity extends AppCompatActivity {

    // 2. التصريح عن عناصر الواجهة مع بادئة الأحرف الأولى للطالب (_yf)
    private ImageView ivLogoIntroYf;
    private TextView tvTitreIntroYf, tvSousTitreIntroYf, tvStatusChargementYf;
    private ProgressBar pbChargementYf;
    private MaterialButton btnEntrerDouaneYf;

    // 3. إدارة مؤقتات التحميل والتنقل
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnableEtape1, runnableEtape2, runnableFinal;
    private boolean transitionEffectuee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 4. ربط العناصر البرمجية بمكونات الواجهة الرسومية المنتهية بـ _yf
        initialiserVues();

        // 5. تفعيل حركة ظهور ناعمة للشعار (Fade-in & Scale Animation)
        animerLogo();

        // 6. تشغيل محاكاة مراحل التحميل الزمني
        lancerProgressionTemporelle();

        // 7. إتاحة زر الدخول الفوري عند النقر لتخطي الانتظار
        btnEntrerDouaneYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirPosteControle();
            }
        });
    }

    /**
     * 8. دالة تهيئة وربط عناصر الواجهة
     */
    private void initialiserVues() {
        ivLogoIntroYf = findViewById(R.id.iv_logo_intro_yf);
        tvTitreIntroYf = findViewById(R.id.tv_titre_intro_yf);
        tvSousTitreIntroYf = findViewById(R.id.tv_sous_titre_intro_yf);
        tvStatusChargementYf = findViewById(R.id.tv_status_chargement_intro_yf);
        pbChargementYf = findViewById(R.id.pb_chargement_intro_yf);
        btnEntrerDouaneYf = findViewById(R.id.btn_entrer_douane_yf);
    }

    /**
     * 9. حركة ناعمة للشعار لجعل المقدمة جذابة واحترافية
     */
    private void animerLogo() {
        if (ivLogoIntroYf != null) {
            ivLogoIntroYf.setAlpha(0.0f);
            ivLogoIntroYf.setScaleX(0.85f);
            ivLogoIntroYf.setScaleY(0.85f);

            ivLogoIntroYf.animate()
                    .alpha(1.0f)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(900)
                    .start();
        }
    }

    /**
     * 10. محاكاة مراحل الاتصال الزمني وتحديث النص تلقائياً
     */
    private void lancerProgressionTemporelle() {
        // المرحلة الأولى : فحص الشبكة الزمنية (بعد 800 ميلي ثانية)
        runnableEtape1 = new Runnable() {
            @Override
            public void run() {
                if (!transitionEffectuee && tvStatusChargementYf != null) {
                    tvStatusChargementYf.setText("Connexion au reseau chronologique...");
                }
            }
        };
        handler.postDelayed(runnableEtape1, 800);

        // المرحلة الثانية : جاهزية النظام (بعد 1600 ميلي ثانية)
        runnableEtape2 = new Runnable() {
            @Override
            public void run() {
                if (!transitionEffectuee && tvStatusChargementYf != null) {
                    tvStatusChargementYf.setText("Poste de controle pret.");
                }
            }
        };
        handler.postDelayed(runnableEtape2, 1600);

        // المرحلة النهائية : الانتقال التلقائي للنشاط الأول (بعد 2400 ميلي ثانية)
        runnableFinal = new Runnable() {
            @Override
            public void run() {
                ouvrirPosteControle();
            }
        };
        handler.postDelayed(runnableFinal, 2400);
    }

    /**
     * 11. دالة الانتقال إلى النشاط الرئيسي (MainActivity) وإغلاق شاشة المقدمة
     */
    private synchronized void ouvrirPosteControle() {
        if (!transitionEffectuee) {
            transitionEffectuee = true;
            annulerTousLesCallbacks();

            Intent intentMain = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intentMain);
            // تأثير انتقال سلس بين النشاطين
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    /**
     * 12. إلغاء المؤقتات عند الخروج لتفادي أي تسريب في الذاكرة
     */
    private void annulerTousLesCallbacks() {
        if (handler != null) {
            if (runnableEtape1 != null) handler.removeCallbacks(runnableEtape1);
            if (runnableEtape2 != null) handler.removeCallbacks(runnableEtape2);
            if (runnableFinal != null) handler.removeCallbacks(runnableFinal);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        annulerTousLesCallbacks();
    }
}
