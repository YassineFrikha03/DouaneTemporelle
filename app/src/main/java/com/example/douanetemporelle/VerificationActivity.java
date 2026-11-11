package com.example.douanetemporelle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * 1. النشاط الثاني : فاحص التناقض الزمني والتحقق المتقدم عبر reCAPTCHA ضد الروبوتات (VerificationActivity)
 * تم استبدال الكاميرا بنظام reCAPTCHA أمني تفاعلي متقدم يولد نصوصاً بصرية مشوهة ديناميكياً.
 * شرط حاسم: بدون اجتياز اختبار reCAPTCHA، يتم رفض كافة أنواع السفر الزمنية (Tourisme, Modification, Fuite) تلقائياً.
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
    private LinearLayout llRecaptchaBoxYf;
    private ImageView ivRecaptchaCheckYf;
    private TextView tvStatutRecaptchaYf;
    private ImageView ivCodeBarreYf;
    private TextView tvCodeBarreNumeroYf;
    private MaterialButton btnApprouverYf;
    private MaterialButton btnRefuserYf;

    // 4. حالة التحقق الأمني: هل تم اجتياز اختبار reCAPTCHA بنجاح؟
    private boolean reCaptchaValide = false;
    private String codeCaptchaActuel = "";

    // 5. متغيرات تخزين البيانات المستلمة
    private String nomVoyageur;
    private int anneeNaissance;
    private int anneeDestination;
    private String motifVoyage;
    private int ageTemporel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 6. ربط النشاط بملف التصميم activity_verification.xml
        setContentView(R.layout.activity_verification);

        // 7. ربط عناصر واجهة المستخدم بالمعرفات البرمجية
        initialiserVues();

        // 8. استخراج البيانات المنقولة عبر Intent الصريح من MainActivity
        recupererDonneesIntent();

        // 9. معالجة البيانات، توليد الباركود وقفل الموافقة لحين حل reCAPTCHA
        traiterEtAfficherDonnees();

        // 10. تشغيل تأثير المسح الضوئي لهولوغرام الجواز
        animerHologrammePasseport();

        // 11. تفعيل مستمعات الأحداث للنقر على صندوق reCAPTCHA وأزرار القرار
        configurerActions();
    }

    /**
     * 12. دالة ربط عناصر الواجهة بالمعرفات الخاصة بالطالب
     */
    private void initialiserVues() {
        ivHologrammePasseportYf = findViewById(R.id.iv_hologramme_passeport_yf);
        tvValeurNomYf = findViewById(R.id.tv_valeur_nom_yf);
        tvValeurAgeYf = findViewById(R.id.tv_valeur_age_yf);
        tvValeurCoordonneesYf = findViewById(R.id.tv_valeur_coordonnees_yf);
        tvValeurMotifYf = findViewById(R.id.tv_valeur_motif_yf);
        tvAlerteParadoxeYf = findViewById(R.id.tv_alerte_paradoxe_yf);
        llRecaptchaBoxYf = findViewById(R.id.ll_recaptcha_box_yf);
        ivRecaptchaCheckYf = findViewById(R.id.iv_recaptcha_check_yf);
        tvStatutRecaptchaYf = findViewById(R.id.tv_statut_recaptcha_yf);
        ivCodeBarreYf = findViewById(R.id.iv_code_barre_yf);
        tvCodeBarreNumeroYf = findViewById(R.id.tv_code_barre_numero_yf);
        btnApprouverYf = findViewById(R.id.btn_approuver_yf);
        btnRefuserYf = findViewById(R.id.btn_refuser_yf);
    }

    /**
     * 13. استخراج البيانات المنقولة باستخدام المفاتيح الديناميكية
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
     * 14. دالة حسابية للتحقق مما إذا كان العدد أولياً (Nombre Premier)
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
     * 15. معالجة البيانات، توليد الباركود وتطبيق الرفض المبدئي لحين حل كابتشا
     */
    private void traiterEtAfficherDonnees() {
        int differenceBrute = anneeDestination - anneeNaissance;
        ageTemporel = Math.abs(differenceBrute);

        // عرض تفاصيل المسافر والتذكرة
        tvValeurNomYf.setText(nomVoyageur);
        tvValeurAgeYf.setText(getString(R.string.valeur_age_format, ageTemporel));
        tvValeurCoordonneesYf.setText(getString(R.string.format_coordonnees, anneeNaissance, anneeDestination, ageTemporel));
        tvValeurMotifYf.setText(motifVoyage);

        // توليد الباركود المشفر ديناميكياً
        String codeDonnees = "TN-" + MATRICULE_ETUDIANT + "-" + nomVoyageur.replaceAll("\\s+", "-").toUpperCase() + "-" + anneeDestination;
        Bitmap bitmapCodeBarre = BarcodeUtil.genererCodeBarre(codeDonnees, 420, 70);
        ivCodeBarreYf.setImageBitmap(bitmapCodeBarre);
        tvCodeBarreNumeroYf.setText("*" + codeDonnees + "*");

        // 16. تطبيق شرط الإلزام: في البداية لم يتم حل reCAPTCHA -> قفل زر الموافقة تماماً
        reCaptchaValide = false;
        btnApprouverYf.setEnabled(false);
        btnApprouverYf.setAlpha(0.35f);

        boolean estMotifModification = "Modification du passé".equalsIgnoreCase(motifVoyage)
                || "Modification du passe".equalsIgnoreCase(motifVoyage);
        boolean estPremier = isNombrePremier(ageTemporel);

        if (estMotifModification && estPremier) {
            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
            tvAlerteParadoxeYf.setText(getString(R.string.alerte_paradoxe_danger, ageTemporel));
        } else {
            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_amber));
            tvAlerteParadoxeYf.setText(R.string.alerte_sans_recaptcha);
        }
    }

    /**
     * 17. عرض نافذة اختبار reCAPTCHA البصري المتقدم
     */
    private void afficherDialogueRecaptcha() {
        // إذا كان تم التحقق مسبقاً، إبلاغ المستخدم
        if (reCaptchaValide) {
            Toast.makeText(this, "Test reCAPTCHA deja valide avec succes.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_recaptcha_challenge, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView ivImageCaptcha = dialogView.findViewById(R.id.iv_image_captcha_preview_yf);
        MaterialButton btnRafraichir = dialogView.findViewById(R.id.btn_rafraichir_captcha_yf);
        TextInputLayout tilReponse = dialogView.findViewById(R.id.til_reponse_captcha_yf);
        TextInputEditText etReponse = dialogView.findViewById(R.id.et_reponse_captcha_yf);
        MaterialButton btnAnnuler = dialogView.findViewById(R.id.btn_annuler_captcha_yf);
        MaterialButton btnValider = dialogView.findViewById(R.id.btn_valider_captcha_yf);

        // 18. دالة توليد صورة الكابتشا المشوهة وتحديثها
        Runnable genererNouveauCaptcha = () -> {
            CaptchaGenerator.ResultatCaptcha res = CaptchaGenerator.generer(340, 90);
            codeCaptchaActuel = res.getCode();
            ivImageCaptcha.setImageBitmap(res.getImage());
            tilReponse.setError(null);
            etReponse.setText("");
        };

        // توليد الكابتشا الأولى
        genererNouveauCaptcha.run();

        // زر تجديد الكود عند الرغبة
        btnRafraichir.setOnClickListener(v -> genererNouveauCaptcha.run());

        // زر إلغاء الاختبار
        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        // زر تأكيد حل الاختبار
        btnValider.setOnClickListener(v -> {
            String saisie = etReponse.getText() != null ? etReponse.getText().toString().trim().toUpperCase() : "";

            if (saisie.isEmpty()) {
                tilReponse.setError("Veuillez saisir le code affiche");
                return;
            }

            if (saisie.equals(codeCaptchaActuel)) {
                // 19. نجاح حل الكابتشا: اعتماد هوية الكائن البشري
                reCaptchaValide = true;
                ivRecaptchaCheckYf.setImageResource(R.drawable.ic_recaptcha_checked);
                tvStatutRecaptchaYf.setText(R.string.statut_recaptcha_valide);
                tvStatutRecaptchaYf.setTextColor(getColor(R.color.time_green));

                dialog.dismiss();

                // تقييم شروط السفر بعد إثبات الهوية البشرية
                evaluerConditionsVoyage();

                Toast.makeText(VerificationActivity.this, "Test reCAPTCHA reussi : Identite humaine confirmee.", Toast.LENGTH_SHORT).show();
            } else {
                // فشل الحل: كود غير مطابق
                tilReponse.setError("Code incorrect ! Veuillez reessayer.");
                genererNouveauCaptcha.run();

                reCaptchaValide = false;
                btnApprouverYf.setEnabled(false);
                btnApprouverYf.setAlpha(0.35f);
                tvStatutRecaptchaYf.setText(R.string.statut_recaptcha_echec);
                tvStatutRecaptchaYf.setTextColor(getColor(R.color.time_red));
                tvAlerteParadoxeYf.setText(R.string.alerte_recaptcha_refus_total);
                tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
                tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            }
        });

        dialog.show();
    }

    /**
     * 20. تقييم الشروط المشتركة بعد تأكيد حل reCAPTCHA بنجاح
     */
    private void evaluerConditionsVoyage() {
        boolean estMotifModification = "Modification du passé".equalsIgnoreCase(motifVoyage)
                || "Modification du passe".equalsIgnoreCase(motifVoyage);
        boolean estPremier = isNombrePremier(ageTemporel);

        if (estMotifModification && estPremier) {
            // قيد المفارقة الزمني: منع التعديل في السنوات الأولية
            btnApprouverYf.setEnabled(false);
            btnApprouverYf.setAlpha(0.35f);

            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
            tvAlerteParadoxeYf.setText(getString(R.string.alerte_paradoxe_danger, ageTemporel));
        } else {
            // كائن بشري مؤكد وبدون مفارقة زمنية -> تفعيل زر الموافقة
            btnApprouverYf.setEnabled(true);
            btnApprouverYf.setAlpha(1.0f);

            tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_safe);
            tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_green));
            tvAlerteParadoxeYf.setText(R.string.alerte_paradoxe_safe);
        }
    }

    /**
     * 21. وميض المسح الضوئي لهولوغرام الجواز
     */
    private void animerHologrammePasseport() {
        ObjectAnimator animateurScan = ObjectAnimator.ofFloat(ivHologrammePasseportYf, "alpha", 0.65f, 1.0f);
        animateurScan.setDuration(500);
        animateurScan.setRepeatCount(3);
        animateurScan.setRepeatMode(ValueAnimator.REVERSE);
        animateurScan.start();
    }

    /**
     * 22. إعداد أحداث النقر لصندوق reCAPTCHA ولأزرار اتخاذ القرار
     */
    private void configurerActions() {
        // النقر على صندوق reCAPTCHA لفتح نافذة الاختبار التفاعلي
        llRecaptchaBoxYf.setOnClickListener(v -> afficherDialogueRecaptcha());

        // زر الموافقة: محمي بقيد reCAPTCHA الإلزامي
        btnApprouverYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reCaptchaValide) {
                    Toast.makeText(VerificationActivity.this, R.string.toast_refus_sans_recaptcha, Toast.LENGTH_LONG).show();
                    return;
                }
                ouvrirResultat(true);
            }
        });

        // زر الرفض: متاح دائماً لرفض الروبوتات أو حالات الاشتباه
        btnRefuserYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirResultat(false);
            }
        });
    }

    /**
     * 23. إنشاء نية صريحة نحو ResultActivity مع نقل الحكم النهائي، اسم المسافر والإحداثيات الزمنية
     */
    private void ouvrirResultat(boolean verdictFinal) {
        Intent intentResultat = new Intent(VerificationActivity.this, ResultActivity.class);
        intentResultat.putExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT), nomVoyageur);
        intentResultat.putExtra(IntentKeys.getVerdictKey(MATRICULE_ETUDIANT), verdictFinal);
        intentResultat.putExtra(IntentKeys.getAnneeNaissanceKey(MATRICULE_ETUDIANT), anneeNaissance);
        intentResultat.putExtra(IntentKeys.getAnneeDestinationKey(MATRICULE_ETUDIANT), anneeDestination);
        intentResultat.putExtra(IntentKeys.getMotifKey(MATRICULE_ETUDIANT), motifVoyage);
        startActivity(intentResultat);
    }
}
