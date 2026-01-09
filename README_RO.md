# 🌅 SUN - Tattva Calculator

Aplicație modernă pentru calcularea Tattvelor vedice, bazată pe ora de răsărit.

## ✨ Caracteristici

- ✅ **Calcul Tattva** - 5 tatvas ciclice (24 minute fiecare)
- ✅ **Calcul SubTattva** - 5 sub-tatvas (4.8 minute fiecare)
- ✅ **Ore Planetare** - 12 ore zi + 12 ore noapte
- ✅ **Nitya** - 15 nityas bazate pe poziția Lunii
- ✅ **GPS Modern** - Compatible cu Android 11-14
- ✅ **Material Design 3** - UI modern și colorat
- ✅ **Salvare Locații** - Database cu locații favorite

---

## 📱 Cerințe Sistem

- **Android:** 8.0 (API 26) - 14 (API 34)
- **Spațiu:** ~50 MB (cu fișiere Swiss Ephemeris)
- **Permisiuni:** GPS (opțional)

---

## 🚀 Instalare și Rulare

### Pas 1: Instalează Android Studio

1. Descarcă **Android Studio** de la: https://developer.android.com/studio
2. Instalează cu setările default
3. La primul run, descarcă SDK-urile necesare (Android 8-14)

### Pas 2: Clonează Proiectul

```bash
git clone https://github.com/karen20dec4/SUN.git
cd SUN
```

### Pas 3: Deschide în Android Studio

1. **File** → **Open** → Selectează folderul `SUN`
2. Așteaptă să termine Gradle sync (2-5 minute)

### Pas 4: Configurează Swiss Ephemeris

**IMPORTANT:** Trebuie să ai fișierele Swiss Ephemeris!

#### Opțiunea A: Copiază din versiunea veche
```bash
# Dacă ai versiunea veche în /sdcard/Sun/Swisseph/data/
# Copiază fișierele .se1 în:
app/src/main/assets/sweph/data/

# Structura:
# app/src/main/assets/sweph/data/
#   ├── seas_18.se1
#   ├── semo_18.se1
#   ├── sepl_18.se1
#   └── ... (toate fișierele .se1)
```

#### Opțiunea B: Descarcă de pe site-ul oficial
1. Mergi la: https://www.astro.com/swisseph/
2. Descarcă fișierele ephemeris
3. Pune-le în `app/src/main/assets/sweph/data/`

### Pas 5: Adaugă Swiss Ephemeris Library

**IMPORTANT:** Trebuie să ai `swisseph.jar`!

1. Creează folderul: `app/libs/` (dacă nu există)
2. Copiază `swisseph.jar` în `app/libs/`
3. În Android Studio: **File** → **Project Structure** → **Dependencies** → **Add JAR**

**Dacă nu ai `swisseph.jar`:**
- Caută în proiectul vechi
- SAU descarcă de pe: https://github.com/ivoras/swisseph (compilează-l)

### Pas 6: Rulează Aplicația

#### Pe Emulator:
1. În Android Studio: **Device Manager** → **Create Virtual Device**
2. Alege: **Pixel 6** cu **Android 13** (API 33)
3. Click pe **▶ Run**

#### Pe Telefon Real:
1. Activează **Developer Options** pe telefon:
   - Setări → Despre telefon → Tap 7 ori pe "Build number"
2. Activează **USB Debugging**
3. Conectează telefonul la PC
4. În Android Studio, selectează telefonul din listă
5. Click pe **▶ Run**

---

## 🎯 Cum se Folosește

### Ecranul Principal

```
┌─────────────────────────────────┐
│ Data: 08-11-2025                │
│ Ora: 18:52:01                   │
│ Locație: București              │
│ Răsărit: (+) 06:30:00           │
│ Apus: (+) 17:15:00              │
├─────────────────────────────────┤
│ [TATTVA Card - fundal colorat]  │
│ [SUBTATTVA Card]                │
│ [PLANETĂ Card]                  │
│ [NITYA Card]                    │
├─────────────────────────────────┤
│ [x] Afișare coduri    Switch    │
│ [Schimbă Locația]     Buton     │
└─────────────────────────────────┘
```

- **Actualizare automată** la fiecare 800ms
- **Culori dinamice** pentru fiecare Tattva
- **Toggle coduri** (G, O, A, U, L) în loc de nume

### Ecranul Locații

1. **Editează locația:**
   - Nume, Longitudine, Latitudine, Altitudine, Time Zone
   - Toggle DST (Daylight Saving Time)

2. **GPS:**
   - Click pe "Obține GPS" pentru locația curentă
   - Acordă permisiuni la primul click

3. **Salvează:**
   - Salvează locația în database pentru mai târziu

4. **Accept:**
   - Aplică locația și revino la ecranul principal

5. **Lista locații:**
   - Click pe o locație pentru a o edita
   - Click pe 🗑️ pentru a o șterge

---

## 🐛 Troubleshooting

### ❌ Eroare: "swisseph.jar not found"

**Soluție:**
```bash
# Verifică că există:
app/libs/swisseph.jar

# Dacă nu există, copiază din proiectul vechi sau descarcă-l
```

### ❌ Eroare: "Swiss Ephemeris files missing"

**Soluție:**
```bash
# Verifică că există fișierele .se1:
app/src/main/assets/sweph/data/seas_18.se1
app/src/main/assets/sweph/data/semo_18.se1
# etc.
```

### ❌ GPS nu funcționează

**Verificări:**
1. Acordă permisiuni în: **Setări** → **Apps** → **SUN** → **Permissions** → **Location**
2. Activează GPS-ul pe telefon
3. Verifică că ești în aer liber (nu funcționează în interior)

### ❌ Calcule incorecte

**Verificări:**
1. Time Zone corect?
2. DST activat (dacă e vară)?
3. Coordonate corecte?

### ❌ Aplicația crash-uiește

**Verifică Logcat:**
1. În Android Studio: **View** → **Tool Windows** → **Logcat**
2. Caută erori roșii
3. Trimite-mi screenshot-ul pentru debugging

---

## 📊 Structura Proiectului

```
SUN/
├── app/src/main/
│   ├── java/com/android/sun/
│   │   ├── MainActivity.kt
│   │   ├── domain/calculator/
│   │   │   ├── Supplement.kt
│   │   │   ├── SwissEphWrapper.kt
│   │   │   └── AstroCalculator.kt
│   │   ├── data/
│   │   │   ├── model/
│   │   │   ├── database/
│   │   │   └── repository/
│   │   ├── viewmodel/
│   │   └── ui/
│   │       ├── theme/
│   │       ├── components/
│   │       └── screens/
│   ├── assets/sweph/data/
│   │   └── (fișiere .se1)
│   ├── res/
│   └── AndroidManifest.xml
└── app/libs/
    └── swisseph.jar
```

---

## 🎨 Culori Tattva

| Tattva    | Culoare        | Hex Code |
|-----------|----------------|----------|
| Akasha    | Mov întunecat  | #2C1745  |
| Vayu      | Albastru închis| #000088  |
| Tejas     | Roșu închis    | #880000  |
| Apas      | Gri            | #5A5A5A  |
| Prithivi  | Maro/Galben    | #504008  |

---

## 📞 Contact & Suport

- **GitHub Issues:** https://github.com/karen20dec4/SUN/issues
- **Email:** (adaugă email-ul tău)

---

## 📝 Licență

Acest proiect este open-source.

---

## 🙏 Mulțumiri

- **Swiss Ephemeris** - pentru calculele astronomice
- **Material Design 3** - pentru UI modern
- **Jetpack Compose** - pentru framework-ul UI

---

**Versiune:** 2.0 (Rebuild Kotlin + Compose)
**Data:** 08 Noiembrie 2025
**Autor:** karen20dec4