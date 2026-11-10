package tn.fsegt.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط 2: فاحص التناقض الزمني والتحقق من الهوية - نظام كرونوس
 * المرحلة 03: استقبال بيانات المسافر عبر القصد الصريح (Explicit Intent) والمفاتيح الديناميكية
 */
public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تحميل واجهة التحقق بالهيكل الأفقي الإلزامي
        setContentView(R.layout.activity_verification);

        // ربط عناصر العرض الخاصة باستقبال البيانات
        TextView txtValNom = findViewById(R.id.txt_val_nom_recu_oa);
        TextView txtValNaissance = findViewById(R.id.txt_val_naissance_recue_oa);
        TextView txtValDestination = findViewById(R.id.txt_val_destination_recue_oa);
        TextView txtValMotif = findViewById(R.id.txt_val_motif_recu_oa);

        // استخراج البيانات من القصد الصريح باستخدام الدالة الثابتة ورقم بطاقة الطالب 1688
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

            // عرض البيانات المستلمة مع تفادي القيم الفارغة
            if (txtValNom != null) {
                txtValNom.setText(nomRecu != null && !nomRecu.isEmpty() ? nomRecu : "Inconnu");
            }
            if (txtValNaissance != null) {
                txtValNaissance.setText(String.valueOf(anneeNaissanceRecue));
            }
            if (txtValDestination != null) {
                txtValDestination.setText(String.valueOf(anneeDestinationRecue));
            }
            if (txtValMotif != null) {
                txtValMotif.setText(motifRecu != null && !motifRecu.isEmpty() ? motifRecu : "Non spécifié");
            }
        }
    }
}
