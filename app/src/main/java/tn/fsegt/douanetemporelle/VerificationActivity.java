package tn.fsegt.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط 2: فاحص التناقض الزمني والتحقق من الهوية - نظام كرونوس
 * المرحلة 04: واجهة جواز السفر الزمني، حساب العمر الزمني، وربط زري القرار
 */
public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تحميل واجهة جواز السفر بالهيكل الأفقي الصارم (0.3 صورة يساراً و 0.7 نصوص يميناً)
        setContentView(R.layout.activity_verification);

        // ربط عناصر عرض بيانات جواز السفر الزمني
        TextView txtValNom = findViewById(R.id.txt_val_nom_oa);
        TextView txtValAge = findViewById(R.id.txt_val_age_oa);
        TextView txtValTrajectoire = findViewById(R.id.txt_val_trajectoire_oa);
        TextView txtValMotif = findViewById(R.id.txt_val_motif_oa);

        // ربط زري اتخاذ القرار للمرحلة الحالية
        Button btnApprouver = findViewById(R.id.btn_approuver_oa);
        Button btnRefuser = findViewById(R.id.btn_refuser_oa);

        // استخراج البيانات المستلمة عبر القصد الصريح بالاعتماد على المفاتيح الديناميكية ورقم الطالب 1688
        Intent intent = getIntent();
        if (intent != null) {
            String keyNom = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR);
            String keyNaissance = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_NAISSANCE);
            String keyDestination = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_DESTINATION);
            String keyMotif = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_MOTIF_VOYAGE);

            String nomRecu = intent.getStringExtra(keyNom);
            int anneeNaissanceRecue = intent.getIntExtra(keyNaissance, 0);
            int anneeDestinationRecue = intent.getIntExtra(keyDestination, 0);
            String motifRecu = intent.getStringExtra(keyMotif);

            // حساب العمر الزمني: الفارق بين سنة الوجهة وسنة الولادة
            int differenceAnnees = anneeDestinationRecue - anneeNaissanceRecue;

            // عرض اسم المسافر
            if (txtValNom != null) {
                txtValNom.setText(nomRecu != null && !nomRecu.trim().isEmpty() ? nomRecu : "Voyageur non identifié");
            }

            // عرض العمر الزمني المحسوب
            if (txtValAge != null) {
                String ageAffiche = differenceAnnees + " ans";
                txtValAge.setText(ageAffiche);
            }

            // عرض مسار الرحلة الزمنية للتوضيح (سنة الولادة -> سنة الوجهة)
            if (txtValTrajectoire != null) {
                String trajectoire = anneeNaissanceRecue + " ➔ " + anneeDestinationRecue
                        + " (" + (differenceAnnees >= 0 ? "+" : "") + differenceAnnees + " ans)";
                txtValTrajectoire.setText(trajectoire);
            }

            // عرض سبب السفر المصرح به
            if (txtValMotif != null) {
                txtValMotif.setText(motifRecu != null && !motifRecu.trim().isEmpty() ? motifRecu : "Non spécifié");
            }
        }
    }
}
