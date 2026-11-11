# Douane Temporelle - Système de Contrôle des Flux Chronologiques

Application mobile Android native développée dans le cadre du Devoir Surveillé N°1 de Programmation Mobile (FSEGT - Université de Tunis El Manar, 3ème année Business Intelligence, sous la direction de M. Nidhal Jelassi).

---

## Membres du Projet

- **Yassine Frikha** - Groupe A02
- **Omar El Ayaychi** - Groupe A02

---

## Présentation Générale

L'application **Douane Temporelle** simule un terminal frontalier de régulation spatio-temporelle de haute sécurité. Elle a pour mission d'inspecter les voyageurs temporels, d'évaluer la faisabilité de leur saut chronologique, de détecter les risques de paradoxes temporels, d'assurer une vérification humaine contre les intrusions robotiques via un défi reCAPTCHA personnalisé, et de restituer un verdict immersif contextualisé selon l'époque de destination choisie.

---

## Parcours Utilisateur et Activités

L'application est structurée autour de quatre écrans principaux interconnectés :

### 1. Écran d'Initialisation (IntroActivity)
- Écran de démarrage cinématique (Splash Screen).
- Animation d'un logo de vortex temporel avec barre de progression personnalisée.
- Transition fluide et automatique vers l'interface principale après chargement des protocoles.

### 2. Borne d'Enregistrement du Voyageur (MainActivity)
- Saisie des informations du voyageur :
  - Nom complet.
  - Année de naissance (avec contrôle strict interdisant les dates futures par rapport à l'année courante).
  - Année de destination souhaitée.
  - Motif du déplacement (Tourisme historique, Modification d'événement, Fuite temporelle).
- Analyse dynamique en direct :
  - Calcul de l'écart chronologique avec mise à jour textuelle et colorimétrique instantanée (Passé, Présent, Futur).
- Règles de filtrage et détection des paradoxes :
  - Tout motif visant la modification du continuum historique ou la fuite temporelle entraîne des avertissements de sécurité stricts.
- Transmission sécurisée des données via le système de clés dynamiques.

### 3. Poste de Contrôle & Visa Temporel (VerificationActivity)
- Interface conforme aux spécifications imposées :
  - Architecture en `LinearLayout` horizontal racine.
  - Volet gauche (`0.3` de poids) : `ImageView` du badge holographique officiel du voyageur.
  - Volet droit (`0.7` de poids) : `RelativeLayout` structuré en billet / passeport temporel.
- Génération procédurale d'un code-barres haute fidélité encodant l'empreinte cryptographique du transit temporel.
- Système de sécurité reCAPTCHA temporel interactif :
  - Remplacement des capteurs biométriques par un moteur de reCAPTCHA intégré.
  - Boîte de sélection officielle "Je ne suis pas un robot".
  - Fenêtre modale de défi visuel générant un code alphanumérique déformé avec bruit graphique aléatoire et fonction de rafraîchissement.
  - Verrouillage strict de l'approbation du voyage tant que la validation humaine n'est pas certifiée.

### 4. Salle du Verdict & Immersion d'Époque (ResultActivity)
- Restitution du verdict officiel des douanes temporelles :
  - **En cas d'approbation :**
    - Animation d'expansion et rotation du bouclier vert émeraude.
    - Compteur numérique dynamique simulant le défilement accéléré des années jusqu'à la destination.
    - Présentation de la carte d'époque détaillant le mode de vie réel des populations à cette date.
  - **En cas de refus / arrestation :**
    - Animation de chute brutale avec rebond du sceau d'interdiction.
    - Secousse sismique de l'écran (tremblement haptique visuel).
    - Clignotement d'alerte rouge et placement immédiat sous mandat de détention temporelle.
- Bouton de réinitialisation permettant de clore la session et de revenir à la borne initiale pour un nouveau voyageur.

---

## Moteurs Techniques et Innovations

### 1. Moteur Graphique Cinématique 60fps (AnimatedBackgroundView)
Composant de rendu personnalisé exploitant l'accélération matérielle du `Canvas` Android sans allocation d'objets durant le cycle de dessin pour garantir une fluidité constante à 60 images par seconde :
- **Effet Caméra Ken Burns :** Zoom continu doux et panoramique sinusoïdal sur l'illustration haute résolution de l'époque, donnant vie aux rues, architectures et populations.
- **Surcouche d'animations particulaires en temps réel :**
  - **Époque Antique :** Rayons solaires dorés rotatifs et poussières d'or en suspension.
  - **Époque Médiévale :** Scintillement des torches de garde et braises de forges montant dans la nuit.
  - **Révolution Industrielle :** Engrenages mécaniques d'horlogerie en rotation synchronisée et panaches de vapeur.
  - **Guerres Mondiales :** Projecteurs de DCA balayant le ciel brumeux et cendres de bombardement.
  - **Ère Contemporaine :** Satellite orbital traversant la haute atmosphère et flux d'ondes de télécommunications.
  - **Ère Cybernétique :** Voitures volantes à sustentation magnétique avec traînées de phares cyan et feux magenta, accompagnées de pluie de données numériques.
  - **Civilisation Interstellaire :** Saut hyperspatial 3D avec étoiles étirées à la vitesse de la lumière (Warp Speed).
  - **Refus / Arrestation :** Projecteurs de traque rotatifs à 360 degrés et barrières laser horizontales oscillantes.
- **Vignettage cinématique :** Dégradé sombre assurant un contraste maximal et une lisibilité parfaite des textes et boutons.

### 2. Moteur de Défi reCAPTCHA Visuel (CaptchaGenerator)
- Moteur autonome de rendu bitmap générant des codes de sécurité déformés.
- Application de variations aléatoires d'angles, de couleurs et d'échelles sur chaque caractère.
- Injection de lignes de bruit et de points de dispersion pour bloquer toute résolution automatisée.

### 3. Gestionnaire de Clés d'Intent Dynamiques (IntentKeys)
- Génération programmatique des clés de transmission de données basée sur le matricule étudiant afin d'éviter toute chaîne figée dans le code source.

---

## Référentiel Historique des Époques

La segmentation temporelle respecte la réalité historique et sociale des populations humaines :

| Intervalle Temporel | Nom de l'Époque | Mode de Vie et État des Populations |
| :--- | :--- | :--- |
| **Avant 500** | Époque Antique | Cités de pierre, philosophie naissante, papyrus, artisanat du bronze, légionnaires romains et navigation côtière. |
| **500 à 1491** | Époque Médiévale | Châteaux forts, féodalité, travail de la terre, épidémies de peste, guildes artisanales et cathédrales. |
| **1492 à 1788** | Renaissance & Lumières | Grandes explorations maritimes (caravelles), imprimerie de Gutenberg, essor des sciences, monarchies et cours royales. |
| **1789 à 1913** | Révolution Industrielle | Mines de charbon, essor du chemin de fer, locomotives à vapeur, travail en usines, éclairage au gaz et exode rural. |
| **1914 à 1945** | Guerres Mondiales & Entre-deux-guerres | Conflits armés mondiaux, tranchées, couvre-feux stricts, rationnement alimentaire sévère, reconstruction d'après-guerre et usines d'armement. |
| **1946 à 1989** | Guerre Froide & Après-Guerre | Rideau de fer, tension géopolitique Est-Ouest, menace nucléaire, premiers pas sur la Lune, essor de la télévision et de l'automobile. |
| **1990 à 2026** | Ère Numérique Contemporaine | Internet mondial, ordinateurs personnels, téléphonie mobile, réseaux sociaux, transition énergétique et mondialisation instantanée. |
| **2027 à 2150** | Ère Cybernétique | Mégalopoles suspendues, intelligence artificielle omniprésente, véhicules à lévitation magnétique et énergies à fusion propre. |
| **Après 2150** | Civilisation Interstellaire | Colonies orbitales lointaines, voyages supraluminiques, téléportation quantique, Dyson-sphères et terraformation galactique. |
| **Tous temps (Refus)** | Arrestation Temporelle | État d'arrestation immédiat, patrouilles des douanes en exosquelettes, barrières laser de haute sécurité et confinement préventif. |

---

## Respect des Contraintes Académiques

- **Nomenclature des Identifiants :** L'intégralité des identifiants XML de tous les fichiers de mise en page se termine obligatoirement par le suffixe `_yf` (initiales de l'étudiant).
- **Commentaires en Arabe Numérotés :** Chaque fichier source Java intègre des commentaires explicatifs structurés en langue arabe avec une numérotation séquentielle continue (`// 1. ... // 27.`).
- **Contrainte d'Architecture du Contrôle :** Respect strict de la disposition du passeport temporel en `LinearLayout` horizontal avec une `ImageView` gauche de poids 0.3 et un `RelativeLayout` droit de poids 0.7.
- **Règles Temporelles Métier :**
  - Contrôle strict de l'année de naissance interdisant les valeurs supérieures à l'année courante.
  - Calcul dynamique de l'âge et de la trajectoire chronologique.
  - Détection et neutralisation des motifs de voyage paradoxaux.
- **Sobriété Visuelle :** Absence totale d'émojis dans l'interface, les chaînes de ressources et la documentation, conformément aux consignes.

---

## Structure du Projet

```text
DouaneTemporelle/
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/douanetemporelle/
│   │   │   │   ├── AnimatedBackgroundView.java       # Moteur Canvas d'arrière-plan animé 60fps & 9 époques
│   │   │   │   ├── CaptchaGenerator.java             # Générateur de reCAPTCHA visuel déformé
│   │   │   │   ├── IntentKeys.java                   # Génération dynamique des clés d'Intent
│   │   │   │   ├── IntroActivity.java                # Écran de lancement (Splash Screen)
│   │   │   │   ├── MainActivity.java                 # Formulaire voyageur & détection d'anomalies
│   │   │   │   ├── ResultActivity.java               # Verdict douanier, saut animé & thématique d'époque
│   │   │   │   └── VerificationActivity.java         # Poste biométrique, code-barres & reCAPTCHA
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       │   ├── bg_era_antique.jpg            # Arrière-plan cinématique Antiquité (Forum romain)
│   │   │       │   ├── bg_era_medieval.jpg           # Arrière-plan cinématique Moyen Âge (Marché & château)
│   │   │       │   ├── bg_era_industriel.jpg         # Arrière-plan cinématique Révolution Industrielle
│   │   │       │   ├── bg_era_guerre.jpg             # Arrière-plan cinématique Guerres Mondiales (1914-1945)
│   │   │       │   ├── bg_era_contemporain.jpg       # Arrière-plan cinématique Ère Contemporaine
│   │   │       │   ├── bg_era_cybernetique.jpg       # Arrière-plan cinématique Ère Cybernétique (An 2100)
│   │   │       │   ├── bg_era_interstellaire.jpg     # Arrière-plan cinématique Civilisation Interstellaire
│   │   │       │   ├── bg_era_arrestation.jpg        # Arrière-plan cinématique Détention & Arrestation
│   │   │       │   ├── bg_card_verdict_approved.xml  # Carte verre sombre translucide émeraude
│   │   │       │   ├── bg_card_verdict_refused.xml   # Carte verre sombre translucide rubis
│   │   │       │   ├── bg_scanner_desk.xml           # Fond vectoriel du poste d'inspection
│   │   │       │   ├── bg_recaptcha_box.xml          # Encadré du défi reCAPTCHA
│   │   │       │   ├── ic_recaptcha_checked.xml      # Case reCAPTCHA cochée
│   │   │       │   ├── ic_recaptcha_unchecked.xml    # Case reCAPTCHA vierge
│   │   │       │   ├── ic_recaptcha_logo.xml         # Logo officiel reCAPTCHA
│   │   │       │   ├── ic_verdict_approved.xml       # Sceau du saut temporel approuvé
│   │   │       │   ├── ic_verdict_arrest.xml         # Sceau de l'arrestation temporelle
│   │   │       │   └── ...                           # Autres ressources vectorielles et icônes
│   │   │       ├── layout/
│   │   │       │   ├── activity_intro.xml            # Mise en page de l'écran d'introduction
│   │   │       │   ├── activity_main.xml             # Mise en page du formulaire d'enregistrement
│   │   │       │   ├── activity_verification.xml     # Mise en page du poste de contrôle (0.3 / 0.7)
│   │   │       │   ├── activity_result.xml           # Mise en page de la salle de verdict
│   │   │       │   └── dialog_recaptcha_challenge.xml# Boîte de dialogue du défi reCAPTCHA
│   │   │       ├── values/
│   │   │       │   ├── colors.xml                    # Palette temporelle (Cyan, Émeraude, Ambre, Rubis)
│   │   │       │   ├── strings.xml                   # Textes et protocoles de la Douane Temporelle
│   │   │       │   └── themes.xml                    # Thème sombre immersif de l'application
│   │   │       └── mipmap/                           # Icônes de l'application
├── build.gradle.kts
├── settings.gradle.kts
└── README.md                                         # Documentation officielle du projet
```

---

## Technologies et Environnement

- **Plateforme :** Android
- **Langage de Programmation :** Java (avec compatibilité Java 11 / 17)
- **Interface Utilisateur :** XML natif (Material Components & AndroidX)
- **Niveau d'API :** `minSdk = 35`, `targetSdk = 37`, `compileSdk = 37`
- **Moteur de Construction :** Gradle 8.11 avec Kotlin DSL (`build.gradle.kts`)
- **Accélération Graphique :** Hardware-accelerated Canvas 60fps avec `ValueAnimator`

