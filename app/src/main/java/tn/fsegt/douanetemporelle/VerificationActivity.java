package tn.fsegt.douanetemporelle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط 2: فاحص التناقض الزمني والتحقق من الهوية - نظام كرونوس
 * المرحلة 05: خوارزمية كشف التناقض الزمني بالعدد الأولي وتجميد زر الموافقة تلقائياً
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
        TextView txtAlerteParadoxe = findViewById(R.id.txt_alerte_paradoxe_oa);

        // ربط زري اتخاذ القرار
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

            // حساب الفارق الزمني بدقة باستخدام نوع long لتفادي أي تجاوز عددي
            long differenceAnnees = (long) anneeDestinationRecue - (long) anneeNaissanceRecue;

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

            // ==============================================
            // تطبيق قاعدة التناقض الزمني (المرحلة 05)
            // ==============================================
            boolean isModificationDuPasse = "Modification du passé".equals(motifRecu);
            boolean isNombrePremier = isPrime(differenceAnnees);
            boolean paradoxeDetecte = isModificationDuPasse && isNombrePremier;

            if (paradoxeDetecte) {
                // تجميد زر الموافقة عند كشف مفارقة زمنية
                if (btnApprouver != null) {
                    btnApprouver.setEnabled(false);
                    btnApprouver.setAlpha(0.35f);
                }
                // الحفاظ على زر الرفض مفعلاً وجاهزاً
                if (btnRefuser != null) {
                    btnRefuser.setEnabled(true);
                    btnRefuser.setAlpha(1.0f);
                }
                // إظهار رسالة التنبيه والتفسير
                if (txtAlerteParadoxe != null) {
                    String msgAlerte = "Risque de paradoxe : l'écart de " + differenceAnnees
                            + " ans est un nombre premier. L'approbation est désactivée.";
                    txtAlerteParadoxe.setText(msgAlerte);
                    txtAlerteParadoxe.setVisibility(View.VISIBLE);
                }
            } else {
                // تمكين زر الموافقة في الحالات السليمة
                if (btnApprouver != null) {
                    btnApprouver.setEnabled(true);
                    btnApprouver.setAlpha(1.0f);
                }
                if (btnRefuser != null) {
                    btnRefuser.setEnabled(true);
                    btnRefuser.setAlpha(1.0f);
                }
                if (txtAlerteParadoxe != null) {
                    txtAlerteParadoxe.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * دالة ثابتة لفحص هل العدد أولي وفقاً لمتطلبات المشروع
     * العدد الأولي هو عدد صحيح موجب أكبر أو يساوي 2 ولا يقبل القسمة إلا على نفسه وعلى 1
     * الأعداد السالبة، 0، و 1 ليست أولية
     *
     * @param n الفارق الزمني المحسوب
     * @return true إذا كان العدد أولياً، false في غير ذلك
     */
    public static boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        if (n == 2 || n == 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        // التحقق من القواسم بتفادي التجاوز الحسابي عبر مقارنة i <= n / i
        for (long i = 5; i <= n / i; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
