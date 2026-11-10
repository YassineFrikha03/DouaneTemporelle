package tn.fsegt.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط 3: النتيجة النهائية وقرار الجمارك الزمنية كرونوس
 * المرحلة 06: معالجة القرار النهائي وإعادة توجيه المسافر الجديد
 */
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // ربط عناصر واجهة المستخدم ذات المعرفات المنتهية بـ _oa
        RelativeLayout layoutRoot = findViewById(R.id.layout_result_root_oa);
        ImageView imgIcon = findViewById(R.id.img_verdict_icon_oa);
        TextView txtTitle = findViewById(R.id.txt_verdict_title_oa);
        TextView txtMessage = findViewById(R.id.txt_message_verdict_oa);
        TextView txtDesc = findViewById(R.id.txt_desc_verdict_oa);
        Button btnNouveauVoyageur = findViewById(R.id.btn_nouveau_voyageur_oa);

        // توليد المفاتيح الديناميكية بالاعتماد على رقم بطاقة الطالب 1688
        String keyVerdict = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_VERDICT_FINAL);
        String keyNom = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR);

        // استرجاع البيانات المرسلة عبر Intent مع توفير قيم افتراضية آمنة
        boolean verdict = false;
        String nomVoyageur = "Voyageur Inconnu";

        Intent intentRecu = getIntent();
        if (intentRecu != null) {
            verdict = intentRecu.getBooleanExtra(keyVerdict, false);
            String nomExtra = intentRecu.getStringExtra(keyNom);
            if (nomExtra != null && !nomExtra.trim().isEmpty()) {
                nomVoyageur = nomExtra.trim();
            }
        }

        // تطبيق المنطق والسمة البصرية حسب القرار النهائي (موافقة أو رفض)
        if (verdict) {
            // حالة الموافقة: خلفية خضراء واضحة ورسالة "Bon voyage [Nom]"
            layoutRoot.setBackgroundResource(R.drawable.bg_verdict_approved);
            imgIcon.setImageResource(R.drawable.ic_verdict_approved);
            txtTitle.setText(R.string.title_verdict_approuve);
            txtMessage.setText(getString(R.string.title_verdict_approuve).isEmpty() ? "" : "Bon voyage " + nomVoyageur);
            txtDesc.setText(R.string.desc_verdict_approuve);
        } else {
            // حالة الرفض: خلفية حمراء واضحة ورسالة "[Nom] est en état d'arrestation temporelle"
            layoutRoot.setBackgroundResource(R.drawable.bg_verdict_refused);
            imgIcon.setImageResource(R.drawable.ic_verdict_refused);
            txtTitle.setText(R.string.title_verdict_refuse);
            txtMessage.setText(nomVoyageur + " est en état d'arrestation temporelle");
            txtDesc.setText(R.string.desc_verdict_refuse);
        }

        // إعداد زر "مسافر جديد" للعودة إلى النشاط الرئيسي وتصفير النموذج
        btnNouveauVoyageur.setOnClickListener(v -> {
            Intent returnIntent = new Intent(ResultActivity.this, MainActivity.class);
            // إغلاق كافة الأنشطة السابقة في المكدس لضمان العودة النظيفة
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(returnIntent);
            finish();
        });
    }
}
