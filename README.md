# Whac-a-Mole
## SZTE-TTIK Mobil alkalmazásfejlesztés beadandó
Választott téma: 93 - Reflex tesztelő játék (billentyűzet használata nélkül)

Kiértékeléshez segítség:
| Elem | proof |
| --- | --- |
| Fordítási hiba nincs | nyilván |
| Futtatási hiba nincs | nyilván |
| Firebase autentikáció meg van valósítva: | Regisztáció és bejelentkezés google fiókkal és emaillel is meg van oldva; Reg: Google - A onetapclient bejelentkezés intézi, ha nem volt még regisztráció , Email: `RegisterActivity.registerWithEmail()`; Login: Google - `MainActivity.signInWithGoogleAccount()` , Email: `LoginActivity.loginWithEmail()` |
| Adatmodell definiálása (class vagy interfész formájában) | Player class |
| Legalább 3 különböző activity használata  | 6 activity van |
| Beviteli mezők beviteli típusa megfelelő (jelszó kicsillagozva, email-nél megfelelő billentyűzet jelenik meg stb. | res/layout mappában van minden, ami tartalmaz beviteli mezőt: login, editlogin, register |
| ConstraintLayout és még egy másik layout típus használata | ConstraintLayoutra példa a main layout-ja, LinearLayoutra példa a list_item.xml |
| Reszponzív | Minden layout-hoz csináltam horizontal és vertical verziót is, illetve a game activity-hez tablet nézet is van. Sajnos tesztelni már nem tudtam őket valós eszközön, ilyen ez az almás élet... |
| Legalább 2 különböző animáció használata | A vakondok fel és le mozgása 1-1 animáció amit külső libbel oldottam meg (`com.daimajia.androidanimations:library:2.4@aar`). Lásd: `GameActivity.moleGoesAway()` vagy `GameActivity.showUpMole()` ahol az animációt maga a `YoYo` osztály szolgáltatja |
| Intentek használata: navigáció meg van valósítva az activityk között (minden activity elérhető) | A puding próbája az evés |
| Lifecycle Hookok | pl MainActivity-ben `onResume()`, `onRestart()` ahol újravalidálom a user-t |
| Permission-ös erőforrás | Ha leütsz egy vakondot akkor rezeg a telefon. Manifest-ben "android.permission.VIBRATE", használata GameActivity-ben `hitAMole` -> `vibe.vibrate(100)` |
| Notification használata | RegisterActivity-ben dob egyet ha új email-es profilt regisztrálunk illetve az első játékkor eltűnik. |
| CRUD műveletek | A PlayerDAO osztály végzi ezeket és AsyncTask helyett egy Callback osztályt használtam |
| 2 komplex firestore lekérdezés | Szintén PlayerDAO-ban `getUsersByOneScoreAndAscendingByName`, `getTop10` |


