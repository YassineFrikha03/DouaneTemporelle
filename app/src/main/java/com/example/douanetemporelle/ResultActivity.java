package com.example.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

/**
 * 1. النشاط الثالث : شاشة الحكم النهائي (ResultActivity)
 * تعرض النتيجة النهائية لطلب السفر عبر الزمن.
 * يتغير لون الخلفية تلقائياً بحسب القرار:
 * - اللون الأخضر في حال الموافقة (Bon voyage [Nom])
 * - اللون الأحمر في حال الرفض ([Nom] est en état d'arrestation temporelle)
 * زر "مسافر جديد" يعيد المستخدم إلى MainActivity مع إغلاق كافة الأنشطة السابقة.
 */
public class ResultActivity extends AppCompatActivity {

    // 2. آخر 4 أرقام من بطاقة الطالب لاسترجاع المفاتيح ديناميكياً
    private static final String MATRICULE_ETUDIANT = "1234";

    // 3. تصريح عناصر واجهة المستخدم (تنتهي جميعها بـ _yf)
    private LinearLayout llRacineResultatYf;
    private ImageView ivIconeVerdictYf;
    private TextView tvBadgeDouaneYf;
    private TextView tvTitreVerdictYf;
    private LinearLayout llCarteVerdictYf;
    private TextView tvMessagePersonnaliseYf;
    private TextView tvDetailsVerdictYf;
    private MaterialButton btnNouveauVoyageurYf;

    // 4. متغيرات البيانات المستلمة
    private String nomVoyageur;
    private boolean verdictApprouve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 5. ربط النشاط بملف التصميم activity_result.xml
        setContentView(R.layout.activity_result);

        // 6. ربط عناصر واجهة المستخدم بالمعرفات البرمجية
        initialiserVues();

        // 7. استخراج البيانات المنقولة عبر Intent من النشاط الثاني
        recupererDonneesIntent();

        // 8. تطبيق القرار: تغيير لون الخلفية والأيقونة والرسالة المخصصة
        appliquerVerdict();

        // 9. برمجة زر العودة لتنظيف المكدس والرجوع للنشاط الأول
        configurerBoutonRetour();
    }

    /**
     * 10. ربط عناصر الواجهة بالمعرفات الخاصة بالطالب
     */
    private void initialiserVues() {
        llRacineResultatYf = findViewById(R.id.ll_racine_resultat_yf);
        ivIconeVerdictYf = findViewById(R.id.iv_icone_verdict_yf);
        tvBadgeDouaneYf = findViewById(R.id.tv_badge_douane_yf);
        tvTitreVerdictYf = findViewById(R.id.tv_titre_verdict_yf);
        llCarteVerdictYf = findViewById(R.id.ll_carte_verdict_yf);
        tvMessagePersonnaliseYf = findViewById(R.id.tv_message_personnalise_yf);
        tvDetailsVerdictYf = findViewById(R.id.tv_details_verdict_yf);
        btnNouveauVoyageurYf = findViewById(R.id.btn_nouveau_voyageur_yf);
    }

    /**
     * 11. استخراج البيانات من Intent باستخدام المفاتيح المولدة ديناميكياً
     */
    private void recupererDonneesIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            nomVoyageur = intent.getStringExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT));
            verdictApprouve = intent.getBooleanExtra(IntentKeys.getVerdictKey(MATRICULE_ETUDIANT), false);
        }

        if (nomVoyageur == null || nomVoyageur.trim().isEmpty()) {
            nomVoyageur = "Voyageur";
        }
    }

    /**
     * 12. تطبيق الحكم: تغيير خلفية الشاشة والرسالة المخصصة حسب القيد المطلوب في كراس الشروط
     */
    private void appliquerVerdict() {
        if (verdictApprouve) {
            // حالة القبول: خلفية خضراء متدرجة ورسالة سفر سعيدة
            llRacineResultatYf.setBackgroundResource(R.drawable.bg_verdict_approved);
            ivIconeVerdictYf.setImageResource(R.drawable.ic_verdict_approved);
            tvTitreVerdictYf.setText(R.string.titre_verdict_approuve);
            tvTitreVerdictYf.setTextColor(ContextCompat.getColor(this, R.color.time_green));
            llCarteVerdictYf.setBackgroundResource(R.drawable.bg_card_verdict_approved);

            String message = getString(R.string.message_bon_voyage, nomVoyageur);
            tvMessagePersonnaliseYf.setText(message);
            tvDetailsVerdictYf.setText(R.string.details_verdict_approuve);
        } else {
            // حالة الرفض: خلفية حمراء قرمزية متدرجة لحالة الإنذار والتوقيف الزمني
            llRacineResultatYf.setBackgroundResource(R.drawable.bg_verdict_refused);
            ivIconeVerdictYf.setImageResource(R.drawable.ic_verdict_arrest);
            tvTitreVerdictYf.setText(R.string.titre_verdict_refuse);
            tvTitreVerdictYf.setTextColor(ContextCompat.getColor(this, R.color.time_red));
            llCarteVerdictYf.setBackgroundResource(R.drawable.bg_card_verdict_refused);

            String message = getString(R.string.message_arrestation, nomVoyageur);
            tvMessagePersonnaliseYf.setText(message);
            tvDetailsVerdictYf.setText(R.string.details_verdict_refuse);
        }
    }

    /**
     * 13. إعداد زر "مسافر جديد" للرجوع إلى MainActivity مع إغلاق الأنشطة السابقة
     */
    private void configurerBoutonRetour() {
        btnNouveauVoyageurYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // إنشاء نية صريحة للرجوع إلى MainActivity
                Intent intentRetour = new Intent(ResultActivity.this, MainActivity.class);

                // تطبيق الرايات لإغلاق جميع الأنشطة السابقة (VerificationActivity و ResultActivity)
                intentRetour.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intentRetour);

                // إغلاق النشاط الحالي
                finish();
            }
        });
    }
}
