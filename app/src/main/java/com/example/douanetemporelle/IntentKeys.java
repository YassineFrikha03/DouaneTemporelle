package com.example.douanetemporelle;

/**
 * 1. الصنف المسؤول عن إدارة وإنشاء مفاتيح تبادل البيانات بين الأنشطة (Intents)
 * تم تطبيق قيد المشروع: توليد المفاتيح ديناميكياً باستخدام آخر 4 أرقام من بطاقة الطالب
 */
public class IntentKeys {

    // 2. آخر 4 أرقام من بطاقة الطالب (يمكن تعديلها حسب رقم الطالب)
    public static final String MATRICULE_DEFAULT = "1234";

    // أسماء الحقول الأساسية
    public static final String CLE_NOM = "NOM_VOYAGEUR";
    public static final String CLE_ANNEE_NAISSANCE = "ANNEE_NAISSANCE";
    public static final String CLE_ANNEE_DESTINATION = "ANNEE_DESTINATION";
    public static final String CLE_MOTIF = "MOTIF_VOYAGE";
    public static final String CLE_VERDICT = "VERDICT_FINAL";

    /**
     * 3. دالة ثابتة لبناء مفتاح مخصص مسبوق بآخر 4 أرقام من بطاقة الطالب
     * تضمن عدم كتابة المفاتيح بشكل ثابت (Hardcoded) في الكود وفق شروط المشروع
     *
     * @param matricule آخر 4 أرقام من بطاقة الطالب
     * @param nomCle    الاسم القاعدي للمفتاح
     * @return المفتاح الكامل (مثال: "1234_NOM_VOYAGEUR")
     */
    public static String getKey(String matricule, String nomCle) {
        String code = (matricule != null && !matricule.trim().isEmpty()) ? matricule.trim() : MATRICULE_DEFAULT;
        return code + "_" + nomCle;
    }

    // 4. دوال مساعدة لإنشاء المفاتيح الخاصة بكل عنصر باستعمال رقم التسجيل الممرر
    public static String getNomKey(String matricule) {
        return getKey(matricule, CLE_NOM);
    }

    public static String getAnneeNaissanceKey(String matricule) {
        return getKey(matricule, CLE_ANNEE_NAISSANCE);
    }

    public static String getAnneeDestinationKey(String matricule) {
        return getKey(matricule, CLE_ANNEE_DESTINATION);
    }

    public static String getMotifKey(String matricule) {
        return getKey(matricule, CLE_MOTIF);
    }

    public static String getVerdictKey(String matricule) {
        return getKey(matricule, CLE_VERDICT);
    }
}

