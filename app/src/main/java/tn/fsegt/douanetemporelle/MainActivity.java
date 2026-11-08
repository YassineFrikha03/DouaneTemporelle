package tn.fsegt.douanetemporelle;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * النشاط الرئيسي 1: بوابة تسجيل المسافرين عبر الزمن - نظام كرونوس
 * المرحلة 01 المحسنة: واجهة مستخدم متقدمة مع شاشة تحميل تفاعلية وزر تبديل المظهر
 */
public class MainActivity extends AppCompatActivity {

    // متغير لتتبع حالة المظهر المظلم أو التكتيكي الفاتح
    private boolean isDarkMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تحميل واجهة الاستمارة الرسمية المحسنة مع المعرفات المنتهية بـ oa
        setContentView(R.layout.activity_main);

        // ربط عناصر واجهة التحميل المتقدمة
        final View layoutLoadingOverlay = findViewById(R.id.layout_loading_overlay_oa);
        final ImageView imgLoadingPortal = findViewById(R.id.img_loading_portal_oa);

        // تفعيل حركة دوران مستمرة لبوابة التحميل الزمنية بسرعة 3000 ملي ثانية
        if (imgLoadingPortal != null) {
            ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(imgLoadingPortal, "rotation", 0f, 360f);
            rotateAnim.setDuration(3000);
            rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
            rotateAnim.setInterpolator(new LinearInterpolator());
            rotateAnim.start();
        }

        // محاكاة الاتصال بالنظام الزمني وإخفاء شاشة التحميل بانتقال ناعم بعد 1800 ملي ثانية
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

        // ربط وتفعيل زر التبديل بين المظهر المظلم والفاتح
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
                        // الانتقال إلى المظهر الليلي الفضائي
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
                        // الانتقال إلى المظهر النهاري التكتيكي
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
    }
}
