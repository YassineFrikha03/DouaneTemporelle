package tn.fsegt.douanetemporelle;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * النشاط الرئيسي 1: بوابة تسجيل المسافرين عبر الزمن - نظام كرونوس
 * المرحلة 02: التحقق الشامل من صحة مدخلات الاستمارة مع الحفاظ على الهيكل والمظهر المتقدم
 */
public class MainActivity extends AppCompatActivity {

    // متغير لتتبع حالة المظهر المظلم أو التكتيكي الفاتح
    private boolean isDarkMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تحميل واجهة الاستمارة الرسمية مع المعرفات المنتهية بـ oa
        setContentView(R.layout.activity_main);

        // ربط عناصر شاشة التحميل التفاعلية
        final View layoutLoadingOverlay = findViewById(R.id.layout_loading_overlay_oa);
        final ImageView imgLoadingPortal = findViewById(R.id.img_loading_portal_oa);

        // تفعيل دوران البوابة الزمنية بسرعة 3000 ملي ثانية
        if (imgLoadingPortal != null) {
            ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(imgLoadingPortal, "rotation", 0f, 360f);
            rotateAnim.setDuration(3000);
            rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.start();
        }

        // إخفاء شاشة التحميل بعد 1800 ملي ثانية بانتقال ناعم
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (layoutLoadingOverlay != null) {
                    layoutLoadingOverlay.animate()
                            .alpha(0f)
                            .setDuration(600)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    layoutLoadingOverlay.setVisibility(View.GONE);
                                }
                            })
                            .start();
                }
            }
        }, 1800);

        // ربط عناصر التبديل بين المظهرين المظلم والفاتح
        final ImageView btnThemeToggle = findViewById(R.id.btn_theme_toggle_oa);
        final FrameLayout rootMain = findViewById(R.id.root_main_oa);
        final LinearLayout cardForm = findViewById(R.id.card_form_oa);
        final TextView txtIntroTitle = findViewById(R.id.txt_intro_title_oa);

        if (btnThemeToggle != null) {
            btnThemeToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDarkMode = !isDarkMode;
                    if (isDarkMode) {
                        btnThemeToggle.setImageResource(R.drawable.ic_sun);
                        if (rootMain != null) {
                            rootMain.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.bg_chronos_deep));
                        }
                        if (cardForm != null) {
                            cardForm.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_card_chronos));
                        }
                        if (txtIntroTitle != null) {
                            txtIntroTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary));
                        }
                    } else {
                        btnThemeToggle.setImageResource(R.drawable.ic_moon);
                        if (rootMain != null) {
                            rootMain.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.bg_chronos_light));
                        }
                        if (cardForm != null) {
                            cardForm.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                        }
                        if (txtIntroTitle != null) {
                            txtIntroTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary_light));
                        }
                    }
                }
            });
        }

        // ==============================================
        // المرحلة 02: ربط عناصر الاستمارة والتحقق من صحتها
        // ==============================================
        final EditText edtNomVoyageur = findViewById(R.id.edt_nom_voyageur_oa);
        final EditText edtAnneeNaissance = findViewById(R.id.edt_annee_naissance_oa);
        final EditText edtAnneeDestination = findViewById(R.id.edt_annee_destination_oa);
        final RadioGroup rgMotif = findViewById(R.id.rg_motif_oa);
        final TextView txtErreurMotif = findViewById(R.id.txt_erreur_motif_oa);
        final Button btnInspecter = findViewById(R.id.btn_inspecter_oa);

        // إخفاء رسالة خطأ السبب تلقائياً عند تحديد أي خيار
        if (rgMotif != null && txtErreurMotif != null) {
            rgMotif.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId != -1) {
                        txtErreurMotif.setVisibility(View.GONE);
                    }
                }
            });
        }

        // معالجة النقر على زر فحص الملف والتحقق الدقيق من المدخلات
        if (btnInspecter != null) {
            btnInspecter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // تصفير مؤشرات الأخطاء السابقة
                    if (edtNomVoyageur != null) edtNomVoyageur.setError(null);
                    if (edtAnneeNaissance != null) edtAnneeNaissance.setError(null);
                    if (edtAnneeDestination != null) edtAnneeDestination.setError(null);
                    if (txtErreurMotif != null) txtErreurMotif.setVisibility(View.GONE);

                    boolean isValid = true;
                    View firstErrorView = null;

                    // 1. التحقق من اسم المسافر (غير فارغ ولا يحتوي فقط على مسافات)
                    String nom = "";
                    if (edtNomVoyageur != null) {
                        nom = edtNomVoyageur.getText().toString().trim();
                        if (nom.isEmpty()) {
                            edtNomVoyageur.setError(getString(R.string.err_nom_requis));
                            isValid = false;
                            firstErrorView = edtNomVoyageur;
                        }
                    }

                    // 2. التحقق من سنة الولادة (قيمة عددية صحيحة مع تفادي الاستثناءات)
                    if (edtAnneeNaissance != null) {
                        String naissStr = edtAnneeNaissance.getText().toString().trim();
                        if (naissStr.isEmpty()) {
                            edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_requise));
                            isValid = false;
                            if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                        } else {
                            try {
                                Integer.parseInt(naissStr);
                            } catch (NumberFormatException e) {
                                edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_invalide));
                                isValid = false;
                                if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                            }
                        }
                    }

                    // 3. التحقق من سنة الوجهة (قيمة عددية صحيحة مع السماح بالسفر نحو الماضي)
                    if (edtAnneeDestination != null) {
                        String destStr = edtAnneeDestination.getText().toString().trim();
                        if (destStr.isEmpty()) {
                            edtAnneeDestination.setError(getString(R.string.err_annee_destination_requise));
                            isValid = false;
                            if (firstErrorView == null) firstErrorView = edtAnneeDestination;
                        } else {
                            try {
                                Integer.parseInt(destStr);
                            } catch (NumberFormatException e) {
                                edtAnneeDestination.setError(getString(R.string.err_annee_destination_invalide));
                                isValid = false;
                                if (firstErrorView == null) firstErrorView = edtAnneeDestination;
                            }
                        }
                    }

                    // 4. التحقق من اختيار سبب السفر الإلزامي من بين الخيارات الثلاثة
                    if (rgMotif != null) {
                        int selectedId = rgMotif.getCheckedRadioButtonId();
                        if (selectedId == -1) {
                            if (txtErreurMotif != null) {
                                txtErreurMotif.setVisibility(View.VISIBLE);
                            }
                            isValid = false;
                            if (firstErrorView == null) firstErrorView = rgMotif;
                        }
                    }

                    // توجيه المؤشر نحو أول حقل به خطأ
                    if (!isValid) {
                        if (firstErrorView != null) {
                            firstErrorView.requestFocus();
                        }
                        return;
                    }

                    // المرحلة 03: استخراج القيم الصالحة وبناء القصد الصريح
                    int anneeNaissance = 0;
                    int anneeDestination = 0;
                    try {
                        if (edtAnneeNaissance != null) {
                            anneeNaissance = Integer.parseInt(edtAnneeNaissance.getText().toString().trim());
                        }
                        if (edtAnneeDestination != null) {
                            anneeDestination = Integer.parseInt(edtAnneeDestination.getText().toString().trim());
                        }
                    } catch (NumberFormatException e) {
                        // تمت معالجة هذا الخطأ مسبقاً في التحقق
                    }

                    String motifSelectionne = "";
                    if (rgMotif != null) {
                        int selectedId = rgMotif.getCheckedRadioButtonId();
                        RadioButton rbChoisi = findViewById(selectedId);
                        if (rbChoisi != null) {
                            motifSelectionne = rbChoisi.getText().toString();
                        }
                    }

                    // إنشاء القصد الصريح لنقل البيانات إلى نشاط التحقق VerificationActivity
                    Intent intent = new Intent(MainActivity.this, VerificationActivity.class);

                    // إضافة البيانات بالاعتماد حصراً على الدالة الثابتة ورقم الطالب 1688
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR), nom);
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_NAISSANCE), anneeNaissance);
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_DESTINATION), anneeDestination);
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_MOTIF_VOYAGE), motifSelectionne);

                    // تشغيل النشاط الثاني دون إغلاق النشاط الحالي للسماح بالعودة
                    startActivity(intent);
                }
            });
        }
    }
}
