# Douane Temporelle — CHRONOS ⏳

Application Android native développée dans le cadre du module de **Programmation Mobile (DS 1)** à la **Faculté des Sciences Économiques et de Gestion de Tunis (FSEGT)** — 3ème Année Business Intelligence (BI).

---

## 👥 Membres de l'Équipe

- **Omar Elayachi** — Groupe **A02**
- **Yassine Frikha** — Groupe **A02**

---

## 🧭 Présentation du Projet

**CHRONOS** est un système de contrôle douanier spatio-temporel futuriste. L'application gère l'enregistrement, l'inspection des voyageurs temporels, la détection des paradoxes historiques basés sur les nombres premiers, et la délivrance de passeports temporels officiels.

---

## 🚀 Parcours et Fonctionnalités Clés

1. **Enregistrement et Validation Stricte (`MainActivity`) :**
   - Saisie du nom complet (lettres Unicode et espaces uniquement, contrôle en temps réel).
   - Année de naissance (4 chiffres obligatoires, vérifiée dynamiquement contre l'année système actuelle).
   - Année de destination temporelle et motif du saut (`Tourisme`, `Modification du passé`, `Fuite`).
   - Défi de calcul mental local (**CAPTCHA Temporel** pédagogique).
   - Basculement de thème d'affichage (Dark Navy futuriste / Light Tactique).

2. **Séquence Cinématique (« A Journey Through Time » — `TemporalJourneyIntroActivity`) :**
   - Animation d'immersion avec cadran vectoriel, rotation d'aiguille temporelle et ondes de flux.
   - Bouton d'accès direct « Passer l'introduction ».

3. **Passeport Temporel & Scanner de Paradoxe (`VerificationActivity`) :**
   - **Architecture XML imposée respectée strictement** : `LinearLayout` horizontal racine $\rightarrow$ `ImageView` (poids 0.3) + `RelativeLayout` (poids 0.7).
   - Génération locale d'un numéro de passeport unique `CHR-XXXXXXXX`.
   - Balayage laser dynamique et retour haptique au clic d'action.
   - **Règle du Paradoxe Temporel** : Si le motif est `Modification du passé` et que l'écart temporel est un **nombre premier**, l'approbation est automatiquement désactivée pour protéger la ligne du temps.

4. **Verdict Douanier (`ResultActivity`) :**
   - Résultat visuel instantané : Autorisation en vert néon avec tampon officiel « SAUT APPROUVÉ » ou arrestation en rouge alerte avec tampon « VOYAGE REFUSÉ ».
   - Transmission synchronisée du numéro de passeport.

5. **Tunnel Temporel de Retour (« Time Vortex » — `TemporalReturnActivity`) :**
   - Déclenché lors du clic sur « Nouveau voyageur ».
   - Rendu Canvas haute performance d'un vortex de 8 cadrans d'horloge avec chiffres romains (`XII`, `III`, `VI`, `IX`).
   - Retour direct à `MainActivity` avec réinitialisation complète du formulaire et régénération d'un nouveau CAPTCHA via `onNewIntent()`.

---

## 🛠️ Stack Technique

- **Langage :** Java (Android SDK 34 / minSdk 24)
- **UI / Design :** Android Views / XML, Canvas 2D, Vector Drawables, Interpolateurs natifs
- **Architecture de Clés Dynamiques :** `ChronosKeys.buildKey(matricule, suffixe)`
- **Standards de Code :** 100 % des commentaires en langue arabe avec chiffres 0-9 ; identifiants XML suffixés par `_oa`.
- **Tests :** Suite complète de tests unitaires JUnit 4 (`ChronosRegressionTest`).

---

## 📦 Compilation et Exécution

```bash
# Compilation de l'APK Debug
./gradlew assembleDebug

# Exécution des tests unitaires
./gradlew testDebugUnitTest
```
