# Phase 9: Gra Dnia — mini-gry pszczelarskie

> **Depends on:** Phase 4 (animations/haptics/settings), Phase 5 (ekrany + nawigacja), Phase 8 (feedback i bonusy quizowe)
> **Goal:** Dodać nową sekcję "Gra Dnia" na HomeScreen i zaoferować lekkie, dzienne mini-gry z pszczelarskim motywem zamiast klasycznego quizu.

---

## Cel

Dodać do aplikacji lekki, codzienny tryb rozrywki, który:
- wprowadza nowy typ aktywności („Gra Dnia”), nie zastępując quizu
- zapewnia prostą, angażującą mini-grę z estetyką pszczelarską
- wykorzystuje istniejącą architekturę Compose Multiplatform, route i ustawienia
- nie wymaga dodatkowego backendu ani rozbudowanej bazy pytań

## Dlaczego to pasuje

Obecnie aplikacja ma mocną sekcję quizową i gamifikację przez ranking + streaki. "Gra Dnia" to naturalne rozszerzenie: dodaje codzienną aktywność i bardziej „game-like” doświadczenie, które zachęca do regularnego otwierania aplikacji.

## Proponowane mini-gry

### 1. Pszczeli labirynt

Opis:
- plansza 5×5 lub 6×6 pól
- start: pszczoła przy ulu, cel: kwiatek / nowy ul
- przeszkody: pajęczynki, krople deszczu, kamienie
- ruch: tap na pole sąsiednie (góra/dół/lewo/prawo)

Wygląd:
- siatka pól z animowaną postacią pszczoły
- kliknięte pole podświetla drogę
- po poprawnym dotarciu do celu: efekt konfetti i punktacja

Mechanika:
- gra dnia to jednorazowy układ wygenerowany na podstawie daty
- wystarczy jeden poziom trudności, przyjazny dla początkujących
- ocena: liczba ruchów lub "ukończone" / "nie ukończone"

### 2. Pary pszczelarskie (Memory)

Opis:
- klasyczna gra memory z 6–8 parami kart
- pary tworzą powiązania tematyczne: choroba ↔ leczenie, kwiat ↔ zapylacz, ul ↔ zadanie

Wygląd:
- prosty grid kart z maskownicą
- po odkryciu dwóch kart: dopasowanie kolorami i animacją
- po ukończeniu: ocena czasu i liczby prób

Mechanika:
- zawiera edukacyjny feedback w opisie pary
- najlepsza do 2–3 minut rozgrywki; niska bariera wejścia

### 3. Flappy Bee (Flappy Bird-style)

Opis:
- ciągły tryb zręcznościowy: pszczoła leci w prawo, przeszkody w postaci rogów plastra miodu lub kropli rosy
- tap powoduje krótki skok w górę, bez tap spada w dół
- celem jest przejście jak największej liczby przeszkód

Wygląd:
- poziomy, kolorowy krajobraz łąki / ula
- ruchoma tło / przesuwające się proste przeszkody
- pasujące ikony pszczoły i miodowych elementów

Mechanika:
- użyć prostego update loop w `LaunchedEffect` zamiast pełnej fizyki
- kolizja detekowana w jednej osi (Y vs proste prostokąty)
- wynik: liczba przejść lub dystans

### 4. Łamigłówka sekwencji (pattern memory)

Opis:
- wyświetl trzyetapową sekwencję symboli pszczelich (kwiaty, loty, emotikony)
- użytkownik powtarza kolejność przez tapnięcia

Wygląd:
- prosty pasek sekwencji, pola z ikonami
- animowany podświetlenie kroku
- po błędzie: podpowiedź i możliwość spróbowania ponownie

Mechanika:
- bardzo niska bariera wejścia, dobra jako „gra dnia” dla nowych użytkowników
- może być nagrodzona animacją ula i dźwiękiem

---

## Proponowany UX flow

1. HomeScreen:
   - dodaj nową `ActionCard` "Gra Dnia" obok istniejącej karty "Zagraj"
   - podtytuł: "Codzienna krótka gra z pszczelarskim twistem"
   - badge/punktacja: "Dzisiaj: Flappy Bee" / "Dzisiaj: Labirynt"

2. Po tapnięciu: `Route.GameOfDay` → `GameOfDayScreen`

3. Ekran główny gry dnia:
   - tytuł dnia i opis gry
   - „Start” + opcja „Zobacz instrukcję”
   - widok wyników dnia, jeśli gra była ukończona wcześniej
   - przycisk "Zagraj ponownie" (opcjonalnie, ale wynik dnia liczy się tylko raz)

4. Po zakończeniu:
   - ekran wyniku z gratulacjami
   - mini-badge za ukończenie dnia
   - przycisk "Powrót" do Home

5. Dzienna zmiana:
   - wybór gry oparty o lokalną datę (`LocalDate.now()`)
   - np. `GameOfDayType.fromDate(date)`
   - dzięki temu każdy dzień ma unikalny wariant

---

## Architektura i pliki

### Nowe pliki / moduły UI

- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/navigation/Route.kt`
  - dodać `data object GameOfDay : Route`

- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/screens/GameOfDayScreen.kt`
  - ekrany: lista instrukcji + gra + wynik

- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/presentation/gameofday/GameOfDayViewModel.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/presentation/gameofday/GameOfDayState.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/presentation/gameofday/GameOfDayIntent.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/presentation/gameofday/GameOfDayEffect.kt`

- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/GameOfDayCard.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/GameOfDayBoard.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/FlappyBeeView.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/MemoryPairsBoard.kt`

### Wspólne modele / losowanie gry dnia

- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/gameofday/GameOfDayType.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/gameofday/GameOfDaySeed.kt`
- `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/gameofday/GameOfDayGenerator.kt`

### Integracja w `AppNavigation.kt`
- nowa trasa w `AnimatedContent` i `when` dla `Route.GameOfDay`
- przekazanie `LocalSettingsState` i `LocalNotificationScheduler` jak dotąd

### Minimalny dodatkowy shared state

Jeżeli chcesz utrzymać cron logic w `shared`, można dodać:
- `GameOfDayType` w commonMain `shared` albo w `composeApp` (najprościej w UI)
- lecz warto zachować większość logiki gry w `composeApp`, bo to UI-first feature

---

## Szczegóły implementacji gry Flappy Bee

### Cel
Dodać prostą, przyjemną mini-grę tap-to-fly która ma charakter dayli challenge.

### Rozgrywka
- Bee porusza się w poziomie na stałej prędkości w prawo
- ekran przesuwa się w lewo w regularnych krokach
- jedno tapnięcie podnosi pszczołę o jedną „poziomkę"
- grawitacja powoduje opadanie, gdy użytkownik nie tapie
- przeszkody to pionowe szczeliny w plastry miodu / liście

### UI
- prosty `Canvas` lub `Box` z pszczołą i przeszkodami
- licznik punktów w lewym górnym rogu
- przycisk pauzy / restart
- tło łąki z delikatną animacją kursu

### Dlaczego taki wybór
- wyróżnia się w świecie quizów jako mini-rozrywka
- daje natychmiastową rozgrywkę i łatwy do zapamiętania feedback
- można go ograniczyć do jednego trybu na dzień, więc nie rozbudowuje się w grę arcade

### Krok implementacji
1. stwórz `FlappyBeeView` z `Canvas` i `LaunchedEffect` loop
2. dodaj prosty `beeY` float + `velocity` + `gravity`
3. generuj przeszkody co X sekund
4. kolizję sprawdzaj prostokąt vs prostokąt
5. po kolizji wyświetl wynik i przekaż `GameOfDayResult`

---

## Przykład rozkładu dnia

| Dzień tygodnia | Gra Dnia | Cel | Wrażenie | Czas | Dodatki |
|---|---|---|---|---|---|
| poniedziałek | Pszczeli labirynt | ukończ trasę | logiczna, spokojna | 1–2 min | animowany ruch bumble-bee |
| wtorek | Pary pszczelarskie | dobranie par | edukacyjna, pamięć | 1–2 min | feed |
| środa | Flappy Bee | przetrwaj jak najdłużej | zręcznościowa | 1–3 min | rosnący dystans |
| czwartek | Sekwencja | powtórz sekwencję | pamięć wzrokowa | <1 min | prosta instrukcja |
| piątek | Mix: 2 krótkie zadania | 2 mini-gry | urozmaicenie | 2–3 min | nagroda dnia |

---

## Co dodać do istniejącej warstwy motywów i animacji

- nowe ikony / obrazki: pszczoła, kwiatek, pajęczyna, plaster miodu
- nowe kolory akcentu dla sekcji "Gra Dnia"
- zachowaj istniejące `AppTheme.spacing` i `AppColors` dla spójności
- użyj istniejących toggle sound/haptics w `GameOfDayScreen` (jeżeli `soundEnabled` → gra odtwarza dźwięk tapu i success)

---

## Plan wdrożenia w commitach

### 1. `feat(game-of-day): add route and HomeScreen card`
- `Route.GameOfDay`
- `HomeScreen` — nowa karta `Gra Dnia`
- `AppNavigation` — nawigacja do nowej trasy
- prosty placeholder `GameOfDayScreen` z „coming soon"

### 2. `feat(game-of-day): add GameOfDay screen + daily selection`
- `GameOfDayViewModel`, `GameOfDayState`, `GameOfDayIntent`
- `GameOfDayType.fromDate()`
- UI: tytuł, opis, „Start” button, dzisiejszy wariant

### 3. `feat(game-of-day): add Flappy Bee mini-game`
- `FlappyBeeView`
- gra tap-to-fly + przeszkody + wynik
- integracja z `GameOfDayScreen`
- efekt dźwiękowy/haptics dla tapu i kolizji

### 4. `feat(game-of-day): add Maze + Memory modes`
- `GameOfDayType` rozszerzony o `Maze`, `Memory`
- `MazeBoard`, `MemoryPairsBoard`
- opisy i instrukcje w `GameOfDayScreen`

### 5. `feat(game-of-day): add daily reward + badge`
- wynik dnia zapisany lokalnie jako `GameOfDayResult`
- pokazanie małego badge na HomeScreen
- możliwość powtórzenia tylko dla zabawy, ale wynik zapisany raz dziennie

---

---

## Szczegółowe zadania dla developera (krok po kroku)

### ✅ COMMIT 1: `feat(game-of-day): add route and HomeScreen card` (~30 min)

#### Zadanie 1.1: Dodaj Route.GameOfDay
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/navigation/Route.kt`
- **Kod:** `data object GameOfDay : Route`
- **Checklist:** Route kompiluje się, widoczna w całej aplikacji

#### Zadanie 1.2: Dodaj kartę na HomeScreen
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/screens/HomeScreen.kt`
- **Wymagania:**
  - Nowa `ActionCard` pomiędzy "Zagraj" a "Ranking"
  - Emoji: 🎮, Tytuł: "Gra Dnia", Podtytuł: "Codzienna gra"
  - OnClick → effect NavigateToGameOfDay
- **Checklist:** Karta widoczna, odpowiedni layout, clickable

#### Zadanie 1.3: Dodaj obsługę w AppNavigation
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/navigation/AppNavigation.kt`
- **Wymagania:**
  - Case w `AnimatedContent` dla `Route.GameOfDay`
  - Renderuj placeholder `GameOfDayScreen()`
  - Animacja fade + slide
- **Checklist:** Ekran otwiera się, wracanie do Home działa

---

### ✅ COMMIT 2: `feat(game-of-day): add GameOfDay screen + daily selection` (~65 min)

#### Zadanie 2.1: Stwórz modele MVI
- **Pliki:**
  - `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/presentation/gameofday/GameOfDayType.kt`
  - `GameOfDayState.kt`
  - `GameOfDayIntent.kt`
  - `GameOfDayEffect.kt`

**GameOfDayType.kt:**
```kotlin
enum class GameOfDayType {
    FlappyBee, Maze, Memory, Sequence;
    
    companion object {
        fun fromDate(date: LocalDate = LocalDate.now()): GameOfDayType {
            val dayOfWeek = date.dayOfWeek.value
            return when (dayOfWeek) {
                1, 3, 6 -> FlappyBee    // Mon, Wed, Sat
                2 -> Maze
                4 -> Memory
                5 -> Sequence
                else -> FlappyBee
            }
        }
    }
    
    fun title() = when (this) {
        FlappyBee -> "Flappy Bee"
        Maze -> "Pszczeli labirynt"
        Memory -> "Pary pszczelarskie"
        Sequence -> "Sekwencja kwiatów"
    }
    
    fun description() = when (this) {
        FlappyBee -> "Pomagaj pszczołce lecieć między przeszkodami!"
        Maze -> "Poprowadź pszczołę do celu..."
        Memory -> "Dopasuj pary kart..."
        Sequence -> "Powtórz sekwencję..."
    }
}
```

**GameOfDayState.kt:**
```kotlin
data class GameOfDayState(
    val todayType: GameOfDayType = GameOfDayType.FlappyBee,
    val gameStatus: GameStatus = GameStatus.Menu,
    val score: Int = 0,
    val bestScore: Int = 0,
    val isCompleted: Boolean = false,
)

enum class GameStatus { Menu, Playing, GameOver, Won, Paused }
```

**GameOfDayIntent.kt:**
```kotlin
sealed interface GameOfDayIntent {
    data object LoadGameOfDay : GameOfDayIntent
    data object StartGame : GameOfDayIntent
    data object ShowInstructions : GameOfDayIntent
    data object HideInstructions : GameOfDayIntent
    data class EndGame(val score: Int) : GameOfDayIntent
    data object ResetGame : GameOfDayIntent
    data object ExitGame : GameOfDayIntent
}
```

**GameOfDayEffect.kt:**
```kotlin
sealed interface GameOfDayEffect {
    data object NavigateBack : GameOfDayEffect
    data class ShowSnackbar(val message: String) : GameOfDayEffect
}
```

#### Zadanie 2.2: Stwórz GameOfDayViewModel
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/presentation/gameofday/GameOfDayViewModel.kt`
- **Wymagania:**
  - Extend `MviViewModel<GameOfDayState, GameOfDayIntent, GameOfDayEffect>`
  - `LoadGameOfDay` ustala `todayType = GameOfDayType.fromDate()`
  - `StartGame` zmienia status na `Playing`
  - `EndGame(score)` zapisuje wynik i zmienia status
  - `ExitGame` emituje NavigateBack effect

#### Zadanie 2.3: Stwórz GameOfDayScreen
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/screens/GameOfDayScreen.kt`
- **Wymagania:**
  - Menu state: tytuł, opis, "Start", "Instrukcja"
  - Placeholder dla Playing state (do commit 3)
  - GameOver state: wynik, "Powrót"
  - Back button zawsze widoczny
- **UI inspiracja:**
  ```
  ┌──────────────────┐
  │ [< Wróć]         │
  │                  │
  │ 🎮 Flappy Bee    │
  │ Leci w prawo...  │
  │                  │
  │ [Instrukcja]     │
  │ [Start gry]      │
  │                  │
  │ Wynik: -         │
  └──────────────────┘
  ```

---

### ✅ COMMIT 3: `feat(game-of-day): add Flappy Bee mini-game` (~90 min)

#### Zadanie 3.1: Stwórz FlappyBeeView
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/FlappyBeeView.kt`
- **Wymagania:**
  - `Canvas` lub series `Box`-ów z pszczołą
  - **Physics:**
    - `gravity = 0.5f` (pixel/frame)
    - `flapPower = -12f` (skok)
    - `updateInterval = 16ms` (~60fps)
  - **Przeszkody:** generuj co 100 frames, przerwa 80px, X speed 4px/frame
  - **Kolizja:** AABB (bee vs obstacle)
  - **Tap:** detectTapGestures zmienia velocity
- **Score:** liczba przejść

**Pseudo-kod:**
```kotlin
@Composable
fun FlappyBeeView(
    onGameOver: (score: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var beeY by remember { mutableStateOf(200f) }
    var velocity by remember { mutableStateOf(0f) }
    var score by remember { mutableStateOf(0) }
    var obstacles by remember { mutableStateOf<List<Obstacle>>(emptyList()) }
    var gameOver by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        while (!gameOver) {
            // Physics update
            velocity += 0.5f  // gravity
            beeY += velocity
            
            // Obstacle generation
            // Collision check
            // Score update
            
            delay(16)
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { velocity = -12f }
            }
    ) {
        // draw bee, obstacles, score
    }
}
```

- **Checklist:**
  - Bee spada i podlata
  - Przeszkody generują się i poruszają
  - Kolizja kończy grę
  - Score liczy się prawidłowo
  - Tap response jest natychmiastowy

#### Zadanie 3.2: Integruj z GameOfDayScreen
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/screens/GameOfDayScreen.kt`
- **Wymagania:**
  - Gdy `gameStatus == Playing && todayType == FlappyBee` → `FlappyBeeView`
  - `onGameOver` emituje `GameOfDayIntent.EndGame(score)`

#### Zadanie 3.3: Dodaj dźwięki (opcjonalnie na MVP)
- Jeśli `settingsState.soundEnabled`:
  - Tap sound
  - Game over sound
  - Punkt sound (opcjonalnie)
- Jeśli `settingsState.hapticsEnabled`:
  - Light impact na tap
  - Error impact na kolizję

---

### ✅ COMMIT 4: `feat(game-of-day): add Maze + Memory modes` (~120 min)

#### Zadanie 4.1: Stwórz MazeBoard
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/MazeBoard.kt`
- **Wymagania:**
  - Plansza 6×6 z przeszkodami
  - Start (0,0), Cel (5,5)
  - Tap na sąsiednie pole → poruszenie
  - Kolizja z przeszkodą jest blokowana
  - Score = liczba ruchów
- **Data generation:**
  ```kotlin
  val seed = LocalDate.now().dayOfYear
  val random = Random(seed)
  // generuj przeszkody deterministycznie
  ```

#### Zadanie 4.2: Stwórz MemoryPairsBoard
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/ui/components/MemoryPairsBoard.kt`
- **Wymagania:**
  - 4×4 grid (8 par)
  - Tap flips card
  - Match = zostaje odkryte
  - No match = flip back
  - Score = czas + liczba prób
- **Pary:**
  ```kotlin
  val pairs = listOf(
      "🐝 Nosema" to "🏥 Płynne mleczko",
      // itp 7 par
  )
  ```

#### Zadanie 4.3: Integruj w GameOfDayScreen
- **Wymagania:**
  ```kotlin
  when (state.todayType) {
      FlappyBee -> FlappyBeeView(...)
      Maze -> MazeBoard(...)
      Memory -> MemoryPairsBoard(...)
      Sequence -> PlaceholderScreen()
  }
  ```

---

### ✅ COMMIT 5: `feat(game-of-day): add daily reward + badge` (~70 min)

#### Zadanie 5.1: Stwórz GameOfDayRepository
- **Plik:** `composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/data/gameofday/GameOfDayRepository.kt`
- **Wymagania:**
  ```kotlin
  interface GameOfDayRepository {
      suspend fun getTodayResult(): GameOfDayResult?
      suspend fun saveTodayResult(result: GameOfDayResult)
  }
  
  data class GameOfDayResult(
      val date: LocalDate,
      val gameType: GameOfDayType,
      val score: Int,
      val timestamp: Long = currentTimeMillis(),
  )
  ```
- **Implementacja:** MultiplatformSettings z JSON serialization

#### Zadanie 5.2: Uaktualnij GameOfDayViewModel
- Załaduj wynik w `LoadGameOfDay`
- Ustaw `isCompleted` jeśli wynik istnieje
- Zapisz wynik w `EndGame`

#### Zadanie 5.3: Uaktualnij HomeScreen
- Pokaż na karcie "Gra Dnia":
  - ✅ badge jeśli `isCompleted`
  - Wynik punktów
  - "Nowa gra" jeśli !isCompleted
- **UI:**
  ```
  ActionCard(
      title = "Gra Dnia",
      badge = if (isCompleted) "✅" else null,
      description = "Wynik: ${score ?: "---"}"
  )
  ```

#### Zadanie 5.4: GameOfDayBadge component (opcjonalnie)
- Mały badge z ikoną na HomeScreen
- Typ gry + wynik

---

## Podsumowanie estymaty

| Commit | Opis | Pliki | Czas |
|--------|------|-------|------|
| 1 | Route + Card + Nav | 3 | 30 min |
| 2 | Models + ViewModel + Screen | 5 | 65 min |
| 3 | Flappy Bee | 1 + integracja | 90 min |
| 4 | Maze + Memory | 2 + integracja | 120 min |
| 5 | Persistence + Badge | 3 | 70 min |
| **MVP** | **(Commit 1-3)** | | **~185 min (3h)** |
| **FULL** | **(Commit 1-5)** | | **~375 min (6.5h)** |

---

## Ryzyka i mitigacje

### Ryzyko: złożoność Flappy Bee w Compose
- `Mitigacja:` proste ustawienia ruchu, brak zaawansowanej fizyki, użycie dyskretnego loopa w `LaunchedEffect`

### Ryzyko: rozdzielenie od quizu
- `Mitigacja:` zachowaj osobną trasę i osobne stany, żadna istniejąca logika quizu nie zmienia się

### Ryzyko: nadmierny interfejs
- `Mitigacja:` rozpocznij od jednego wariantu (Flappy Bee) i dodaj kolejne tylko po wyraźnym MVP

### Ryzyko: brak czasu na wykonanie
- `Mitigacja:` najpierw tylko `HomeScreen` + `GameOfDayScreen` + `Flappy Bee` jako minimalny, reszta jako późniejszy dodatek

---

## Wymagane decyzje architektoniczne

- `GameOfDay` powinien trzymać logikę tylko w `composeApp`, bo to UI-heavy feature.
- `GameOfDay` może korzystać z istniejących ustawień (sound/haptics) przez `LocalSettingsState`.
- `GameOfDay` typy mogą być deterministycznie generowane z daty, bez backendu.
- funkcja `play again` powinna być dostępna, ale wynik dnia powinien być licznikiem danego dnia, nie historyczną tabelą.

---

## Summary

**Propozycja:** dodać nową sekcję "Gra Dnia" na `HomeScreen` i jedną lekko grywalną mini-grę `Flappy Bee`, z możliwością rozszerzenia do `Maze`, `Memory`, `Sequence`.

**Zalety:**
- lepsza retencja dzięki codziennej aktywności
- krótkie wciągające sesje poza quizem
- wykorzystanie istniejącej architektury nawigacji i ustawień
- minimalne wymagania backendowe

**Najlepsze MVP:** `Route.GameOfDay` + `GameOfDayScreen` + `FlappyBeeView`.
