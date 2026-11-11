package tn.fsegt.douanetemporelle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * النشاط الانتقالي: الرحلة السينمائية عبر الزمن قبل استعراض جواز السفر - كرونوس
 * المرحلة 10: تسلسل بصري تمهيدي قصير وأنيق "A JOURNEY THROUGH TIME"
 */
public class TemporalJourneyIntroActivity extends AppCompatActivity {

    /**
     * مؤشر أمان لمنع التوجيه المتكرر أو المزدوج نحو جواز السفر
     */
    private boolean isNavigating = false;

    /**
     * معالج الجدولة الزمنية لتسلسل أطوار الحركة
     */
    private final Handler introHandler = new Handler(Looper.getMainLooper());

    /**
     * قائمة محركات الحركة النشطة لإلغائها النظيف عند مغادرة النشاط
     */
    private final List<Animator> activeAnimators = new ArrayList<>();

    // بيانات المسافر المستلمة من الاستمارة
    private String nomVoyageur;
    private int anneeNaissance;
    private int anneeDestination;
    private String motifVoyage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_journey_intro);

        // استخراج البيانات المستلمة عبر القصد الصريح باستخدام المفاتيح الديناميكية ورقم الطالب 1688
        Intent intentRecu = getIntent();
        if (intentRecu != null) {
            String keyNom = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR);
            String keyNaissance = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_NAISSANCE);
            String keyDestination = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_DESTINATION);
            String keyMotif = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_MOTIF_VOYAGE);

            nomVoyageur = intentRecu.getStringExtra(keyNom);
            anneeNaissance = intentRecu.getIntExtra(keyNaissance, 0);
            anneeDestination = intentRecu.getIntExtra(keyDestination, 0);
            motifVoyage = intentRecu.getStringExtra(keyMotif);
        }

        // ربط عناصر الواجهة ذات المعرفات المنتهية بـ _oa
        View viewPulse = findViewById(R.id.view_temporal_pulse_oa);
        ImageView imgDial = findViewById(R.id.img_temporal_dial_oa);
        ImageView imgNeedle = findViewById(R.id.img_temporal_needle_oa);
        TextView txtTitle = findViewById(R.id.txt_intro_title_oa);
        TextView txtPhilosophy = findViewById(R.id.txt_intro_philosophy_oa);
        TextView txtCallout = findViewById(R.id.txt_intro_callout_oa);
        Button btnSkip = findViewById(R.id.btn_skip_intro_oa);

        // تفعيل زر تخطي المقدمة فوراً عند النقر
        if (btnSkip != null) {
            btnSkip.setOnClickListener(v -> naviguerVersPasseport());
        }

        // فحص إمكانية تشغيل الحركات في النظام؛ إذا كانت معطلة يتم التوجيه الفوري
        if (!ValueAnimator.areAnimatorsEnabled()) {
            naviguerVersPasseport();
            return;
        }

        // بدء السلسلة السينمائية للرحلة الزمنية (المدة الإجمالية ~3.0 ثانية)
        lancerSequenceCinematique(viewPulse, imgDial, imgNeedle, txtTitle, txtPhilosophy, txtCallout);
    }

    /**
     * تشغيل الأطوار الأربعة للحركة البصرية المتدرجة
     */
    private void lancerSequenceCinematique(View viewPulse, ImageView imgDial, ImageView imgNeedle,
                                           TextView txtTitle, TextView txtPhilosophy, TextView txtCallout) {

        // ==============================================
        // الطور 1: ظهور القرص والعقرب والعنوان الرئيسي
        // ==============================================
        if (imgDial != null && imgNeedle != null) {
            imgDial.setScaleX(0.7f);
            imgDial.setScaleY(0.7f);
            imgNeedle.setScaleX(0.7f);
            imgNeedle.setScaleY(0.7f);

            PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.7f, 1.0f);
            PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.7f, 1.0f);
            PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.0f, 1.0f);

            ObjectAnimator animDialAppear = ObjectAnimator.ofPropertyValuesHolder(imgDial, pvhScaleX, pvhScaleY, pvhAlpha);
            animDialAppear.setDuration(700L);
            animDialAppear.setInterpolator(new DecelerateInterpolator());
            activeAnimators.add(animDialAppear);
            animDialAppear.start();

            ObjectAnimator animNeedleAppear = ObjectAnimator.ofPropertyValuesHolder(imgNeedle, pvhScaleX, pvhScaleY, pvhAlpha);
            animNeedleAppear.setDuration(700L);
            animNeedleAppear.setInterpolator(new DecelerateInterpolator());
            activeAnimators.add(animNeedleAppear);
            animNeedleAppear.start();
        }

        if (txtTitle != null) {
            txtTitle.setTranslationY(25f);
            ObjectAnimator animTitle = ObjectAnimator.ofPropertyValuesHolder(
                    txtTitle,
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0.0f, 1.0f),
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 25f, 0f)
            );
            animTitle.setDuration(600L);
            activeAnimators.add(animTitle);
            animTitle.start();
        }

        // ==============================================
        // الطور 2: دوران عقرب الزمن وتمدد هالة النبض (بعد 400 ميلي ثانية)
        // ==============================================
        introHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed() || isNavigating) {
                return;
            }

            // دوران العقرب دورتين كاملتين (720 درجة) بمحاكاة السفر عبر الزمن
            if (imgNeedle != null) {
                ObjectAnimator animRotation = ObjectAnimator.ofFloat(imgNeedle, View.ROTATION, 0f, 720f);
                animRotation.setDuration(1600L);
                animRotation.setInterpolator(new AccelerateDecelerateInterpolator());
                activeAnimators.add(animRotation);
                animRotation.start();
            }

            // موجة النبض المتوهجة
            if (viewPulse != null) {
                viewPulse.setVisibility(View.VISIBLE);
                viewPulse.setScaleX(0.8f);
                viewPulse.setScaleY(0.8f);
                viewPulse.setAlpha(0.6f);

                ObjectAnimator animPulse = ObjectAnimator.ofPropertyValuesHolder(
                        viewPulse,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 1.45f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f, 1.45f),
                        PropertyValuesHolder.ofFloat(View.ALPHA, 0.6f, 0.0f)
                );
                animPulse.setDuration(1200L);
                animPulse.setInterpolator(new DecelerateInterpolator());
                activeAnimators.add(animPulse);
                animPulse.start();
            }
        }, 400L);

        // ==============================================
        // الطور 3: الظهور المتدرج للعبارات الفلسفية (بعد 800 و 1500 ميلي ثانية)
        // ==============================================
        introHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed() || isNavigating) {
                return;
            }
            if (txtPhilosophy != null) {
                txtPhilosophy.setTranslationY(20f);
                ObjectAnimator animPhil = ObjectAnimator.ofPropertyValuesHolder(
                        txtPhilosophy,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 20f, 0f)
                );
                animPhil.setDuration(600L);
                activeAnimators.add(animPhil);
                animPhil.start();
            }
        }, 800L);

        introHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed() || isNavigating) {
                return;
            }
            if (txtCallout != null) {
                txtCallout.setTranslationY(18f);
                ObjectAnimator animCallout = ObjectAnimator.ofPropertyValuesHolder(
                        txtCallout,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 18f, 0f)
                );
                animCallout.setDuration(550L);
                activeAnimators.add(animCallout);
                animCallout.start();
            }
        }, 1500L);

        // ==============================================
        // الطور 4: الانتقال التلقائي السلس إلى شاشة جواز السفر (عند 2900 ميلي ثانية)
        // ==============================================
        introHandler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed() && !isNavigating) {
                naviguerVersPasseport();
            }
        }, 2900L);
    }

    /**
     * الانتقال المباشر والآمن إلى VerificationActivity مع نقل البيانات الأصلية
     */
    private synchronized void naviguerVersPasseport() {
        if (isNavigating || isFinishing() || isDestroyed()) {
            return;
        }
        isNavigating = true;

        // إيقاف كافة الحركات والجدولة النشطة
        arreterAnimations();

        Intent intentPasseport = new Intent(TemporalJourneyIntroActivity.this, VerificationActivity.class);

        // الحفاظ التام على المفاتيح والقيم المستلمة دون أي تعديل أو بيانات وهمية
        String keyNom = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR);
        String keyNaissance = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_NAISSANCE);
        String keyDestination = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_DESTINATION);
        String keyMotif = ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_MOTIF_VOYAGE);

        intentPasseport.putExtra(keyNom, nomVoyageur);
        intentPasseport.putExtra(keyNaissance, anneeNaissance);
        intentPasseport.putExtra(keyDestination, anneeDestination);
        intentPasseport.putExtra(keyMotif, motifVoyage);

        startActivity(intentPasseport);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // إنهاء النشاط التمهيدي حتى لا يعود إليه المستخدم بالزر الخلفي
        finish();
    }

    /**
     * إيقاف جميع الحركات وإفراغ قائمة الانتظار لمنع أي تسريب للموارد
     */
    private void arreterAnimations() {
        introHandler.removeCallbacksAndMessages(null);
        for (Animator anim : activeAnimators) {
            if (anim != null && anim.isRunning()) {
                anim.cancel();
            }
        }
        activeAnimators.clear();
    }

    @Override
    protected void onDestroy() {
        arreterAnimations();
        super.onDestroy();
    }
}
