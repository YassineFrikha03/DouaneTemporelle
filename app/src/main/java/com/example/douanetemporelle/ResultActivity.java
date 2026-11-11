package com.example.douanetemporelle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

/**
 * 1. النشاط الثالث : شاشة الحكم النهائي، محاكاة السفر ونمط حياة عصر الوجهة (ResultActivity)
 * تعرض نتيجة طلب السفر مع حركات سينمائية احترافية:
 * - في حال القبول: عداد سنوات متحرك، انبعاث البوابة الزمنية، وتعديل المظهر وفق نمط حياة سكان سنة الوجهة.
 * - في حال الرفض: حركة هبوط وسحق عنيفة لدرع التوقيف، اهتزاز شاشة الطوارئ، ووميض إنذار الحصار الزمني.
 */
public class ResultActivity extends AppCompatActivity {

    // 2. آخر 4 أرقام من بطاقة الطالب لاسترجاع المفاتيح ديناميكياً
    private static final String MATRICULE_ETUDIANT = "1234";

    // 3. تصريح عناصر واجهة المستخدم (تنتهي جميعها بـ _yf)
    private AnimatedBackgroundView viewBackgroundAnimeYf;
    private LinearLayout llRacineResultatYf;
    private TextView tvBadgeDouaneYf;
    private ImageView ivIconeVerdictYf;
    private TextView tvTitreVerdictYf;
    private ScrollView svDetailsResultatYf;
    private LinearLayout llCarteVerdictYf;
    private TextView tvMessagePersonnaliseYf;
    private LinearLayout llCompteurTemporelYf;
    private TextView tvLibelleSautYf;
    private TextView tvCompteurAnneeYf;
    private LinearLayout llEpoqueLifestyleYf;
    private TextView tvBadgeEpoqueYf;
    private TextView tvDescriptionStyleVieYf;
    private TextView tvDetailsVerdictYf;
    private MaterialButton btnNouveauVoyageurYf;

    // 4. متغيرات البيانات المستلمة من النشاط الثاني
    private String nomVoyageur;
    private boolean verdictApprouve;
    private int anneeNaissance;
    private int anneeDestination;
    private String motifVoyage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 5. ربط النشاط بملف التصميم activity_result.xml
        setContentView(R.layout.activity_result);

        // 6. ربط عناصر واجهة المستخدم بالمعرفات البرمجية
        initialiserVues();

        // 7. استخراج البيانات المنقولة عبر Intent من النشاط الثاني
        recupererDonneesIntent();

        // 8. تطبيق القرار، تكييف نمط حياة السكان، وتشغيل الحركات السينمائية
        appliquerVerdictEtAnimations();

        // 9. برمجة زر العودة لتنظيف المكدس والرجوع للنشاط الأول
        configurerBoutonRetour();
    }

    /**
     * 10. ربط عناصر الواجهة بالمعرفات الخاصة بالطالب
     */
    private void initialiserVues() {
        viewBackgroundAnimeYf = findViewById(R.id.view_background_anime_yf);
        llRacineResultatYf = findViewById(R.id.ll_racine_resultat_yf);
        tvBadgeDouaneYf = findViewById(R.id.tv_badge_douane_yf);
        ivIconeVerdictYf = findViewById(R.id.iv_icone_verdict_yf);
        tvTitreVerdictYf = findViewById(R.id.tv_titre_verdict_yf);
        svDetailsResultatYf = findViewById(R.id.sv_details_resultat_yf);
        llCarteVerdictYf = findViewById(R.id.ll_carte_verdict_yf);
        tvMessagePersonnaliseYf = findViewById(R.id.tv_message_personnalise_yf);
        llCompteurTemporelYf = findViewById(R.id.ll_compteur_temporel_yf);
        tvLibelleSautYf = findViewById(R.id.tv_libelle_saut_yf);
        tvCompteurAnneeYf = findViewById(R.id.tv_compteur_annee_yf);
        llEpoqueLifestyleYf = findViewById(R.id.ll_epoque_lifestyle_yf);
        tvBadgeEpoqueYf = findViewById(R.id.tv_badge_epoque_yf);
        tvDescriptionStyleVieYf = findViewById(R.id.tv_description_style_vie_yf);
        tvDetailsVerdictYf = findViewById(R.id.tv_details_verdict_yf);
        btnNouveauVoyageurYf = findViewById(R.id.btn_nouveau_voyageur_yf);
    }

    /**
     * 11. استخراج البيانات من Intent باستخدام المفاتيح المولدة ديناميكياً
     */
    private void recupererDonneesIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            nomVoyageur = intent.getStringExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT));
            verdictApprouve = intent.getBooleanExtra(IntentKeys.getVerdictKey(MATRICULE_ETUDIANT), false);
            anneeNaissance = intent.getIntExtra(IntentKeys.getAnneeNaissanceKey(MATRICULE_ETUDIANT), 1990);
            anneeDestination = intent.getIntExtra(IntentKeys.getAnneeDestinationKey(MATRICULE_ETUDIANT), 2050);
            motifVoyage = intent.getStringExtra(IntentKeys.getMotifKey(MATRICULE_ETUDIANT));
        }

        if (nomVoyageur == null || nomVoyageur.trim().isEmpty()) {
            nomVoyageur = "Voyageur";
        }
    }

    /**
     * 12. تطبيق الحكم: تغيير المظهر ونمط حياة العصر وتشغيل الحركات المناسبة
     */
    private void appliquerVerdictEtAnimations() {
        // تفعيل وضع الخلفية الحركية وفق نتيجة السفر وسنة الوجهة
        if (viewBackgroundAnimeYf != null) {
            viewBackgroundAnimeYf.configurerMode(verdictApprouve, anneeDestination);
        }

        // الحفاظ على شفافية الحاوية الرئيسية لإظهار الخلفية الحركية التفاعلية
        llRacineResultatYf.setBackgroundColor(Color.TRANSPARENT);

        if (verdictApprouve) {
            // حالة القبول: أيقونة الدرع المعتمد واللون الزمردي
            ivIconeVerdictYf.setImageResource(R.drawable.ic_verdict_approved);
            tvTitreVerdictYf.setText(R.string.titre_verdict_approuve);
            tvTitreVerdictYf.setTextColor(Color.parseColor("#10B981"));
            llCarteVerdictYf.setBackgroundResource(R.drawable.bg_card_verdict_approved);

            String message = getString(R.string.message_bon_voyage, nomVoyageur);
            tvMessagePersonnaliseYf.setText(message);
            tvDetailsVerdictYf.setText(R.string.details_verdict_approuve);

            // تفعيل بطاقة نمط حياة السكان وعداد السنوات
            llCompteurTemporelYf.setVisibility(View.VISIBLE);
            llEpoqueLifestyleYf.setVisibility(View.VISIBLE);

            // 13. تخصيص نمط حياة سكان العصر بناءً على سنة الوجهة
            configurerEpoqueEtStyleDeVie(anneeDestination);

            // 14. تشغيل حركات السفر الزمني وانفجار البوابة
            animerSautTemporel(anneeNaissance, anneeDestination);
        } else {
            // حالة الرفض: أيقونة درع الحظر والتوقيف الزمني
            ivIconeVerdictYf.setImageResource(R.drawable.ic_verdict_arrest);
            tvTitreVerdictYf.setText(R.string.titre_verdict_refuse);
            tvTitreVerdictYf.setTextColor(Color.parseColor("#EF4444"));
            llCarteVerdictYf.setBackgroundResource(R.drawable.bg_card_verdict_refused);

            String message = getString(R.string.message_arrestation, nomVoyageur);
            tvMessagePersonnaliseYf.setText(message);
            tvDetailsVerdictYf.setText(R.string.details_verdict_refuse);

            // إخفاء نمط الحياة لأن السفر مرفوض والمسافر محتجز في الحاضر
            llCompteurTemporelYf.setVisibility(View.GONE);
            llEpoqueLifestyleYf.setVisibility(View.GONE);

            // 15. تشغيل حركة السقوط الصاعق، هزة الشاشة ووميض الإنذار
            animerArrestationTemporelle();
        }
    }

    /**
     * 16. تحديد نمط حياة وحضارة السكان في سنة الوجهة بدقة تاريخية لتخصيص الثيم
     */
    private void configurerEpoqueEtStyleDeVie(int annee) {
        String nomEpoque;
        String styleDeVie;
        int couleurAccent;

        if (annee < 500) {
            nomEpoque = "ÉPOQUE ANTIQUE • CIVILISATIONS ANCIENNES";
            styleDeVie = "Mode de vie : Cités de pierre, philosophie naissante, papyrus, artisanat du bronze, légionnaires romains et navigation côtière.";
            couleurAccent = Color.parseColor("#F59E0B"); // ذهبي كلاسيكي
        } else if (annee < 1492) {
            nomEpoque = "ÉPOQUE MÉDIÉVALE • FÉODALITÉ ET ROYAUMES";
            styleDeVie = "Mode de vie : Châteaux forts, chevalerie féodale, serfs travaillant la terre, épidémies de peste, guildes artisanales et cathédrales.";
            couleurAccent = Color.parseColor("#EAB308"); // كهرماني قرووسطي
        } else if (annee < 1789) {
            nomEpoque = "RENAISSANCE & SIÈCLE DES LUMIÈRES";
            styleDeVie = "Mode de vie : Grandes explorations maritimes (caravelles), imprimerie de Gutenberg, essor des sciences, monarchies et cours royales.";
            couleurAccent = Color.parseColor("#FB923C"); // برتقالي نحاسي
        } else if (annee < 1914) {
            nomEpoque = "RÉVOLUTION INDUSTRIELLE & ÈRE VICTORIENNE";
            styleDeVie = "Mode de vie : Mines de charbon, essor du chemin de fer, locomotives à vapeur, travail en usines, éclairage au gaz et exode rural.";
            couleurAccent = Color.parseColor("#D97706"); // برونزي صناعي
        } else if (annee <= 1945) {
            nomEpoque = "GUERRES MONDIALES & ENTRE-DEUX-GUERRES (1914-1945)";
            styleDeVie = "Mode de vie : Conflits armés mondiaux, tranchées, couvre-feux stricts, rationnement alimentaire sévère, reconstruction d'après-guerre et usines d'armement.";
            couleurAccent = Color.parseColor("#EF4444"); // أحمر حربي إنذاري
        } else if (annee <= 1989) {
            nomEpoque = "GUERRE FROIDE & SOCIÉTÉ D'APRÈS-GUERRE";
            styleDeVie = "Mode de vie : Rideau de fer, tension géopolitique Est-Ouest, menace nucléaire, premiers pas sur la Lune, essor de la télévision et de l'automobile.";
            couleurAccent = Color.parseColor("#38BDF8"); // أزرق حرب باردة
        } else if (annee <= 2026) {
            nomEpoque = "ÈRE NUMÉRIQUE CONTEMPORAINE";
            styleDeVie = "Mode de vie : Internet mondial, ordinateurs personnels, téléphonie mobile, réseaux sociaux, transition énergétique et mondialisation instantanée.";
            couleurAccent = Color.parseColor("#10B981"); // زمردي رقمي
        } else if (annee <= 2150) {
            nomEpoque = "ÈRE CYBERNÉTIQUE • MÉGAPOLES IA ET VILLES FLOTTANTES";
            styleDeVie = "Mode de vie : Mégapoles suspendues, intelligence artificielle omniprésente, véhicules à lévitation magnétique et énergies à fusion propre.";
            couleurAccent = Color.parseColor("#00E5FF"); // سيان نيون
        } else {
            nomEpoque = "CIVILISATION INTERSTELLAIRE • ÈRE COSMIQUE QUANTIQUE";
            styleDeVie = "Mode de vie : Colonies orbitales lointaines, voyages supraluminiques, téléportation quantique, Dyson-sphères et terraformation galactique.";
            couleurAccent = Color.parseColor("#A855F7"); // بنفسجي كوني
        }

        tvBadgeEpoqueYf.setText("DESTINATION : ANNEE " + annee + " • " + nomEpoque);
        tvBadgeEpoqueYf.setTextColor(couleurAccent);
        tvDescriptionStyleVieYf.setText(styleDeVie);
        tvCompteurAnneeYf.setTextColor(couleurAccent);
    }

    /**
     * 17. حركات السفر الزمني: انبعاث البوابة الزمنية وتدفق عداد السنوات السريع
     */
    private void animerSautTemporel(int depart, int arrivee) {
        // حركة انفجار وتوسع درع القبول من نقطة الصفر مع ارتداد ديناميكي
        ivIconeVerdictYf.setScaleX(0.2f);
        ivIconeVerdictYf.setScaleY(0.2f);
        ivIconeVerdictYf.setAlpha(0.0f);
        ivIconeVerdictYf.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(900)
                .setInterpolator(new OvershootInterpolator(1.4f))
                .start();

        // حركة دوران وتوهج للدرع لمحاكاة عبور الدوامة
        ivIconeVerdictYf.setRotation(-30f);
        ivIconeVerdictYf.animate()
                .rotation(0f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // حركة عداد السنوات الرقمي السريع لمحاكاة الانتقال عبر الزمن
        ValueAnimator compteurAnim = ValueAnimator.ofInt(depart, arrivee);
        compteurAnim.setDuration(1200);
        compteurAnim.setInterpolator(new DecelerateInterpolator());
        compteurAnim.addUpdateListener(animation -> {
            int val = (int) animation.getAnimatedValue();
            tvCompteurAnneeYf.setText(String.valueOf(val));
        });
        compteurAnim.start();

        // حركة انزلاق وظهور بطاقة العصر ونمط الحياة
        llCarteVerdictYf.setTranslationY(80f);
        llCarteVerdictYf.setAlpha(0.0f);
        llCarteVerdictYf.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setDuration(800)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * 18. حركات التوقيف والحظر: سحق درع الحظر، هزة الشاشة ووميض الإنذار
     */
    private void animerArrestationTemporelle() {
        // حركة هبوط وسحق عنيفة لدرع التوقيف والحظر مع ارتداد Bounce
        ivIconeVerdictYf.setTranslationY(-250f);
        ivIconeVerdictYf.setAlpha(0.0f);
        ivIconeVerdictYf.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setDuration(800)
                .setInterpolator(new BounceInterpolator())
                .start();

        // هزة ارتدادية لكامل شاشة التطبيق تحاكي صدمة الحصار الزمني
        ObjectAnimator secousse = ObjectAnimator.ofFloat(llRacineResultatYf, "translationX", 0f, 16f, -16f, 12f, -12f, 6f, 0f);
        secousse.setDuration(450);
        secousse.setStartDelay(350);
        secousse.start();

        // وميض إنذار الطوارئ الأحمر المستمر على البطاقة
        ObjectAnimator alarmeClignotante = ObjectAnimator.ofFloat(llCarteVerdictYf, "alpha", 0.72f, 1.0f);
        alarmeClignotante.setDuration(450);
        alarmeClignotante.setRepeatCount(ValueAnimator.INFINITE);
        alarmeClignotante.setRepeatMode(ValueAnimator.REVERSE);
        alarmeClignotante.start();

        // حركة انزلاق بطاقة الحظر
        llCarteVerdictYf.setTranslationY(60f);
        llCarteVerdictYf.setAlpha(0.0f);
        llCarteVerdictYf.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setDuration(700)
                .setStartDelay(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * 19. إعداد زر "مسافر جديد" للرجوع إلى MainActivity مع إغلاق الأنشطة السابقة
     */
    private void configurerBoutonRetour() {
        btnNouveauVoyageurYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRetour = new Intent(ResultActivity.this, MainActivity.class);
                intentRetour.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentRetour);
                finish();
            }
        });
    }
}
