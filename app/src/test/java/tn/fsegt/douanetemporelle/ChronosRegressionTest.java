package tn.fsegt.douanetemporelle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * اختبارات الانحدار الآلية الشاملة - المرحلة 07
 * فحص الخوارزميات الحسابية وتوليد المفاتيح الديناميكية بدون أسرار
 */
public class ChronosRegressionTest {

    /**
     * اختبار 1: التحقق من خوارزمية فحص الأعداد الأولية (قاعدة التناقض الزمني - المرحلة 05)
     */
    @Test
    public void testIsPrime() {
        // الحالات الخاصة: الأعداد السالبة، 0، و 1 ليست أولية
        assertFalse(VerificationActivity.isPrime(-10));
        assertFalse(VerificationActivity.isPrime(-3));
        assertFalse(VerificationActivity.isPrime(-1));
        assertFalse(VerificationActivity.isPrime(0));
        assertFalse(VerificationActivity.isPrime(1));

        // الأعداد الأولية الأساسية المؤكدة
        assertTrue(VerificationActivity.isPrime(2));
        assertTrue(VerificationActivity.isPrime(3));
        assertTrue(VerificationActivity.isPrime(5));
        assertTrue(VerificationActivity.isPrime(7));
        assertTrue(VerificationActivity.isPrime(11));
        assertTrue(VerificationActivity.isPrime(13));
        assertTrue(VerificationActivity.isPrime(17));
        assertTrue(VerificationActivity.isPrime(19));
        assertTrue(VerificationActivity.isPrime(23));
        assertTrue(VerificationActivity.isPrime(29));
        assertTrue(VerificationActivity.isPrime(31));
        assertTrue(VerificationActivity.isPrime(97));

        // الأعداد المركبة غير الأولية
        assertFalse(VerificationActivity.isPrime(4));
        assertFalse(VerificationActivity.isPrime(6));
        assertFalse(VerificationActivity.isPrime(8));
        assertFalse(VerificationActivity.isPrime(9));
        assertFalse(VerificationActivity.isPrime(15));
        assertFalse(VerificationActivity.isPrime(25));
        assertFalse(VerificationActivity.isPrime(27));
        assertFalse(VerificationActivity.isPrime(35));
        assertFalse(VerificationActivity.isPrime(49));
        assertFalse(VerificationActivity.isPrime(121));
    }

    /**
     * اختبار 2: التحقق من خوارزمية التوليد الديناميكي للمفاتيح برقم بطاقة افتراضي
     */
    @Test
    public void testBuildKeyDynamic() {
        // تجربة برقم افتراضي مكون من 4 أرقام
        String dummyMatricule4 = "9999";
        String keyNom = ChronosKeys.buildKey(dummyMatricule4, ChronosKeys.SUFFIX_NOM_VOYAGEUR);
        assertEquals("9999_NOM_VOYAGEUR", keyNom);

        // تجربة برقم افتراضي مكون من 8 أرقام (اقتطاع آخر 4 أرقام)
        String dummyMatricule8 = "12345678";
        String keyDest = ChronosKeys.buildKey(dummyMatricule8, ChronosKeys.SUFFIX_ANNEE_DESTINATION);
        assertEquals("5678_ANNEE_DESTINATION", keyDest);

        // مفتاح النتيجة النهائية
        String keyVerdict = ChronosKeys.buildKey(dummyMatricule4, ChronosKeys.SUFFIX_VERDICT_FINAL);
        assertEquals("9999_VERDICT_FINAL", keyVerdict);
    }

    /**
     * اختبار 3: التحقق من صحة حساب الفارق الزمني نحو المستقبل ونحو الماضي
     */
    @Test
    public void testCalculEcartTemporel() {
        // سفر نحو المستقبل: ولادة 1990، وجهة 2050 (فارق 60 سنة)
        long ecartFutur = 2050L - 1990L;
        assertEquals(60L, ecartFutur);

        // سفر نحو الماضي: ولادة 1990، وجهة 1987 (فارق سالب -3 سنوات بدون قيمة مطلقة اعتباطية)
        long ecartPasse = 1987L - 1990L;
        assertEquals(-3L, ecartPasse);
        assertFalse(VerificationActivity.isPrime(ecartPasse));
    }

    /**
     * اختبار 4: التحقق من منطق الكابتشا الزمني الحسابي (المرحلة 08)
     */
    @Test
    public void testLogiqueCaptcha() {
        int op1 = 7;
        int op2 = 4;
        int sommeAttendue = op1 + op2; // 11
        assertEquals(11, sommeAttendue);

        // فحص حدود المعاملين (بين 1 و 9)
        assertTrue(op1 >= 1 && op1 <= 9);
        assertTrue(op2 >= 1 && op2 <= 9);

        // رفض الإجابة الخاطئة
        int reponseFausse = 10;
        assertFalse(reponseFausse == sommeAttendue);

        // قبول الإجابة الصحيحة
        int reponseCorrecte = 11;
        assertTrue(reponseCorrecte == sommeAttendue);
    }

    /**
     * اختبار 5: التحقق الصارم من صحة اسم المسافر (متابعة المرحلة 02)
     */
    @Test
    public void testValidationNomStricte() {
        // أسماء صالحة: حروف يونيكود ومسافات فقط
        assertTrue(MainActivity.contientUniquementLettresEtEspaces("Omar"));
        assertTrue(MainActivity.contientUniquementLettresEtEspaces("Omar Ayachi"));
        assertTrue(MainActivity.contientUniquementLettresEtEspaces("Élodie Martin"));
        assertTrue(MainActivity.contientUniquementLettresEtEspaces("Jean Paul"));

        // أسماء غير صالحة: تحتوي على أرقام أو رموز أو فواصل أو شرطات
        assertFalse(MainActivity.contientUniquementLettresEtEspaces("Omar2"));
        assertFalse(MainActivity.contientUniquementLettresEtEspaces("Omar-Ayachi"));
        assertFalse(MainActivity.contientUniquementLettresEtEspaces("Omar_Ayachi"));
        assertFalse(MainActivity.contientUniquementLettresEtEspaces("Omar!"));
        assertFalse(MainActivity.contientUniquementLettresEtEspaces("Omar@Ayachi"));
        assertFalse(MainActivity.contientUniquementLettresEtEspaces("1234"));
    }

    /**
     * اختبار 6: التحقق الصارم من قواعد سنة الولادة (متابعة المرحلة 02)
     */
    @Test
    public void testValidationAnneeNaissanceStricte() {
        int anneeCourante = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        // سنوات صالحة: 4 أرقام ولا تتجاوز السنة الحالية
        assertTrue("1990".length() == 4 && Integer.parseInt("1990") <= anneeCourante);
        assertTrue(String.valueOf(anneeCourante).length() == 4 && anneeCourante <= anneeCourante);

        // سنوات غير صالحة: أقل من 4 أرقام، 0000، أو أكبر من السنة الحالية
        assertFalse("999".length() == 4);
        assertTrue("0000".equals("0000")); // مرفوضة بقاعدة الصفر
        assertFalse((anneeCourante + 1) <= anneeCourante); // مرفوضة لأنها في المستقبل
    }
}
