package com.example.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * 1. النشاط الثاني : فاحص المفارقات والتحقق من التذكرة/الجواز (VerificationActivity)
 * يطبق شرط بنية الواجهة الإجبارية (LinearLayout أفقي مع وزن 0.3 و 0.7)
 * ويفحص شرط العدد الأولي ومحاولة تعديل الماضي لتعطيل زر الموافقة
 * الكود معلق باللغة العربية مع الأرقام، وتطابق المعرفات الأحرف الأولى للطالب (_yf)
 */
public class VerificationActivity extends AppCompatActivity {

    // 2. التصريح عن عناصر الواجهة مع بادئة الأحرف الأولى للطالب (_yf)
    private ImageView ivHologrammePasseportYf;
    private TextView tvValeurNomYf, tvValeurAgeYf, tvValeurCoordonneesYf, tvValeurMotifYf, tvAlerteParadoxeYf;
    private MaterialButton btnApprouverYf, btnRefuserYf;

    // 3. رقم بطاقة الطالب المستخدم لتوليد مفاتيح Intent المشتقة ديناميكياً
    private static final String MATRICULE_ETUDIANT = "1234";

    // متغيرات حفظ البيانات المستلمة
    private String nomVoyageur = "";
    private int anneeNaissance = 1990;
    private int anneeDestination = 2050;
    private String motifVoyage = "Tourisme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // 4. ربط العناصر البرمجية بمكونات الواجهة الرسومية المنتهية بـ _yf
        initialiserVues();

        // 5. استرجاع البيانات المرسلة من النشاط الأول عبر الـ Intent باستعمال المفاتيح الديناميكية
        recupererDonneesIntent();

        // 6. عرض البيانات المحسوبة على شكل بطاقة سفر / جواز سفر زمني
        afficherDetailsVoyage();

        // 7. تطبيق قيد المشروع: فحص شرط (Modification du passé + فارق زمني أولي) لتعطيل زر الموافقة فور التحميل
        verifierConditionParadoxe();

        // 8. برمجة حدث النقر على زر "الموافقة" (Approuver)
        btnApprouverYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoyerVerdict(true); // true = تمت الموافقة
            }
        });

        // 9. برمجة حدث النقر على زر "الرفض" (Refuser)
        btnRefuserYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoyerVerdict(false); // false = مرفوض لوجود خطر
            }
        });
    }

    /**
     * 10. دالة لربط عناصر الواجهة الرسومية
     */
    private void initialiserVues() {
        ivHologrammePasseportYf = findViewById(R.id.iv_hologramme_passeport_yf);
        tvValeurNomYf = findViewById(R.id.tv_valeur_nom_yf);
        tvValeurAgeYf = findViewById(R.id.tv_valeur_age_yf);
        tvValeurCoordonneesYf = findViewById(R.id.tv_valeur_coordonnees_yf);
        tvValeurMotifYf = findViewById(R.id.tv_valeur_motif_yf);
        tvAlerteParadoxeYf = findViewById(R.id.tv_alerte_paradoxe_yf);

        btnApprouverYf = findViewById(R.id.btn_approuver_yf);
        btnRefuserYf = findViewById(R.id.btn_refuser_yf);
    }

    /**
     * 11. دالة استخراج البيانات من Intent باستخدام دوال الصنف IntentKeys مع رقم الطالب
     */
    private void recupererDonneesIntent() {
        Intent intentRecu = getIntent();
        if (intentRecu != null) {
            String cleNom = IntentKeys.getNomKey(MATRICULE_ETUDIANT);
            String cleNaissance = IntentKeys.getAnneeNaissanceKey(MATRICULE_ETUDIANT);
            String cleDestination = IntentKeys.getAnneeDestinationKey(MATRICULE_ETUDIANT);
            String cleMotif = IntentKeys.getMotifKey(MATRICULE_ETUDIANT);

            if (intentRecu.hasExtra(cleNom)) {
                nomVoyageur = intentRecu.getStringExtra(cleNom);
            }
            anneeNaissance = intentRecu.getIntExtra(cleNaissance, 1990);
            anneeDestination = intentRecu.getIntExtra(cleDestination, 2050);
            if (intentRecu.hasExtra(cleMotif)) {
                motifVoyage = intentRecu.getStringExtra(cleMotif);
            }
        }
    }

    /**
     * 12. دالة حساب العمر الزمني وعرض المعطيات في نموذج الباسبور / التذكرة
     */
    private void afficherDetailsVoyage() {
        tvValeurNomYf.setText(nomVoyageur != null && !nomVoyageur.isEmpty() ? nomVoyageur : "Inconnu");

        // حساب العمر الزمني المقدر والفارق
        int difference = anneeDestination - anneeNaissance;
        int ageTemporel = Math.abs(difference);

        tvValeurAgeYf.setText(getString(R.string.valeur_age_format, ageTemporel));

        String signe = difference >= 0 ? "+" + difference : String.valueOf(difference);
        String detailsCoordonnees = anneeNaissance + " ➔ " + anneeDestination + " (" + signe + " ans)";
        tvValeurCoordonneesYf.setText(detailsCoordonnees);

        tvValeurMotifYf.setText(motifVoyage);
    }

    /**
     * 13. دالة فحص ما إذا كان العدد أولياً (Nombre Premier)
     * الأعداد الأولية هي الأعداد الصحيحة الأكبر من 1 والتي لا تقبل القسمة إلا على 1 ونفسها
     */
    private boolean isNombrePremier(int nombre) {
        int n = Math.abs(nombre);
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 14. تطبيق قيد المشروع الصارم:
     * إذا كان السبب "Modification du passé" والفارق الزمني عدداً أولياً، يتم تعطيل زر الموافقة
     */
    private void verifierConditionParadoxe() {
        int difference = Math.abs(anneeDestination - anneeNaissance);
        boolean estPremier = isNombrePremier(difference);
        boolean estModificationPasse = "Modification du passé".equalsIgnoreCase(motifVoyage);

        if (estModificationPasse && estPremier) {
            // تعطيل زر الموافقة فور تحميل النشاط
            btnApprouverYf.setEnabled(false);
            btnApprouverYf.setAlpha(0.35f);

            // إظهار صندوق تحذير أحمر يوضح سبب التعطيل وتفاصيل المفارقة (UI/UX)
            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
            tvAlerteParadoxeYf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_paradox, 0, 0, 0);
            tvAlerteParadoxeYf.setText(getString(R.string.alerte_paradoxe_danger, difference));

            Toast.makeText(this, "⚠️ Risque de paradoxe temporel détecté ! Approbation désactivée.", Toast.LENGTH_LONG).show();
        } else {
            // الحالة العادية: الزر مفعل والمؤشر أخضر
            btnApprouverYf.setEnabled(true);
            btnApprouverYf.setAlpha(1.0f);
            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_safe);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_green));
            tvAlerteParadoxeYf.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
            tvAlerteParadoxeYf.setText(R.string.alerte_paradoxe_safe);
        }
    }

    /**
     * 15. دالة الانتقال للنشاط الثالث (ResultActivity) وتمرير الحكم النهائي والاسم
     *
     * @param verdictApprouve true للموافقة و false للرفض
     */
    private void envoyerVerdict(boolean verdictApprouve) {
        // إنشاء نية صريحة (Explicit Intent) نحو ResultActivity
        Intent intentResultat = new Intent(VerificationActivity.this, ResultActivity.class);

        // تمرير البيانات عبر مفاتيح مشتقة ديناميكياً برقم بطاقة الطالب
        intentResultat.putExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT), nomVoyageur);
        intentResultat.putExtra(IntentKeys.getVerdictKey(MATRICULE_ETUDIANT), verdictApprouve);

        startActivity(intentResultat);
    }
}
