package tn.fsegt.douanetemporelle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.UUID;

/**
 * النشاط 2: فاحص التناقض الزمني والتحقق من الهوية - نظام كرونوس
 * المرحلة 05: خوارزمية كشف التناقض الزمني بالعدد الأولي وتجميد زر الموافقة تلقائياً
 * المرحلة 06: ربط زري الموافقة والرفض بشاشة النتيجة النهائية ResultActivity
 * المرحلة 09: التأثيرات الابتكارية الموضعية (توليد رقم الجواز، مسح ضوئي، واستجابة لمسية)
 */
public class VerificationActivity extends AppCompatActivity {

    /**
     * مفتاح حفظ واسترجاع رقم الجواز الزمني عبر دورة حياة النشاط
     */
    private static final String STATE_PASSPORT_NUMBER_OA = "state_passport_number_oa";

    /**
     * رقم جواز السفر الزمني المولد محلياً
     */
    private String numeroPasseport;

    /**
     * محرك الحركة لشعاع المسح الضوئي لضمان إيقافه عند إتلاف النشاط
     */
    private ObjectAnimator scanAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تحميل واجهة جواز السفر بالهيكل الأفقي الصارم (0.3 صورة يساراً و 0.7 نصوص يميناً)
        setContentView(R.layout.activity_verification);

        // استعادة رقم الجواز الزمني عند تغيير الاتجاه أو إنشاؤه لأول مرة (المرحلة 09)
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PASSPORT_NUMBER_OA)) {
            numeroPasseport = savedInstanceState.getString(STATE_PASSPORT_NUMBER_OA);
        } else {
            numeroPasseport = generatePassportNumber();
        }

        // ربط عناصر عرض بيانات جواز السفر الزمني
        TextView txtNumeroPasseport = findViewById(R.id.txt_numero_passeport_verif_oa);
        TextView txtValNom = findViewById(R.id.txt_val_nom_oa);
        TextView txtValAge = findViewById(R.id.txt_val_age_oa);
        TextView txtValTrajectoire = findViewById(R.id.txt_val_trajectoire_oa);
        TextView txtValMotif = findViewById(R.id.txt_val_motif_oa);
        TextView txtAlerteParadoxe = findViewById(R.id.txt_alerte_paradoxe_oa);

        // ربط عناصر تأثير المسح الضوئي الزمني (المرحلة 09)
        View viewScanLine = findViewById(R.id.view_scan_line_oa);
        RelativeLayout layoutPassportContent = findViewById(R.id.layout_passport_content_oa);

        // ربط زري اتخاذ القرار
        Button btnApprouver = findViewById(R.id.btn_approuver_oa);
        Button btnRefuser = findViewById(R.id.btn_refuser_oa);

        // عرض رقم الجواز الزمني بصيغة كرونوس الرسمية
        if (txtNumeroPasseport != null && numeroPasseport != null) {
            txtNumeroPasseport.setText(getString(R.string.format_numero_passeport, numeroPasseport));
        }

        // تفعيل حركة المسح الضوئي الزمني لمرة واحدة دون تعطيل المستخدم (المرحلة 09)
        if (viewScanLine != null && layoutPassportContent != null) {
            layoutPassportContent.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                int height = layoutPassportContent.getHeight();
                if (height <= 0) {
                    height = 900;
                }
                viewScanLine.setVisibility(View.VISIBLE);
                scanAnimator = ObjectAnimator.ofFloat(viewScanLine, "translationY", 0f, (float) height);
                scanAnimator.setDuration(1200L);
                scanAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                scanAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (viewScanLine != null) {
                            viewScanLine.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                scanAnimator.start();
            });
        }

        // تطبيق تأثير كشف جواز السفر بسلاسة وهدوء (المرحلة 10)
        if (layoutPassportContent != null && android.animation.ValueAnimator.areAnimatorsEnabled()) {
            layoutPassportContent.setAlpha(0.2f);
            layoutPassportContent.setTranslationY(24f);
            layoutPassportContent.animate()
                    .alpha(1.0f)
                    .translationY(0f)
                    .setDuration(450L)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

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

            final String nomFinal = (nomRecu != null && !nomRecu.trim().isEmpty()) ? nomRecu.trim() : "Voyageur non identifié";

            // حساب الفارق الزمني بدقة باستخدام نوع long لتفادي أي تجاوز عددي
            long differenceAnnees = (long) anneeDestinationRecue - (long) anneeNaissanceRecue;

            // عرض اسم المسافر
            if (txtValNom != null) {
                txtValNom.setText(nomFinal);
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

            // ==============================================
            // ربط زري اتخاذ القرار بشاشة النتيجة (المرحلة 06 و 09)
            // ==============================================
            if (btnApprouver != null) {
                btnApprouver.setOnClickListener(v -> {
                    // توليد رجفة لمسية خفيفة عند النقر (المرحلة 09)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    naviguerVersResultat(true, nomFinal, numeroPasseport);
                });
            }

            if (btnRefuser != null) {
                btnRefuser.setOnClickListener(v -> {
                    // توليد رجفة لمسية خفيفة عند النقر (المرحلة 09)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    naviguerVersResultat(false, nomFinal, numeroPasseport);
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // حفظ رقم الجواز الزمني لضمان ثباته عند تدوير الشاشة (المرحلة 09)
        outState.putString(STATE_PASSPORT_NUMBER_OA, numeroPasseport);
    }

    @Override
    protected void onDestroy() {
        // إيقاف حركة المسح الضوئي لتفادي أي تسريب للذاكرة عند إغلاق الشاشة
        if (scanAnimator != null) {
            scanAnimator.cancel();
            scanAnimator = null;
        }
        super.onDestroy();
    }

    /**
     * الانتقال إلى شاشة النتيجة النهائية ResultActivity مع تمرير القرار واسم المسافر ورقم الجواز
     *
     * @param verdict true للموافقة على السفر و false للرفض
     * @param nom اسم المسافر
     * @param passportNum رقم الجواز الزمني
     */
    private void naviguerVersResultat(boolean verdict, String nom, String passportNum) {
        Intent resultIntent = new Intent(VerificationActivity.this, ResultActivity.class);
        String keyVerdict = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_VERDICT_FINAL);
        String keyNom = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR);
        String keyPassport = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NUMERO_PASSEPORT);

        resultIntent.putExtra(keyVerdict, verdict);
        resultIntent.putExtra(keyNom, nom);
        resultIntent.putExtra(keyPassport, passportNum);
        startActivity(resultIntent);
    }

    /**
     * توليد معرف جواز زمني فريد محلياً بصيغة CHR-XXXXXXXX (8 خانات سداسية عشرية كبيرة)
     *
     * @return رقم الجواز الزمني
     */
    public static String generatePassportNumber() {
        String uuidClean = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.US);
        return "CHR-" + uuidClean.substring(0, 8);
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
