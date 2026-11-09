package com.example.douanetemporelle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * 1. صنف مساعد لتوليد باركود أمني عالي الدقة (Barcode Generator) ديناميكياً
 * يحاكي تشفير Code-128 المعتمد في جوازات السفر وتذاكر العبور الزمني
 */
public class BarcodeUtil {

    /**
     * 2. دالة توليد صورة نقطية (Bitmap) لباركود مخصص لبيانات المسافر
     *
     * @param donnees النص أو المعرف الزمني المراد تشفيره
     * @param largeur العرض المطلوب للباركود (بالبكسل)
     * @param hauteur الارتفاع المطلوب للباركود (بالبكسل)
     * @return Bitmap يحتوي على خطوط الباركود المتناسقة مع واجهة التطبيق
     */
    public static Bitmap genererCodeBarre(String donnees, int largeur, int hauteur) {
        if (donnees == null || donnees.trim().isEmpty()) {
            donnees = "CHRONO-1234-YF";
        }

        Bitmap bitmap = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 3. طلاء خلفية الباركود بشفافية كاملة لتندمج مع لون التذكرة
        canvas.drawColor(Color.TRANSPARENT);

        // 4. إعداد فرشاة الخطوط القياسية باللون الأبيض الرمادي الناصع
        Paint paintBarre = new Paint();
        paintBarre.setAntiAlias(false);
        paintBarre.setColor(Color.parseColor("#F1F5F9"));

        // 5. إعداد فرشاة خطوط الحراسة والأمان باللون السيان النيون (Cyan)
        Paint paintGarde = new Paint();
        paintGarde.setAntiAlias(false);
        paintGarde.setColor(Color.parseColor("#00E5FF"));

        int hash = Math.abs(donnees.hashCode());
        int[] motifBase = {2, 1, 3, 1, 4, 1, 2, 2, 1, 3, 2, 1, 1, 4, 2, 1, 3, 2, 1, 1, 4, 2};

        int x = 6;
        int maxX = largeur - 6;
        int index = 0;

        // 6. رسم خطوط بداية الباركود (Start Guard Bars)
        canvas.drawRect(x, 2, x + 3, hauteur - 2, paintGarde);
        x += 5;
        canvas.drawRect(x, 2, x + 2, hauteur - 2, paintGarde);
        x += 6;

        // 7. رسم خطوط التشفير المتغيرة بناءً على بيانات المسافر
        while (x < maxX - 14) {
            int motif = motifBase[index % motifBase.length];
            int largeurBarre = ((hash + index * 5) % 3) + 1; // سماكة تتراوح بين 1 و 3 بكسل

            canvas.drawRect(x, 4, x + largeurBarre, hauteur - 4, paintBarre);
            x += largeurBarre;

            int espacement = (motif % 3) + 1;
            x += espacement;

            index++;
        }

        // 8. رسم خطوط نهاية الباركود (Stop Guard Bars)
        if (x < largeur - 5) {
            canvas.drawRect(x, 2, x + 2, hauteur - 2, paintGarde);
            x += 4;
            canvas.drawRect(x, 2, x + 3, hauteur - 2, paintGarde);
        }

        return bitmap;
    }
}

