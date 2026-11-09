package com.example.douanetemporelle;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

/**
 * 1. النشاط الثاني : فاحص التناقض الزمني، جواز السفر والتحقق من الوجه عبر الكاميرا ضد الروبوتات (VerificationActivity)
 * يتيح التقاط صورة حية للمسافر للتأكد من هويته البشرية، يعرض الباركود الأمني المشفر،
 * ويطبق قاعدة الأعداد الأولية لمنع التناقض الزمني.
 */
public class VerificationActivity extends AppCompatActivity {

    // 2. كود طلب إذن الكاميرا
    private static final int CODE_PERMISSION_CAMERA = 101;

    // 3. آخر 4 أرقام من بطاقة الطالب لإنشاء مفاتيح Intent ديناميكياً
    private static final String MATRICULE_ETUDIANT = "1234";

    // 4. تصريح عناصر الواجهة الرسومية (تنتهي جميعها بـ _yf)
    private ImageView ivHologrammePasseportYf;
    private TextView tvValeurNomYf;
    private TextView tvValeurAgeYf;
    private TextView tvValeurCoordonneesYf;
    private TextView tvValeurMotifYf;
    private TextView tvAlerteParadoxeYf;
    private TextView tvValeurScanFacialYf;
    private MaterialButton btnScannerCameraYf;
    private ImageView ivCodeBarreYf;
    private TextView tvCodeBarreNumeroYf;
    private MaterialButton btnApprouverYf;
    private MaterialButton btnRefuserYf;

    // 5. مشغل النشاط لالتقاط الصورة عبر الكاميرا واستقبال النتيجة
    private ActivityResultLauncher<Intent> lanceurCameraYf;

    // 6. متغيرات تخزين البيانات المستلمة
    private String nomVoyageur;
    private int anneeNaissance;
    private int anneeDestination;
    private String motifVoyage;
    private int ageTemporel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 7. ربط النشاط بملف التصميم activity_verification.xml
        setContentView(R.layout.activity_verification);

        // 8. إعداد مشغل الكاميرا لاستقبال صورة المسافر الحقيقية للتحقق ضد الروبوتات
        initialiserLanceurCamera();

        // 9. ربط عناصر واجهة المستخدم بالمعرفات البرمجية
        initialiserVues();

        // 10. استخراج البيانات المنقولة عبر Intent الصريح من MainActivity
        recupererDonneesIntent();

        // 11. حساب العمر الزمني، توليد الباركود وفحص شرط المفارقة
        traiterEtAfficherDonnees();

        // 12. تشغيل تأثير المسح الضوئي البيومتري للوجه
        animerScannerFacial();

        // 13. تفعيل مستمعات الأحداث للنقر على أزرار القرار والكاميرا
        configurerActions();
    }

    /**
     * 14. إعداد مشغل الكاميرا لمعالجة الصورة الملتقطة وعرضها داخل جواز السفر
     */
    private void initialiserLanceurCamera() {
        lanceurCameraYf = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.get("data") != null) {
                            Bitmap photoCapturee = (Bitmap) extras.get("data");

                            // عرض الصورة الحقيقية للمسافر في مكان الهولوغرام
                            ivHologrammePasseportYf.setImageBitmap(photoCapturee);
                            ivHologrammePasseportYf.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            // تحديث حالة الفحص لتأكيد أن الكائن بشري وليس روبوت أو مستنسخ زماني
                            tvValeurScanFacialYf.setText(R.string.statut_anti_robot_valide);
                            tvValeurScanFacialYf.setTextColor(getColor(R.color.time_green));

                            Toast.makeText(this, "Verification reussie : Sujet biologique confirme (Non-robot)", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    /**
     * 15. دالة ربط عناصر الواجهة بالمعرفات الخاصة بالطالب
     */
    private void initialiserVues() {
        ivHologrammePasseportYf = findViewById(R.id.iv_hologramme_passeport_yf);
        tvValeurNomYf = findViewById(R.id.tv_valeur_nom_yf);
        tvValeurAgeYf = findViewById(R.id.tv_valeur_age_yf);
        tvValeurCoordonneesYf = findViewById(R.id.tv_valeur_coordonnees_yf);
        tvValeurMotifYf = findViewById(R.id.tv_valeur_motif_yf);
        tvAlerteParadoxeYf = findViewById(R.id.tv_alerte_paradoxe_yf);
        tvValeurScanFacialYf = findViewById(R.id.tv_valeur_scan_facial_yf);
        btnScannerCameraYf = findViewById(R.id.btn_scanner_camera_yf);
        ivCodeBarreYf = findViewById(R.id.iv_code_barre_yf);
        tvCodeBarreNumeroYf = findViewById(R.id.tv_code_barre_numero_yf);
        btnApprouverYf = findViewById(R.id.btn_approuver_yf);
        btnRefuserYf = findViewById(R.id.btn_refuser_yf);
    }

    /**
     * 16. دالة استخراج البيانات المرسلة باستخدام المفاتيح المولدة ديناميكياً
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
     * 17. دالة حسابية للتحقق مما إذا كان الفارق الزمني عدداً أولياً (Nombre Premier)
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
     * 18. معالجة البيانات، حساب الفارق الزمني، توليد الباركود، وفحص شرط المفارقة
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

        // توليد الباركود المشفر ديناميكياً وتطبيقه على التذكرة
        String codeDonnees = "TN-" + MATRICULE_ETUDIANT + "-" + nomVoyageur.replaceAll("\\s+", "-").toUpperCase() + "-" + anneeDestination;
        Bitmap bitmapCodeBarre = BarcodeUtil.genererCodeBarre(codeDonnees, 420, 70);
        ivCodeBarreYf.setImageBitmap(bitmapCodeBarre);
        tvCodeBarreNumeroYf.setText("*" + codeDonnees + "*");

        // فحص شرط التناقض الزمني المطلوب في كراس الشروط:
        // إذا كان السبب 'Modification du passé' والفارق الزمني عدداً أولياً -> تعطيل زر القبول فوراً
        boolean estMotifModification = "Modification du passé".equalsIgnoreCase(motifVoyage)
                || "Modification du passe".equalsIgnoreCase(motifVoyage);
        boolean estPremier = isNombrePremier(ageTemporel);

        if (estMotifModification && estPremier) {
            btnApprouverYf.setEnabled(false);
            btnApprouverYf.setAlpha(0.35f);

            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
            tvAlerteParadoxeYf.setText(getString(R.string.alerte_paradoxe_danger, ageTemporel));
        } else {
            btnApprouverYf.setEnabled(true);
            btnApprouverYf.setAlpha(1.0f);

            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_safe);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_green));
            tvAlerteParadoxeYf.setText(R.string.alerte_paradoxe_safe);
        }
    }

    /**
     * 19. تشغيل وميض حركي يحاكي مسح الليزر البيومتري للوجه على صورة جواز السفر
     */
    private void animerScannerFacial() {
        ObjectAnimator animateurScan = ObjectAnimator.ofFloat(ivHologrammePasseportYf, "alpha", 0.55f, 1.0f);
        animateurScan.setDuration(450);
        animateurScan.setRepeatCount(3);
        animateurScan.setRepeatMode(ValueAnimator.REVERSE);
        animateurScan.start();
    }

    /**
     * 20. التحقق من صلاحية الكاميرا وفتحها للتحقق من هوية المسافر
     */
    private void verifierEtLancerCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE_PERMISSION_CAMERA);
        } else {
            ouvrirAppareilPhoto();
        }
    }

    /**
     * 21. إطلاق نية الكاميرا لالتقاط صورة حية للمسافر
     */
    private void ouvrirAppareilPhoto() {
        Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            lanceurCameraYf.launch(intentPhoto);
        } catch (Exception e) {
            Toast.makeText(this, "Impossible d\'acceder a la camera sur cet appareil", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ouvrirAppareilPhoto();
        } else {
            Toast.makeText(this, "Autorisation camera requise pour le scan anti-robot", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 22. إعداد أحداث النقر للأزرار (الكاميرا، الموافقة، والرفض)
     */
    private void configurerActions() {
        // النقر على زر فحص الكاميرا
        btnScannerCameraYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifierEtLancerCamera();
            }
        });

        // النقر المباشر على صورة جواز السفر لفتح الكاميرا أيضاً
        ivHologrammePasseportYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifierEtLancerCamera();
            }
        });

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
     * 23. إنشاء نية صريحة (Explicit Intent) نحو النشاط الثالث ResultActivity
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
