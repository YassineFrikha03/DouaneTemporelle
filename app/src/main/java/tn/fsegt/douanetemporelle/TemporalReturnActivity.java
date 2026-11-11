package tn.fsegt.douanetemporelle;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط الانتقالي: النفق الزمني عند العودة إلى الاستمارة الرئيسية - نظام كرونوس
 * المرحلة 11: تسلسل حركي نفق الساعات ثلاثي الأبعاد قبل إعادة تصفير الاستمارة
 */
public class TemporalReturnActivity extends AppCompatActivity {

    /**
     * حارس أمان لمنع إطلاق نشاط MainActivity المتكرر
     */
    private boolean isNavigating = false;

    private TemporalTunnelView tunnelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_return);

        tunnelView = findViewById(R.id.view_temporal_tunnel_oa);
        Button btnSkip = findViewById(R.id.btn_skip_tunnel_oa);

        // ربط مستمع انتهاء حركة النفق للانتقال التلقائي
        if (tunnelView != null) {
            tunnelView.setOnTunnelFinishedListener(this::naviguerVersMainActivity);
        }

        // ربط زر تخطي النفق للانتقال الفوري
        if (btnSkip != null) {
            btnSkip.setOnClickListener(v -> naviguerVersMainActivity());
        }

        // التعامل مع زر الرجوع في النظام لتخطي النفق بأمان والعودة للاستمارة
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                naviguerVersMainActivity();
            }
        });

        // إذا كانت الحركات معطلة في النظام يتم التوجيه الفوري
        if (!ValueAnimator.areAnimatorsEnabled()) {
            naviguerVersMainActivity();
            return;
        }

        // بدء حركة النفق الزمني
        if (tunnelView != null) {
            tunnelView.startAnimation();
        }
    }

    /**
     * التوجيه الآمن نحو MainActivity مع تصفير المكدس وفقاً لاستراتيجية المرحلة 06
     */
    private synchronized void naviguerVersMainActivity() {
        if (isNavigating || isFinishing() || isDestroyed()) {
            return;
        }
        isNavigating = true;

        if (tunnelView != null) {
            tunnelView.stopAnimation();
        }

        Intent returnIntent = new Intent(TemporalReturnActivity.this, MainActivity.class);
        // غلق كافة الأنشطة السابقة في المكدس لضمان عودة نظيفة وحيدة
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(returnIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (tunnelView != null) {
            tunnelView.stopAnimation();
        }
        super.onDestroy();
    }
}
