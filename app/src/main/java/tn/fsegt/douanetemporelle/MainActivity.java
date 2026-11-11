package tn.fsegt.douanetemporelle;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Calendar;
import java.util.Random;

/**
 * النشاط الرئيسي 1: بوابة تسجيل المسافرين عبر الزمن - نظام كرونوس
 * المرحلة 02 ومتابعتها: التحقق الصارم في الوقت الفعلي وعند الإرسال من صحة الاسم وسنة الولادة
 * المرحلة 03: نقل البيانات عبر القصد الصريح بالاعتماد على المفاتيح الديناميكية
 * المرحلة 06: تصفير وتجهيز الاستمارة عند استقبال مسافر جديد عبر onNewIntent
 * المرحلة 08: محاكاة محلية لكابتشا التحكم البشري الحسابي قبل فحص الملف
 */
public class MainActivity extends AppCompatActivity {

    // متغير لتتبع حالة المظهر المظلم أو التكتيكي الفاتح
    private boolean isDarkMode = true;

    // مراجع عناصر الاستمارة لإتاحة إعادة الضبط السلس
    private EditText edtNomVoyageur;
    private EditText edtAnneeNaissance;
    private EditText edtAnneeDestination;
    private RadioGroup rgMotif;
    private TextView txtErreurMotif;

    // مراجع عناصر الكابتشا الزمني المحلي
    private TextView txtCaptchaQuestion;
    private EditText edtCaptchaReponse;
    private TextView txtErreurCaptcha;

    // معاملا العملية الحسابية للكابتشا (أرقام بين 1 و 9)
    private int captchaOperande1 = 0;
    private int captchaOperande2 = 0;

    // مفاتيح حفظ واسترجاع حالة الكابتشا عند تدوير الشاشة
    private static final String STATE_CAPTCHA_OP1 = "state_captcha_op1_oa";
    private static final String STATE_CAPTCHA_OP2 = "state_captcha_op2_oa";

    // مولد الأرقام العشوائية المحلي
    private final Random random = new Random();

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

        // ربط عناصر الكابتشا
        txtCaptchaQuestion = findViewById(R.id.txt_captcha_question_oa);
        edtCaptchaReponse = findViewById(R.id.edt_captcha_reponse_oa);
        txtErreurCaptcha = findViewById(R.id.txt_erreur_captcha_oa);

        // استعادة حالة الكابتشا عند تدوير الجهاز أو توليد مسألة جديدة لأول مرة
        if (savedInstanceState != null) {
            captchaOperande1 = savedInstanceState.getInt(STATE_CAPTCHA_OP1, 0);
            captchaOperande2 = savedInstanceState.getInt(STATE_CAPTCHA_OP2, 0);
        }
        if (captchaOperande1 < 1 || captchaOperande1 > 9 || captchaOperande2 < 1 || captchaOperande2 > 9) {
            genererNouveauCaptcha();
        } else {
            mettreAJourAffichageCaptcha();
        }

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
                        if (txtCaptchaQuestion != null) {
                            txtCaptchaQuestion.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary));
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
                        if (txtCaptchaQuestion != null) {
                            txtCaptchaQuestion.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary_light));
                        }
                    }
                }
            });
        }

        // ==============================================
        // ربط عناصر الاستمارة والتحقق من صحتها
        // ==============================================
        edtNomVoyageur = findViewById(R.id.edt_nom_voyageur_oa);
        edtAnneeNaissance = findViewById(R.id.edt_annee_naissance_oa);
        edtAnneeDestination = findViewById(R.id.edt_annee_destination_oa);
        rgMotif = findViewById(R.id.rg_motif_oa);
        txtErreurMotif = findViewById(R.id.txt_erreur_motif_oa);
        final Button btnInspecter = findViewById(R.id.btn_inspecter_oa);

        // مراقبة حقل الاسم في الوقت الفعلي لمنع وإظهار الأحرف غير المسموحة
        if (edtNomVoyageur != null) {
            edtNomVoyageur.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null && s.length() > 0) {
                        if (!contientUniquementLettresEtEspaces(s.toString())) {
                            edtNomVoyageur.setError(getString(R.string.err_nom_lettres_espaces));
                        } else {
                            edtNomVoyageur.setError(null);
                        }
                    } else {
                        edtNomVoyageur.setError(null);
                    }
                }
            });
        }

        // مراقبة حقل سنة الولادة في الوقت الفعلي للسماح بالكتابة التدريجية لـ 4 أرقام
        if (edtAnneeNaissance != null) {
            edtAnneeNaissance.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    // مسح الخطأ السابق فور قيام المستخدم بتعديل الحقل
                    if (edtAnneeNaissance.getError() != null) {
                        edtAnneeNaissance.setError(null);
                    }
                }
            });
        }

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
                    // تصفير مؤشرات الأخطاء السابقة للاستمارة والكابتشا
                    if (edtNomVoyageur != null) edtNomVoyageur.setError(null);
                    if (edtAnneeNaissance != null) edtAnneeNaissance.setError(null);
                    if (edtAnneeDestination != null) edtAnneeDestination.setError(null);
                    if (txtErreurMotif != null) txtErreurMotif.setVisibility(View.GONE);
                    if (txtErreurCaptcha != null) txtErreurCaptcha.setVisibility(View.GONE);

                    boolean isValid = true;
                    View firstErrorView = null;

                    // 1. التحقق الصارم من اسم المسافر (حروف ومسافات فقط، غير فارغ، وتنظيف الأطراف)
                    String nomBrut = "";
                    String nomNettoye = "";
                    if (edtNomVoyageur != null) {
                        nomBrut = edtNomVoyageur.getText().toString();
                        nomNettoye = nomBrut.trim();
                        if (nomNettoye.isEmpty()) {
                            edtNomVoyageur.setError(getString(R.string.err_nom_obligatoire));
                            isValid = false;
                            firstErrorView = edtNomVoyageur;
                        } else if (!contientUniquementLettresEtEspaces(nomBrut)) {
                            edtNomVoyageur.setError(getString(R.string.err_nom_lettres_espaces));
                            isValid = false;
                            firstErrorView = edtNomVoyageur;
                        }
                    }

                    // 2. التحقق الصارم من سنة الولادة (4 أرقام بالضبط، رفض 0000، وعدم تجاوز السنة الحالية)
                    int anneeNaissance = 0;
                    if (edtAnneeNaissance != null) {
                        String naissStr = edtAnneeNaissance.getText().toString().trim();
                        if (naissStr.length() != 4) {
                            edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_4_chiffres));
                            isValid = false;
                            if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                        } else if ("0000".equals(naissStr)) {
                            edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_invalide));
                            isValid = false;
                            if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                        } else {
                            try {
                                anneeNaissance = Integer.parseInt(naissStr);
                                int anneeCourante = Calendar.getInstance().get(Calendar.YEAR);
                                if (anneeNaissance <= 0) {
                                    edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_invalide));
                                    isValid = false;
                                    if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                                } else if (anneeNaissance > anneeCourante) {
                                    edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_futur));
                                    isValid = false;
                                    if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                                }
                            } catch (NumberFormatException e) {
                                edtAnneeNaissance.setError(getString(R.string.err_annee_naissance_invalide));
                                isValid = false;
                                if (firstErrorView == null) firstErrorView = edtAnneeNaissance;
                            }
                        }
                    }

                    // 3. التحقق من سنة الوجهة (قيمة عددية صحيحة مع السماح بالسفر نحو الماضي والمستقبل)
                    int anneeDestination = 0;
                    if (edtAnneeDestination != null) {
                        String destStr = edtAnneeDestination.getText().toString().trim();
                        if (destStr.isEmpty()) {
                            edtAnneeDestination.setError(getString(R.string.err_annee_destination_requise));
                            isValid = false;
                            if (firstErrorView == null) firstErrorView = edtAnneeDestination;
                        } else {
                            try {
                                anneeDestination = Integer.parseInt(destStr);
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

                    // أولوية التحقق: إذا كانت استمارة المسافر غير صالحة نتوقف فوراً دون فحص الكابتشا أو تغييره
                    if (!isValid) {
                        if (firstErrorView != null) {
                            firstErrorView.requestFocus();
                        }
                        return;
                    }

                    // ==============================================
                    // المرحلة 08: فحص إجابة الكابتشا الزمني المحلي
                    // ==============================================
                    String reponseStr = "";
                    if (edtCaptchaReponse != null) {
                        reponseStr = edtCaptchaReponse.getText().toString().trim();
                    }

                    // في حالة عدم إدخال إجابة
                    if (reponseStr.isEmpty()) {
                        if (txtErreurCaptcha != null) {
                            txtErreurCaptcha.setText(R.string.err_captcha_requis);
                            txtErreurCaptcha.setVisibility(View.VISIBLE);
                        }
                        if (edtCaptchaReponse != null) {
                            edtCaptchaReponse.requestFocus();
                        }
                        return;
                    }

                    int reponseSaisie;
                    try {
                        reponseSaisie = Integer.parseInt(reponseStr);
                    } catch (NumberFormatException e) {
                        reponseSaisie = Integer.MIN_VALUE;
                    }

                    int reponseAttendue = captchaOperande1 + captchaOperande2;

                    // في حالة إجابة خاطئة أو غير عددية
                    if (reponseSaisie != reponseAttendue) {
                        if (edtCaptchaReponse != null) {
                            edtCaptchaReponse.setText("");
                        }
                        if (txtErreurCaptcha != null) {
                            txtErreurCaptcha.setText(R.string.err_captcha_incorrect);
                            txtErreurCaptcha.setVisibility(View.VISIBLE);
                        }
                        // تجديد المسألة الحسابية بمسألة مختلفة مع الحفاظ التام على بيانات الاستمارة
                        genererNouveauCaptcha();
                        if (edtCaptchaReponse != null) {
                            edtCaptchaReponse.requestFocus();
                        }
                        return;
                    }

                    // عند صحة الكابتشا: استهلاك التحدي الحالي وتجهيز مسألة جديدة لاحتمال الرجوع
                    if (edtCaptchaReponse != null) {
                        edtCaptchaReponse.setText("");
                    }
                    if (txtErreurCaptcha != null) {
                        txtErreurCaptcha.setVisibility(View.GONE);
                    }
                    genererNouveauCaptcha();

                    // استخراج سبب السفر المحدد
                    String motifSelectionne = "";
                    if (rgMotif != null) {
                        int selectedId = rgMotif.getCheckedRadioButtonId();
                        RadioButton rbChoisi = findViewById(selectedId);
                        if (rbChoisi != null) {
                            motifSelectionne = rbChoisi.getText().toString();
                        }
                    }

                    // إنشاء القصد الصريح لنقل البيانات إلى النشاط السينمائي التمهيدي TemporalJourneyIntroActivity (المرحلة 10)
                    Intent intent = new Intent(MainActivity.this, TemporalJourneyIntroActivity.class);

                    // إضافة البيانات بالاعتماد حصراً على الدالة الثابتة ورقم الطالب 1688 والاسم المنظف
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_NOM_VOYAGEUR), nomNettoye);
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_NAISSANCE), anneeNaissance);
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_ANNEE_DESTINATION), anneeDestination);
                    intent.putExtra(ChronosKeys.buildKey(ChronosKeys.STUDENT_ID, ChronosKeys.SUFFIX_MOTIF_VOYAGE), motifSelectionne);

                    // تشغيل النشاط التمهيدي دون إغلاق النشاط الحالي للسماح بالعودة
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * فحص هل يحتوي النص حصراً على حروف ومسافات وفق متطلبات المشروع
     *
     * @param s النص المراد فحصه
     * @return true إذا كان النص يحتوي حصراً على حروف ومسافات
     */
    public static boolean contientUniquementLettresEtEspaces(String s) {
        if (s == null) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // حفظ حدي مسألة الكابتشا لضمان التوافق التام عند تدوير الشاشة
        outState.putInt(STATE_CAPTCHA_OP1, captchaOperande1);
        outState.putInt(STATE_CAPTCHA_OP2, captchaOperande2);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // تصفير النموذج بالكامل عند العودة من شاشة النتيجة لتسجيل مسافر جديد
        reinitialiserFormulaire();
    }

    /**
     * توليد مسألة كابتشا جديدة محلياً بأرقام بين 1 و 9 مختلفة عن السابقة
     */
    private void genererNouveauCaptcha() {
        int nouveauOp1;
        int nouveauOp2;
        // ضمان ألا تكون المسألة الجديدة متطابقة تماماً مع المسألة السابقة
        do {
            nouveauOp1 = 1 + random.nextInt(9);
            nouveauOp2 = 1 + random.nextInt(9);
        } while (nouveauOp1 == captchaOperande1 && nouveauOp2 == captchaOperande2);

        captchaOperande1 = nouveauOp1;
        captchaOperande2 = nouveauOp2;
        mettreAJourAffichageCaptcha();
    }

    /**
     * تحديث نص السؤال الحسابي على الواجهة
     */
    private void mettreAJourAffichageCaptcha() {
        if (txtCaptchaQuestion != null) {
            txtCaptchaQuestion.setText(getString(R.string.format_captcha_question, captchaOperande1, captchaOperande2));
        }
    }

    /**
     * إعادة ضبط وتصفير حقول الاستمارة وتوليد تحدي كابتشا جديد
     */
    private void reinitialiserFormulaire() {
        if (edtNomVoyageur != null) {
            edtNomVoyageur.setText("");
            edtNomVoyageur.setError(null);
        }
        if (edtAnneeNaissance != null) {
            edtAnneeNaissance.setText("");
            edtAnneeNaissance.setError(null);
        }
        if (edtAnneeDestination != null) {
            edtAnneeDestination.setText("");
            edtAnneeDestination.setError(null);
        }
        if (rgMotif != null) {
            rgMotif.clearCheck();
        }
        if (txtErreurMotif != null) {
            txtErreurMotif.setVisibility(View.GONE);
        }
        if (edtCaptchaReponse != null) {
            edtCaptchaReponse.setText("");
        }
        if (txtErreurCaptcha != null) {
            txtErreurCaptcha.setVisibility(View.GONE);
        }
        // توليد مسألة كابتشا جديدة ونظيفة للمسافر الجديد
        genererNouveauCaptcha();

        if (edtNomVoyageur != null) {
            edtNomVoyageur.requestFocus();
        }
    }
}
