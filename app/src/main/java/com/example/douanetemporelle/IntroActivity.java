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
 * 1. شاشة البداية والتعريف بالتطبيق (Intro / Splash Activity)
 * تعرض شعار Douane Temporelle ونظام التحميل الزمني قبل الانتقال لشاشة التسجيل
 * الكود معلق باللغة العربية مع الأرقام، وتطابق المعرفات الأحرف الأولى للطالب (_yf)
 */
public class IntroActivity extends AppCompatActivity {

    // 2. التصريح عن عناصر الواجهة مع بادئة الأحرف الأولى للطالب (_yf)
    private ImageView ivLogoIntroYf;
    private TextView tvTitreIntroYf, tvSousTitreIntroYf, tvStatusChargementYf;
    private ProgressBar pbChargementYf;
    private MaterialButton btnEntrerDouaneYf;

    // 3. مؤقت الانتقال التلقائي (2.5 ثوانٍ)
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnableTransition;
    private boolean transitionEffectuee = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 4. ربط العناصر البرمجية بمكونات الواجهة الرسومية المنتهية بـ _yf
        initialiserVues();

        // 5. إعداد الانتقال التلقائي بعد انتهاء تحميل البروتوكولات الزمنية
        runnableTransition = new Runnable() {
            @Override
            public void run() {
                ouvrirPosteControle();
            }
        };
        handler.postDelayed(runnableTransition, 2500);

        // 6. إتاحة زر الدخول الفوري عند النقر لتخطي الانتظار (تحسين تجربة المستخدم UI/UX)
        btnEntrerDouaneYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirPosteControle();
            }
        });
    }

    /**
     * 7. دالة تهيئة وربط عناصر الواجهة
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
     * 8. دالة الانتقال إلى النشاط الرئيسي (MainActivity) وإغلاق شاشة المقدمة
     */
    private synchronized void ouvrirPosteControle() {
        if (!transitionEffectuee) {
            transitionEffectuee = true;
            // إلغاء المؤقت إن وجد لمنع التكرار
            if (handler != null && runnableTransition != null) {
                handler.removeCallbacks(runnableTransition);
            }
            Intent intentMain = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intentMain);
            finish(); // إغلاق شاشة البداية حتى لا يعود إليها المستخدم بزر الرجوع
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnableTransition != null) {
            handler.removeCallbacks(runnableTransition);
        }
    }
}

