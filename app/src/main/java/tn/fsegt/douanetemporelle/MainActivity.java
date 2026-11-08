package tn.fsegt.douanetemporelle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * النشاط الرئيسي 1: بوابة تسجيل المسافرين عبر الزمن - نظام كرونوس
 * المرحلة 01: ربط واجهة المستخدم الرسمية وتثبيت الهيكل الأساسي
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تحميل واجهة الاستمارة الرسمية لنظام كرونوس مع المعرفات المنتهية بـ oa
        setContentView(R.layout.activity_main);
    }
}
