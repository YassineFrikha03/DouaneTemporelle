package com.example.douanetemporelle;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

/**
 * 1. النشاط الثاني : فاحص التناقض الزمني والتحقق الإجباري الحقيقي من الوجه (VerificationActivity)
 * شرط صارم: يجب أن ترصد الخوارزمية وجهاً بشرياً حقيقياً (FaceDetector)، وفي حال عدم وجود وجه
 * يتم رفض كافة أنواع السفر الزمنية (Tourisme, Modification, Fuite) تلقائياً ويُقفل زر الموافقة.
 */
public class VerificationActivity extends AppCompatActivity {

    // 2. كود طلب إذن الكاميرا
    private static final int CODE_PERMISSION_CAMERA = 101;

    // 3. آخر 4 أرقام من بطاقة الطالب لإنشاء مفاتيح Intent ديناميكياً
    private static final String MATRICULE_ETUDIANT = "1234";

    // 4. تصريح عناصر الواجهة الرسومية (تنتهي جميعها بـ _yf)
    private ImageView ivHologrammePasseportYf;
    private TextView tvValeurNomYf;
    private TextView tvValeurAgeYf;
    private TextView tvValeurCoordonneesYf;
    private TextView tvValeurMotifYf;
    private TextView tvAlerteParadoxeYf;
    private TextView tvValeurScanFacialYf;
    private MaterialButton btnScannerCameraYf;
    private ImageView ivCodeBarreYf;
    private TextView tvCodeBarreNumeroYf;
    private MaterialButton btnApprouverYf;
    private MaterialButton btnRefuserYf;

    // 5. مشغل النشاط لالتقاط الصورة عبر الكاميرا والتحقق من الوجه
    private ActivityResultLauncher<Intent> lanceurCameraYf;

    // 6. حالة الفحص البيومتري: هل تم رصد وجه بشري حقيقي مؤكد؟
    private boolean visageHumainValide = false;

    // 7. متغيرات تخزين البيانات المستلمة
    private String nomVoyageur;
    private int anneeNaissance;
    private int anneeDestination;
    private String motifVoyage;
    private int ageTemporel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 8. ربط النشاط بملف التصميم activity_verification.xml
        setContentView(R.layout.activity_verification);

        // 9. إعداد مشغل الكاميرا مع فحص التعرف على الوجه وشرط منع الروبوتات
        initialiserLanceurCamera();

        // 10. ربط عناصر واجهة المستخدم بالمعرفات البرمجية
        initialiserVues();

        // 11. استخراج البيانات المنقولة عبر Intent الصريح من MainActivity
        recupererDonneesIntent();

        // 12. معالجة البيانات، توليد الباركود، وقفل القفزة حتى يتم فحص الوجه
        traiterEtAfficherDonnees();

        // 13. تشغيل تأثير المسح الضوئي البيومتري للوجه
        animerScannerFacial();

        // 14. تفعيل مستمعات الأحداث للنقر على أزرار القرار والكاميرا
        configurerActions();
    }

    /**
     * 15. إعداد مشغل الكاميرا لمعالجة الصورة والتحقق الإلزامي من وجود وجه بشري
     */
    private void initialiserLanceurCamera() {
        lanceurCameraYf = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null && extras.get("data") != null) {
                            Bitmap photoCapturee = (Bitmap) extras.get("data");

                            // فحص وجود الوجه الصارم عبر FaceDetector حصراً
                            boolean visageDetecte = detecterVisage(photoCapturee);

                            if (visageDetecte) {
                                // 16. حالة النجاح المؤكد: تم العثور على وجه إنساني حقيقي
                                visageHumainValide = true;

                                // رسم إطار التصويب البيومتري على الوجه المكتشف
                                Bitmap photoAvecCiblage = dessinerCiblageVisage(photoCapturee);
                                ivHologrammePasseportYf.setImageBitmap(photoAvecCiblage);
                                ivHologrammePasseportYf.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                tvValeurScanFacialYf.setText(R.string.statut_anti_robot_valide);
                                tvValeurScanFacialYf.setTextColor(getColor(R.color.time_green));

                                // إعادة تقييم شروط السفر
                                evaluerConditionsVoyage();

                                Toast.makeText(this, "Visage humain detecte avec succes : voyageur confirme.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 17. حالة الفشل الصارم: لا يوجد أي وجه في الصورة -> رفض فوري لكافة الأسفار
                                ivHologrammePasseportYf.setImageBitmap(photoCapturee);
                                ivHologrammePasseportYf.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                appliquerRefusAbsoluSansVisage();
                            }
                            return;
                        }
                    }

                    // في حال إلغاء الكاميرا دون التقاط صورة وجه
                    appliquerRefusAbsoluSansVisage();
                }
        );
    }

    /**
     * 18. دالة الفحص البيومتري الصارم: تشترط وجود ملامح وجه بشري حقيقي حصراً
     * تعتمد فقط على خوارزمية FaceDetector الرسمية، وإذا لم تجد وجهاً تُرجع false حتماً
     */
    private boolean detecterVisage(Bitmap bitmapOriginal) {
        if (bitmapOriginal == null) {
            return false;
        }

        // اختبار أول بدقة 400 بكسل للكشف عن الوجه
        if (analyserVisage(bitmapOriginal, 400)) {
            return true;
        }

        // اختبار ثان بدقة 300 بكسل لتغطية الوجوه القريبة أو البعيدة
        if (analyserVisage(bitmapOriginal, 300)) {
            return true;
        }

        // اختبار ثالث بالدقة الأصلية إذا كانت كافية
        if (bitmapOriginal.getWidth() >= 64 && analyserVisage(bitmapOriginal, bitmapOriginal.getWidth())) {
            return true;
        }

        // في حال عدم وجود أي وجه بشري: يتم الرفض القاطع
        return false;
    }

    /**
     * 19. تشغيل خوارزمية FaceDetector على دقة محددة للتأكد من ملامح العينين
     */
    private boolean analyserVisage(Bitmap source, int largeurCible) {
        try {
            int wSource = source.getWidth();
            int hSource = source.getHeight();
            if (wSource <= 0 || hSource <= 0) return false;

            int hauteurCible = (hSource * largeurCible) / wSource;
            if (largeurCible % 2 != 0) largeurCible++;
            if (hauteurCible % 2 != 0) hauteurCible++;

            Bitmap bitmapRedim = Bitmap.createScaledBitmap(source, largeurCible, hauteurCible, true);
            Bitmap bitmap565 = bitmapRedim.copy(Bitmap.Config.RGB_565, false);

            FaceDetector detector = new FaceDetector(largeurCible, hauteurCible, 1);
            FaceDetector.Face[] visages = new FaceDetector.Face[1];
            int nombre = detector.findFaces(bitmap565, visages);

            return (nombre > 0 && visages[0] != null && visages[0].confidence() >= 0.30f);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 20. رسم إطار بيومتري دائري بلون سيان حول الوجه المكتشف لإثبات رصده بدقة
     */
    private Bitmap dessinerCiblageVisage(Bitmap sourceOriginal) {
        try {
            int w = sourceOriginal.getWidth();
            int h = sourceOriginal.getHeight();
            if (w % 2 != 0) w--;
            if (h % 2 != 0) h--;
            if (w <= 0 || h <= 0) return sourceOriginal;

            Bitmap b565 = Bitmap.createScaledBitmap(sourceOriginal, w, h, true).copy(Bitmap.Config.RGB_565, false);
            FaceDetector detector = new FaceDetector(w, h, 1);
            FaceDetector.Face[] visages = new FaceDetector.Face[1];
            int count = detector.findFaces(b565, visages);

            if (count > 0 && visages[0] != null) {
                Bitmap resultat = sourceOriginal.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(resultat);
                PointF pointMilieu = new PointF();
                visages[0].getMidPoint(pointMilieu);
                float distanceYeux = visages[0].eyesDistance();

                Paint paintCercle = new Paint();
                paintCercle.setColor(Color.parseColor("#00E5FF"));
                paintCercle.setStyle(Paint.Style.STROKE);
                paintCercle.setStrokeWidth(3.5f);
                paintCercle.setAntiAlias(true);

                // رسم دائرة الهدف البيومتري حول الوجه المكتشف
                canvas.drawCircle(pointMilieu.x, pointMilieu.y, distanceYeux * 1.25f, paintCercle);

                // رسم علامة الصليب في المركز
                canvas.drawLine(pointMilieu.x - 10, pointMilieu.y, pointMilieu.x + 10, pointMilieu.y, paintCercle);
                canvas.drawLine(pointMilieu.x, pointMilieu.y - 10, pointMilieu.x, pointMilieu.y + 10, paintCercle);

                return resultat;
            }
        } catch (Exception ignored) {
        }
        return sourceOriginal;
    }

    /**
     * 21. تطبيق الرفض الشامل لكافة أنواع السفر عند غياب الوجه
     */
    private void appliquerRefusAbsoluSansVisage() {
        visageHumainValide = false;

        // تعطيل زر الموافقة بالكامل وتخفيض الشفافية
        btnApprouverYf.setEnabled(false);
        btnApprouverYf.setAlpha(0.35f);

        // إظهار نداء الخطر والتنبيه إلى رفض جميع أنواع الرحلات للروبوتات
        tvValeurScanFacialYf.setText(R.string.statut_anti_robot_echec);
        tvValeurScanFacialYf.setTextColor(getColor(R.color.time_red));

        tvAlerteParadoxeYf.setText(R.string.alerte_anti_robot_refus_total);
        tvAlerteParadoxeYf.setTextColor(getColor(R.color.time_red));
        tvAlerteParadoxeYf.setBackgroundResource(R.drawable.bg_paradox_warning);

        Toast.makeText(this, "Alerte de securite : Aucun visage detecte ! Tous les voyages sont refuses.", Toast.LENGTH_LONG).show();
    }

    /**
     * 22. دالة ربط عناصر الواجهة بالمعرفات الخاصة بالطالب
     */
    private void initialiserVues() {
        ivHologrammePasseportYf = findViewById(R.id.iv_hologramme_passeport_yf);
        tvValeurNomYf = findViewById(R.id.tv_valeur_nom_yf);
        tvValeurAgeYf = findViewById(R.id.tv_valeur_age_yf);
        tvValeurCoordonneesYf = findViewById(R.id.tv_valeur_coordonnees_yf);
        tvValeurMotifYf = findViewById(R.id.tv_valeur_motif_yf);
        tvAlerteParadoxeYf = findViewById(R.id.tv_alerte_paradoxe_yf);
        tvValeurScanFacialYf = findViewById(R.id.tv_valeur_scan_facial_yf);
        btnScannerCameraYf = findViewById(R.id.btn_scanner_camera_yf);
        ivCodeBarreYf = findViewById(R.id.iv_code_barre_yf);
        tvCodeBarreNumeroYf = findViewById(R.id.tv_code_barre_numero_yf);
        btnApprouverYf = findViewById(R.id.btn_approuver_yf);
        btnRefuserYf = findViewById(R.id.btn_refuser_yf);
    }

    /**
     * 23. استخراج البيانات المنقولة باستخدام المفاتيح الديناميكية
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
     * 24. دالة حسابية للتحقق مما إذا كان العدد أولياً (Nombre Premier)
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
     * 25. معالجة البيانات، توليد الباركود وتطبيق الرفض المبدئي لحين التحقق من الوجه
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

        // تطبيق شرط الإلزام: في البداية لا يوجد وجه متحقق منه -> رفض كل الأسفار حتى يثبت العكس
        visageHumainValide = false;
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
            tvAlerteParadoxeYf.setText(R.string.alerte_sans_visage);
        }
    }

    /**
     * 26. تقييم الشروط المشتركة بعد تأكيد وجود الوجه البشري
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
     * 27. وميض المسح الليزري للوجه
     */
    private void animerScannerFacial() {
        ObjectAnimator animateurScan = ObjectAnimator.ofFloat(ivHologrammePasseportYf, "alpha", 0.55f, 1.0f);
        animateurScan.setDuration(450);
        animateurScan.setRepeatCount(3);
        animateurScan.setRepeatMode(ValueAnimator.REVERSE);
        animateurScan.start();
    }

    /**
     * 28. التحقق من صلاحية الكاميرا وفتحها
     */
    private void verifierEtLancerCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE_PERMISSION_CAMERA);
        } else {
            ouvrirAppareilPhoto();
        }
    }

    /**
     * 29. فتح تطبيق الكاميرا لالتقاط صورة الوجه
     */
    private void ouvrirAppareilPhoto() {
        Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            lanceurCameraYf.launch(intentPhoto);
        } catch (Exception e) {
            Toast.makeText(this, "Impossible d\'acceder a la camera sur cet appareil", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ouvrirAppareilPhoto();
        } else {
            appliquerRefusAbsoluSansVisage();
        }
    }

    /**
     * 30. إعداد أحداث النقر للأزرار مع فرض شرط الوجه لمنع الموافقة للروبوتات
     */
    private void configurerActions() {
        btnScannerCameraYf.setOnClickListener(v -> verifierEtLancerCamera());
        ivHologrammePasseportYf.setOnClickListener(v -> verifierEtLancerCamera());

        // زر الموافقة: محمي بقيد الوجه الإلزامي
        btnApprouverYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visageHumainValide) {
                    Toast.makeText(VerificationActivity.this, R.string.toast_refus_sans_visage, Toast.LENGTH_LONG).show();
                    return;
                }
                ouvrirResultat(true);
            }
        });

        // زر الرفض: متاح دائماً لرفض الروبوتات والمتسللين
        btnRefuserYf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrirResultat(false);
            }
        });
    }

    /**
     * 31. إنشاء نية صريحة نحو ResultActivity مع نقل الحكم النهائي واسم المسافر
     */
    private void ouvrirResultat(boolean verdictFinal) {
        Intent intentResultat = new Intent(VerificationActivity.this, ResultActivity.class);
        intentResultat.putExtra(IntentKeys.getNomKey(MATRICULE_ETUDIANT), nomVoyageur);
        intentResultat.putExtra(IntentKeys.getVerdictKey(MATRICULE_ETUDIANT), verdictFinal);
        startActivity(intentResultat);
    }
}
