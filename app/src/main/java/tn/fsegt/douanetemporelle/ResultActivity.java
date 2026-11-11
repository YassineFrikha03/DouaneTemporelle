package tn.fsegt.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * النشاط 3: النتيجة النهائية وقرار الجمارك الزمنية كرونوس
 * المرحلة 06: معالجة القرار النهائي وإعادة توجيه المسافر الجديد
 * المرحلة 09: عرض الختم الجمركي المتحرك ورقم الجواز الزمني الموثق
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
        TextView txtNumeroPasseport = findViewById(R.id.txt_numero_passeport_result_oa);
        TextView txtTamponVerdict = findViewById(R.id.txt_tampon_verdict_oa);
        Button btnNouveauVoyageur = findViewById(R.id.btn_nouveau_voyageur_oa);

        // توليد المفاتيح الديناميكية بالاعتماد على رقم بطاقة الطالب 1688
        String keyVerdict = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_VERDICT_FINAL);
        String keyNom = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR);
        String keyPassport = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NUMERO_PASSEPORT);

        // استرجاع البيانات المرسلة عبر Intent مع توفير قيم افتراضية آمنة
        boolean verdict = false;
        String nomVoyageur = "Voyageur Inconnu";
        String numeroPasseportRecu = null;

        Intent intentRecu = getIntent();
        if (intentRecu != null) {
            verdict = intentRecu.getBooleanExtra(keyVerdict, false);
            String nomExtra = intentRecu.getStringExtra(keyNom);
            if (nomExtra != null && !nomExtra.trim().isEmpty()) {
                nomVoyageur = nomExtra.trim();
            }
            numeroPasseportRecu = intentRecu.getStringExtra(keyPassport);
        }

        // عرض رقم الجواز الزمني الموثق (المرحلة 09)
        if (txtNumeroPasseport != null) {
            if (numeroPasseportRecu != null && !numeroPasseportRecu.trim().isEmpty()) {
                txtNumeroPasseport.setText(getString(R.string.format_numero_passeport, numeroPasseportRecu));
            } else {
                txtNumeroPasseport.setText(getString(R.string.format_numero_passeport, "CHR-INCONNU"));
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

            // إعداد ختم الموافقة الرسمي (المرحلة 09)
            if (txtTamponVerdict != null) {
                txtTamponVerdict.setBackgroundResource(R.drawable.bg_stamp_approved);
                txtTamponVerdict.setText(R.string.stamp_verdict_approuve);
                txtTamponVerdict.setTextColor(ContextCompat.getColor(this, R.color.chronos_neon_green));
            }
        } else {
            // حالة الرفض: خلفية حمراء واضحة ورسالة "[Nom] est en état d'arrestation temporelle"
            layoutRoot.setBackgroundResource(R.drawable.bg_verdict_refused);
            imgIcon.setImageResource(R.drawable.ic_verdict_refused);
            txtTitle.setText(R.string.title_verdict_refuse);
            txtMessage.setText(nomVoyageur + " est en état d'arrestation temporelle");
            txtDesc.setText(R.string.desc_verdict_refuse);

            // إعداد ختم الرفض والاعتقال الرسمي (المرحلة 09)
            if (txtTamponVerdict != null) {
                txtTamponVerdict.setBackgroundResource(R.drawable.bg_stamp_refused);
                txtTamponVerdict.setText(R.string.stamp_verdict_refuse);
                txtTamponVerdict.setTextColor(ContextCompat.getColor(this, R.color.chronos_red_alert));
            }
        }

        // تأثير انقضاض الختم الجمركي على بطاقة القرار (المرحلة 09)
        if (txtTamponVerdict != null) {
            txtTamponVerdict.setScaleX(1.4f);
            txtTamponVerdict.setScaleY(1.4f);
            txtTamponVerdict.setAlpha(0.2f);
            txtTamponVerdict.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .alpha(1.0f)
                    .setDuration(380L)
                    .setInterpolator(new OvershootInterpolator(1.4f))
                    .start();
        }

        // إعداد زر "مسافر جديد" لبدء الانتقال الزمني عبر النفق قبل العودة للاستمارة (المرحلة 11)
        btnNouveauVoyageur.setOnClickListener(v -> {
            Intent returnTunnelIntent = new Intent(ResultActivity.this, TemporalReturnActivity.class);
            startActivity(returnTunnelIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
}
