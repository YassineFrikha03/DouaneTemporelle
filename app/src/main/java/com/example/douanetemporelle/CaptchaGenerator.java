package com.example.douanetemporelle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;

/**
 * 1. محرك توليد اختبار reCAPTCHA البصري المتقدم للتحقق من الكائنات البشرية
 * يولد نصوصاً أمنية مشوهة ديناميكياً مع خطوط تشويش لمنع الروبوتات من فك التشفير
 */
public class CaptchaGenerator {

    // 2. قائمة المحارف المعتمدة (استبعاد المحارف المتشابهة مثل 0 و O و 1 و I)
    private static final String CARACTERES_CAPTCHA = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int LONGUEUR_CODE = 6;
    private static final Random random = new Random();

    // 3. فئة نتيجة التوليد: تحتوي على النص الصريح والصورة المولدة
    public static class ResultatCaptcha {
        private final String code;
        private final Bitmap image;

        public ResultatCaptcha(String code, Bitmap image) {
            this.code = code;
            this.image = image;
        }

        public String getCode() {
            return code;
        }

        public Bitmap getImage() {
            return image;
        }
    }

    /**
     * 4. دالة إنشاء صورة CAPTCHA متقدمة مع خطوط ونقاط تشويش بصرية
     *
     * @param largeur عرض الصورة النقطية
     * @param hauteur ارتفاع الصورة النقطية
     * @return كائن ResultatCaptcha يضم الكود الصحيح والصورة
     */
    public static ResultatCaptcha generer(int largeur, int hauteur) {
        // توليد كود عشوائي مكون من 6 خانات
        StringBuilder sbCode = new StringBuilder();
        for (int i = 0; i < LONGUEUR_CODE; i++) {
            int index = random.nextInt(CARACTERES_CAPTCHA.length());
            sbCode.append(CARACTERES_CAPTCHA.charAt(index));
        }
        String codeGenere = sbCode.toString();

        // إنشاء الصورة النقطية وقماش الرسم Canvas
        Bitmap bitmap = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 5. طلاء خلفية الكابتشا بلون تقني داكن
        canvas.drawColor(Color.parseColor("#0C1527"));

        // 6. رسم خطوط تشويش أمنية خلفية
        Paint paintBruit = new Paint();
        paintBruit.setStrokeWidth(1.5f);
        paintBruit.setAntiAlias(true);

        for (int i = 0; i < 7; i++) {
            paintBruit.setColor(Color.argb(70, random.nextInt(256), random.nextInt(256), 255));
            canvas.drawLine(
                    random.nextInt(largeur), random.nextInt(hauteur),
                    random.nextInt(largeur), random.nextInt(hauteur),
                    paintBruit
            );
        }

        // 7. رسم نقاط تشويش عشوائية (Noise Dots)
        Paint paintPoints = new Paint();
        paintPoints.setColor(Color.parseColor("#4000E5FF"));
        for (int i = 0; i < 120; i++) {
            canvas.drawCircle(random.nextInt(largeur), random.nextInt(hauteur), 1.2f, paintPoints);
        }

        // 8. إعداد فرشاة كتابة المحارف المشوهة
        Paint paintTexte = new Paint();
        paintTexte.setTextSize(hauteur * 0.52f);
        paintTexte.setFakeBoldText(true);
        paintTexte.setAntiAlias(true);

        int[] paletteCouleurs = {
                Color.parseColor("#00E5FF"), // سيان
                Color.parseColor("#38BDF8"), // أزرق سماوي
                Color.parseColor("#818CF8"), // بنفسجي
                Color.parseColor("#F59E0B"), // ذهبي
                Color.parseColor("#10B981"), // زمردي
                Color.parseColor("#F8FAFC")  // أبيض
        };

        int pasX = (largeur - 30) / LONGUEUR_CODE;
        int debutX = 18;

        for (int i = 0; i < LONGUEUR_CODE; i++) {
            String c = String.valueOf(codeGenere.charAt(i));
            paintTexte.setColor(paletteCouleurs[random.nextInt(paletteCouleurs.length)]);

            int rotation = random.nextInt(35) - 17; // تدوير المحرف بين -17 و +17 درجة
            int xPos = debutX + (i * pasX);
            int yPos = (int) (hauteur * 0.68f) + (random.nextInt(10) - 5);

            canvas.save();
            canvas.rotate(rotation, xPos + 10, yPos - 15);
            canvas.drawText(c, xPos, yPos, paintTexte);
            canvas.restore();
        }

        // 9. رسم خط موجي أمني نهائي يعبر النص لمنع القراءة الآلية
        Paint paintOnde = new Paint();
        paintOnde.setColor(Color.parseColor("#5000E5FF"));
        paintOnde.setStrokeWidth(2f);
        paintOnde.setStyle(Paint.Style.STROKE);
        paintOnde.setAntiAlias(true);

        float yMilieu = hauteur * 0.5f;
        canvas.drawLine(10, yMilieu + (random.nextInt(14) - 7), largeur - 10, yMilieu + (random.nextInt(14) - 7), paintOnde);

        return new ResultatCaptcha(codeGenere, bitmap);
    }
}

