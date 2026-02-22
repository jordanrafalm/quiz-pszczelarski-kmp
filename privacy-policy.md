# Polityka Prywatności — Quiz Pszczelarski

**Obowiązuje od:** 22 lutego 2026  
**Deweloper:** Rafał Jordan  
**Kontakt:** jordanrafalm@gmail.com  
**Aplikacja:** Quiz Pszczelarski (Android, iOS)

---

## 1. Wstęp

Niniejsza Polityka Prywatności opisuje, jakie dane są zbierane przez aplikację **Quiz Pszczelarski**, w jaki sposób są wykorzystywane oraz jakie prawa przysługują użytkownikowi. Korzystając z aplikacji, akceptujesz zasady opisane w tym dokumencie.

---

## 2. Jakie dane zbieramy

### 2.1 Dane zbierane automatycznie

#### Konto użytkownika (Firebase Anonymous Authentication)
- Aplikacja tworzy **anonimowe konto** bez żadnych danych osobowych (bez adresu e-mail, numeru telefonu, imienia ani nazwiska).
- Generowany jest losowy identyfikator użytkownika (UID) — nie jest powiązany z żadną tożsamością.
- Użytkownik może samodzielnie wybrać **pseudonim** (nick), który będzie widoczny w rankingu. Nick nie jest weryfikowany i nie musi zawierać prawdziwych danych.

#### Wyniki quizów (Firebase Firestore)
- Wyniki gier (liczba punktów, liczba rozegranych quizów) przechowywane są w bazie danych w chmurze.
- Dane te są widoczne w globalnym rankingu pod wybranym pseudonimem.

#### Dane analityczne (Firebase Analytics)
Zbieramy anonimowe zdarzenia:
- `quiz_started` — rozpoczęcie quizu (poziom, liczba pytań)
- `quiz_completed` — ukończenie quizu (czas trwania, wynik)
- `quiz_abandoned` — porzucenie quizu

Dane analityczne są **zanonimizowane** i nie pozwalają na identyfikację konkretnej osoby.

#### Raporty błędów (Firebase Crashlytics)
- W przypadku awarii aplikacji automatycznie wysyłany jest raport techniczny.
- Raport zawiera informacje o urządzeniu (model, wersja systemu), stack trace błędu oraz niestandardowe klucze diagnostyczne (aktualny poziom quizu, tryb gry).
- Raporty **nie zawierają** danych osobowych.

### 2.2 Dane przechowywane lokalnie na urządzeniu

- **Cache pytań** — pytania quizowe zapisywane lokalnie w bazie SQLite w celu działania offline. Dane te nie są wysyłane nigdzie poza odświeżeniem z Firebase Firestore.
- **Ustawienia** — preferencje aplikacji (dźwięk, wibracje, pseudonim, powiadomienia) przechowywane lokalnie i nie opuszczają urządzenia.
- **Kolejka offline** — wyniki quizów rozegranych bez dostępu do internetu przechowywane lokalnie do czasu przywrócenia połączenia.

---

## 3. W jakim celu używamy danych

| Cel | Podstawa prawna |
|-----|----------------|
| Umożliwienie logowania i przechowywania wyników | Wykonanie umowy (art. 6 ust. 1 lit. b RODO) |
| Globalny ranking użytkowników | Uzasadniony interes (art. 6 ust. 1 lit. f RODO) |
| Analiza sposobu korzystania z aplikacji | Uzasadniony interes — poprawa jakości aplikacji |
| Diagnostyka i naprawa błędów | Uzasadniony interes |
| Konfiguracja zdalna aplikacji | Uzasadniony interes |

---

## 4. Udostępnianie danych podmiotom trzecim

Dane zbierane przez aplikację **nie są sprzedawane** ani przekazywane podmiotom trzecim w celach marketingowych.

Korzystamy z następujących usług zewnętrznych (podprzetwarzający):

| Usługa | Dostawca | Cel | Polityka prywatności |
|--------|----------|-----|----------------------|
| Firebase Authentication | Google LLC | Anonimowe logowanie | [Link](https://firebase.google.com/support/privacy) |
| Firebase Firestore | Google LLC | Przechowywanie wyników i pytań | [Link](https://firebase.google.com/support/privacy) |
| Firebase Analytics | Google LLC | Analiza użytkowania | [Link](https://firebase.google.com/support/privacy) |
| Firebase Crashlytics | Google LLC | Raporty błędów | [Link](https://firebase.google.com/support/privacy) |
| Firebase Remote Config | Google LLC | Zdalna konfiguracja aplikacji | [Link](https://firebase.google.com/support/privacy) |

Google LLC przetwarza dane zgodnie z umową o przetwarzaniu danych (Data Processing Agreement) oraz przepisami RODO. Dane mogą być przetwarzane na serwerach w Stanach Zjednoczonych — Google stosuje standardowe klauzule umowne jako zabezpieczenie transferu danych.

---

## 5. Powiadomienia push

Aplikacja może wysyłać **lokalne powiadomienia** przypominające o codziennej nauce. Powiadomienia są generowane przez urządzenie użytkownika — **żadne dane nie są wysyłane na serwery** w celu ich dostarczenia (nie używamy FCM/APNs push).

Użytkownik może w dowolnym momencie wyłączyć powiadomienia:
- **Android:** Ustawienia → Aplikacje → Quiz Pszczelarski → Powiadomienia
- **iOS:** Ustawienia → Powiadomienia → Quiz Pszczelarski

---

## 6. Prawa użytkownika (RODO)

Zgodnie z Rozporządzeniem (UE) 2016/679 (RODO) przysługują Ci następujące prawa:

- **Prawo dostępu** — możesz uzyskać informację o danych przechowywanych na Twój temat.
- **Prawo do usunięcia** — możesz zażądać usunięcia swoich danych (konta, wyników, pseudonimu). Wystarczy skontaktować się pod adresem: jordanrafalm@gmail.com
- **Prawo do sprzeciwu** — możesz sprzeciwić się przetwarzaniu danych analitycznych.
- **Prawo do przenoszenia** — możesz receive dane w ustrukturyzowanym formacie.
- **Prawo do skargi** — jeśli uważasz, że przetwarzanie narusza RODO, możesz złożyć skargę do Prezesa Urzędu Ochrony Danych Osobowych (UODO), ul. Stawki 2, 00-193 Warszawa.

Aby skorzystać z praw, skontaktuj się: **jordanrafalm@gmail.com**

---

## 7. Dzieci

Aplikacja **nie jest skierowana do dzieci poniżej 13 roku życia** i świadomie nie zbiera danych osobowych od dzieci. Jeśli jesteś rodzicem i uważasz, że Twoje dziecko przekazało nam dane osobowe, skontaktuj się z nami.

---

## 8. Bezpieczeństwo danych

- Połączenie z Firebase odbywa się przez szyfrowane protokoły (HTTPS/TLS).
- Anonimowe konta nie zawierają danych umożliwiających identyfikację.
- Dostęp do bazy danych Firestore jest ograniczony regułami bezpieczeństwa — użytkownik może odczytywać/zapisywać tylko własne dane.

---

## 9. Zmiany polityki prywatności

Zastrzegamy sobie prawo do aktualizacji niniejszej Polityki Prywatności. O istotnych zmianach poinformujemy poprzez mechanizm wymuszenia aktualizacji w aplikacji lub powiadomienie przy uruchomieniu. Data ostatniej aktualizacji jest zawsze widoczna na górze tego dokumentu.

---

## 10. Kontakt

W sprawach związanych z prywatnością i wsparciem:

**Rafał Jordan**  
📧 jordanrafalm@gmail.com  
🌐 https://github.com/jordanrafalm/quiz-pszczelarski-kmp  
🆘 https://github.com/jordanrafalm/quiz-pszczelarski-kmp/issues
