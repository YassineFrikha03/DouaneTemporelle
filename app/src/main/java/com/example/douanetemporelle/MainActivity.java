package com.example.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * 1. النشاط الأول : استمارة تسجيل المسافر عبر الزمن (MainActivity)
 * يقوم بجمع بيانات المسافر، التحقق منها، وتمريرها إلى شاشة الفحص (VerificationActivity)
 * الكود معلق باللغة العربية مع الأرقام، وتطابق المعرفات الأحرف الأولى لطالب المجموعة (_yf)
 */
public class MainActivity extends AppCompatActivity {

    // 2. التصريح عن عناصر الواجهة (UI Elements) مع بادئة الأحرف الأولى (_yf)
    private TextInputLayout tilNomYf, tilNaissanceYf, tilDestinationYf;
    private TextInputEditText etNomYf, etNaissanceYf, etDestinationYf;
    private TextView tvApercuTemporelYf;
    private RadioGroup rgMotifYf;
    private RadioButton rbTourismeYf, rbModificationYf, rbFuiteYf;
    private MaterialButton btnInspecterYf;

    // 3. رقم بطاقة الطالب لاستخدامه في توليد مفاتيح Intent الديناميكية
    private static final String MATRICULE_ETUDIANT = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 4. ربط العناصر البرمجية بمكونات الواجهة الرسومية (XML IDs المنتهية بـ _yf)
        initialiserVues();

        // 5. تفعيل مراقبة التغييرات الحية لحساب وتحديث الفارق الزمني (تحسين تجربة المستخدم UI/UX)
        configurerCalculateurTemporel();

        // 6. برمجة حدث النقر على زر "Inspecter le profil" للتحقق والانتقال
        btnInspecterYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                traiterEtEnvoyerDonnees();
            }
        });
    }

    /**
     * 7. دالة تهيئة وربط جميع العناصر الرسومية مع التحقق من المعرفات (_yf)
     */
    private void initialiserVues() {
        tilNomYf = findViewById(R.id.til_nom_yf);
        tilNaissanceYf = findViewById(R.id.til_annee_naissance_yf);
        tilDestinationYf = findViewById(R.id.til_annee_destination_yf);

        etNomYf = findViewById(R.id.et_nom_yf);
        etNaissanceYf = findViewById(R.id.et_annee_naissance_yf);
        etDestinationYf = findViewById(R.id.et_annee_destination_yf);

        tvApercuTemporelYf = findViewById(R.id.tv_apercu_temporel_yf);
        rgMotifYf = findViewById(R.id.rg_motif_yf);
        rbTourismeYf = findViewById(R.id.rb_tourisme_yf);
        rbModificationYf = findViewById(R.id.rb_modification_yf);
        rbFuiteYf = findViewById(R.id.rb_fuite_yf);

        btnInspecterYf = findViewById(R.id.btn_inspecter_yf);
    }

    /**
     * 8. دالة لمراقبة السنوات المدخلة وتقديم تغذية راجعة فورية وذكية للمستخدم (UI/UX Feedback)
     */
    private void configurerCalculateurTemporel() {
        TextWatcher observateurTemps = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculerApercuTemporel();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etNaissanceYf.addTextChangedListener(observateurTemps);
        etDestinationYf.addTextChangedListener(observateurTemps);
    }

    /**
     * 9. حساب الفارق الزمني بين سنة الميلاد وسنة الوجهة وتحديث النص التوضيحي ديناميكياً
     */
    private void calculerApercuTemporel() {
        String strNaissance = etNaissanceYf.getText() != null ? etNaissanceYf.getText().toString().trim() : "";
        String strDestination = etDestinationYf.getText() != null ? etDestinationYf.getText().toString().trim() : "";

        if (strNaissance.isEmpty() || strDestination.isEmpty()) {
            tvApercuTemporelYf.setText(R.string.apercu_attente);
            tvApercuTemporelYf.setTextColor(getColor(R.color.time_text_secondary));
            return;
        }

        try {
            int anneeNaissance = Integer.parseInt(strNaissance);
            int anneeDestination = Integer.parseInt(strDestination);
            int difference = anneeDestination - anneeNaissance;

            if (difference > 0) {
                // قفزة نحو المستقبل
                String message = getString(R.string.apercu_futur, difference);
                tvApercuTemporelYf.setText(message);
                tvApercuTemporelYf.setTextColor(getColor(R.color.time_cyan));
            } else if (difference < 0) {
                // قفزة نحو الماضي
                String message = getString(R.string.apercu_passe, Math.abs(difference));
                tvApercuTemporelYf.setText(message);
                tvApercuTemporelYf.setTextColor(getColor(R.color.time_purple));
            } else {
                // نفس السنة
                tvApercuTemporelYf.setText(R.string.apercu_present);
                tvApercuTemporelYf.setTextColor(getColor(R.color.time_amber));
            }
        } catch (NumberFormatException e) {
            tvApercuTemporelYf.setText(R.string.apercu_attente);
        }
    }

    /**
     * 10. معالجة البيانات، التحقق من الحقول الإجبارية، وإنشاء نية صريحة (Explicit Intent)
     */
    private void traiterEtEnvoyerDonnees() {
        // إعادة تعيين رسائل الخطأ السابقة
        tilNomYf.setError(null);
        tilNaissanceYf.setError(null);
        tilDestinationYf.setError(null);

        // قراءة النصوص المدخلة
        String nom = etNomYf.getText() != null ? etNomYf.getText().toString().trim() : "";
        String strNaissance = etNaissanceYf.getText() != null ? etNaissanceYf.getText().toString().trim() : "";
        String strDestination = etDestinationYf.getText() != null ? etDestinationYf.getText().toString().trim() : "";

        // 11. التحقق من ملء حقل الاسم
        if (nom.isEmpty()) {
            tilNomYf.setError(getString(R.string.erreur_nom));
            etNomYf.requestFocus();
            return;
        }

        // 12. التحقق من صحة سنة الميلاد
        if (strNaissance.isEmpty()) {
            tilNaissanceYf.setError(getString(R.string.erreur_annee_naissance));
            etNaissanceYf.requestFocus();
            return;
        }

        int anneeNaissance;
        try {
            anneeNaissance = Integer.parseInt(strNaissance);
        } catch (NumberFormatException e) {
            tilNaissanceYf.setError(getString(R.string.erreur_annee_naissance));
            etNaissanceYf.requestFocus();
            return;
        }

        // 13. التحقق من صحة سنة الوجهة
        if (strDestination.isEmpty()) {
            tilDestinationYf.setError(getString(R.string.erreur_annee_destination));
            etDestinationYf.requestFocus();
            return;
        }

        int anneeDestination;
        try {
            anneeDestination = Integer.parseInt(strDestination);
        } catch (NumberFormatException e) {
            tilDestinationYf.setError(getString(R.string.erreur_annee_destination));
            etDestinationYf.requestFocus();
            return;
        }

        // 14. استخراج سبب السفر المختار من أزرار الراديو
        String motifVoyage = "Tourisme"; // القيمة الافتراضية
        int selectedRadioId = rgMotifYf.getCheckedRadioButtonId();

        if (selectedRadioId == R.id.rb_modification_yf) {
            motifVoyage = "Modification du passé";
        } else if (selectedRadioId == R.id.rb_fuite_yf) {
            motifVoyage = "Fuite";
        } else {
            motifVoyage = "Tourisme";
        }

        // 15. إنشاء نية صريحة (Explicit Intent) نحو النشاط الثاني VerificationActivity
        Intent intentVerification = new Intent(MainActivity.this, VerificationActivity.class);

        // 16. تمرير البيانات مع تطبيق قيد بناء المفاتيح ديناميكياً بدالة ثابتة برقم الطالب
        intentVerification.putExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT), nom);
        intentVerification.putExtra(IntentKeys.getAnneeNaissanceKey(MATRICULE_ETUDIANT), anneeNaissance);
        intentVerification.putExtra(IntentKeys.getAnneeDestinationKey(MATRICULE_ETUDIANT), anneeDestination);
        intentVerification.putExtra(IntentKeys.getMotifKey(MATRICULE_ETUDIANT), motifVoyage);

        // 17. إشعار سريع للمستخدم بنجاح الفحص قبل الانتقال
        Toast.makeText(this, "Analyse temporelle en cours pour " + nom + "...", Toast.LENGTH_SHORT).show();

        // 18. تشغيل النشاط الثاني
        startActivity(intentVerification);
    }
}
