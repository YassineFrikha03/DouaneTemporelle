package tn.fsegt.douanetemporelle;

/**
 * فئة مساعدة لبناء مفاتيح النوايا (Intent Extras) بشكل ديناميكي
 * وفقاً لمتطلبات المشروع رقم 1: استخراج آخر 4 أرقام من بطاقة الطالب
 */
public final class ChronosKeys {

    // آخر 4 أرقام من بطاقة الطالب المعتمدة في المشروع
    public static final String STUDENT_ID = "1688";

    // اللواحق القياسية لمفاتيح البيانات المرسلة
    public static final String SUFFIX_NOM_VOYAGEUR = "NOM_VOYAGEUR";
    public static final String SUFFIX_ANNEE_NAISSANCE = "ANNEE_NAISSANCE";
    public static final String SUFFIX_ANNEE_DESTINATION = "ANNEE_DESTINATION";
    public static final String SUFFIX_MOTIF_VOYAGE = "MOTIF_VOYAGE";

    private ChronosKeys() {
        // منع إنشاء كائنات من هذه الفئة المساعدة
    }

    /**
     * دالة ثابتة لبناء مفتاح ديناميكي بالاعتماد على رقم بطاقة الطالب
     * تأخذ رقم التسجيل ولاحقة المفتاح وتعيد المفتاح المكتمل
     *
     * @param matricule رقم بطاقة الطالب (مثال: 1688)
     * @param suffixKey لاحقة المفتاح (مثال: NOM_VOYAGEUR)
     * @return المفتاح الديناميكي بصيغة "1688_NOM_VOYAGEUR"
     */
    public static String buildKey(String matricule, String suffixKey) {
        if (matricule == null || matricule.trim().isEmpty()) {
            matricule = STUDENT_ID;
        }

        String cleaned = matricule.trim();
        // استخراج آخر 4 أرقام إذا كان الرقم المدخل أطول من 4 أرقام
        String prefix = (cleaned.length() >= 4)
                ? cleaned.substring(cleaned.length() - 4)
                : cleaned;

        return prefix + "_" + suffixKey;
    }
}
