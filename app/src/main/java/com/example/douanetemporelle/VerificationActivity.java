package com.example.douanetemporelle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

/**
 * 1. النشاط الثاني : فاحص التناقض الزمني وجواز السفر البيومتري (VerificationActivity)
 * يستقبل بيانات المسافر، ينفذ الفحص البيومتري للوجه والباركود الأمني المشفر،
 * يتحقق من قاعدة الأعداد الأولية، ثم ينقل الحكم إلى النشاط الثالث ResultActivity.
 */
public class VerificationActivity extends AppCompatActivity {

    // 2. آخر 4 أرقام من بطاقة الطالب لإنشاء مفاتيح Intent ديناميكياً
    private static final String MATRICULE_ETUDIANT = "1234";

    // 3. تصريح عناصر الواجهة الرسومية (تنتهي جميعها بـ _yf)
    private ImageView ivHologrammePasseportYf;
    private TextView tvValeurNomYf;
    private TextView tvValeurAgeYf;
    private TextView tvValeurCoordonneesYf;
    private TextView tvValeurMotifYf;
    private TextView tvAlerteParadoxeYf;
    private TextView tvValeurScanFacialYf;
    private ImageView ivCodeBarreYf;
    private TextView tvCodeBarreNumeroYf;
    private MaterialButton btnApprouverYf;
    private MaterialButton btnRefuserYf;

    // 4. متغيرات تخزين البيانات المستلمة
    private String nomVoyageur;
    private int anneeNaissance;
    private int anneeDestination;
    private String motifVoyage;
    private int ageTemporel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 5. ربط النشاط بملف التصميم activity_verification.xml
        setContentView(R.layout.activity_verification);

        // 6. ربط عناصر واجهة المستخدم بالمعرفات البرمجية
        initialiserVues();

        // 7. استخراج البيانات المنقولة عبر Intent الصريح من MainActivity
        recupererDonneesIntent();

        // 8. حساب العمر الزمني، توليد الباركود وفحص شرط المفارقة
        traiterEtAfficherDonnees();

        // 9. تشغيل تأثير المسح الضوئي البيومتري للوجه
        animerScannerFacial();

        // 10. تفعيل مستمعات الأحداث للنقر على أزرار القرار
        configurerActions();
    }

    /**
     * 11. دالة ربط عناصر الواجهة بالمعرفات الخاصة بالطالب
     */
    private void initialiserVues() {
        ivHologrammePasseportYf = findViewById(R.id.iv_hologramme_passeport_yf);
        tvValeurNomYf = findViewById(R.id.tv_valeur_nom_yf);
        tvValeurAgeYf = findViewById(R.id.tv_valeur_age_yf);
        tvValeurCoordonneesYf = findViewById(R.id.tv_valeur_coordonnees_yf);
        tvValeurMotifYf = findViewById(R.id.tv_valeur_motif_yf);
        tvAlerteParadoxeYf = findViewById(R.id.tv_alerte_paradoxe_yf);
        tvValeurScanFacialYf = findViewById(R.id.tv_valeur_scan_facial_yf);
        ivCodeBarreYf = findViewById(R.id.iv_code_barre_yf);
        tvCodeBarreNumeroYf = findViewById(R.id.tv_code_barre_numero_yf);
        btnApprouverYf = findViewById(R.id.btn_approuver_yf);
        btnRefuserYf = findViewById(R.id.btn_refuser_yf);
    }

    /**
     * 12. دالة استخراج البيانات المرسلة باستخدام المفاتيح المولدة ديناميكياً
     */
    private void recupererDonneesIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            nomVoyageur = intent.getStringExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT));
            anneeNaissance = intent.getIntExtra(IntentKeys.getAnneeNaissanceKey(MATRICULE_ETUDIANT), 1990);
            anneeDestination = intent.getIntExtra(IntentKeys.getAnneeDestinationKey(MATRICULE_ETUDIANT), 2050);
            motifVoyage = intent.getStringExtra(IntentKeys.getMotifKey(MATRICULE_ETUDIANT));
        }

        if (nomVoyageur == null || nomVoyageur.trim().isEmpty()) {
            nomVoyageur = "Inconnu";
        }
        if (motifVoyage == null || motifVoyage.trim().isEmpty()) {
            motifVoyage = "Tourisme";
        }
    }

    /**
     * 13. دالة حسابية للتحقق مما إذا كان الفارق الزمني عدداً أولياً (Nombre Premier)
     * العدد الأولي هو عدد صحيح أكبر قطعا من 1 لا يقبل القسمة إلا على 1 وعلى نفسه
     *
     * @param nombre العدد المراد اختباره
     * @return true إذا كان أولياً، false إذا لم يكن أولياً
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
     * 14. معالجة البيانات، حساب الفارق الزمني، توليد الباركود، وفحص شرط المفارقة
     */
    private void traiterEtAfficherDonnees() {
        // حساب الفارق الزمني المطلق كعمر زمني للقفزة
        int differenceBrute = anneeDestination - anneeNaissance;
        ageTemporel = Math.abs(differenceBrute);

        // عرض تفاصيل المسافر والتذكرة
        tvValeurNomYf.setText(nomVoyageur);
        tvValeurAgeYf.setText(getString(R.string.valeur_age_format, ageTemporel));
        tvValeurCoordonneesYf.setText(getString(R.string.format_coordonnees, anneeNaissance, anneeDestination, ageTemporel));
        tvValeurMotifYf.setText(motifVoyage);

        // 15. توليد الباركود المشفر ديناميكياً وتطبيقه على التذكرة
        String codeDonnees = "TN-" + MATRICULE_ETUDIANT + "-" + nomVoyageur.replaceAll("\\s+", "-").toUpperCase() + "-" + anneeDestination;
        Bitmap bitmapCodeBarre = BarcodeUtil.genererCodeBarre(codeDonnees, 420, 70);
        ivCodeBarreYf.setImageBitmap(bitmapCodeBarre);
        tvCodeBarreNumeroYf.setText("*" + codeDonnees + "*");

        // 16. فحص شرط التناقض الزمني المطلوب في كراس الشروط:
        // إذا كان السبب 'Modification du passé' والفارق الزمني عدداً أولياً -> تعطيل زر القبول فوراً
        boolean estMotifModification = "Modification du passé".equalsIgnoreCase(motifVoyage)
                || "Modification du passe".equalsIgnoreCase(motifVoyage);
        boolean estPremier = isNombrePremier(ageTemporel);

        if (estMotifModification && estPremier) {
            // تعطيل زر الموافقة فوراً عند تحميل الشاشة
            btnApprouverYf.setEnabled(false);
            btnApprouverYf.setAlpha(0.35f);

            // إظهار رسالة تحذير الخطر باللون الأحمر
            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
            tvAlerteParadoxeYf.setText(getString(R.string.alerte_paradoxe_danger, ageTemporel));
        } else {
            // تمكين زر الموافقة في الحالة السليمة
            btnApprouverYf.setEnabled(true);
            btnApprouverYf.setAlpha(1.0f);

            // إظهار رسالة التأكيد والموافقة باللون الأخضر
            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_safe);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_green));
            tvAlerteParadoxeYf.setText(R.string.alerte_paradoxe_safe);
        }
    }

    /**
     * 17. تشغيل وميض حركي يحاكي مسح الليزر البيومتري للوجه على صورة جواز السفر
     */
    private void animerScannerFacial() {
        ObjectAnimator animateurScan = ObjectAnimator.ofFloat(ivHologrammePasseportYf, "alpha", 0.55f, 1.0f);
        animateurScan.setDuration(450);
        animateurScan.setRepeatCount(3);
        animateurScan.setRepeatMode(ValueAnimator.REVERSE);
        animateurScan.start();
    }

    /**
     * 18. إعداد أحداث النقر لأزرار الموافقة والرفض لإرسال الحكم إلى ResultActivity
     */
    private void configurerActions() {
        // حدث النقر على زر الموافقة على القفزة الزمنية
        btnApprouverYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirResultat(true);
            }
        });

        // حدث النقر على زر رفض القفزة الزمنية (خطر المفارقة)
        btnRefuserYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirResultat(false);
            }
        });
    }

    /**
     * 19. إنشاء نية صريحة (Explicit Intent) نحو النشاط الثالث ResultActivity
     * مع نقل الحكم النهائي (booléen) واسم المسافر وفق المفاتيح الديناميكية
     *
     * @param verdictFinal الحكم النهائي (true: مقبول، false: مرفوض)
     */
    private void ouvrirResultat(boolean verdictFinal) {
        Intent intentResultat = new Intent(VerificationActivity.this, ResultActivity.class);
        intentResultat.putExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT), nomVoyageur);
        intentResultat.putExtra(IntentKeys.getVerdictKey(MATRICULE_ETUDIANT), verdictFinal);
        startActivity(intentResultat);
    }
}
