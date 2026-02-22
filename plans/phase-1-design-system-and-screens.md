# Phase 1: Design System, Components, Screens & MVI — Implementation Plan

> **Date:** 2026-02-17  
> **Goal:** Replace the Phase 0 gold screen with a full quiz UI: theme system,
> reusable components, 5 screens, MVI state management, and navigation.  
> **Prerequisite:** Phase 0 scaffold (builds and runs on both platforms).  
> **ADRs:** ADR-0001 (Compose MP), ADR-0002 (Design System), ADR-0003 (MVI).

---

## Table of Contents

1. [Project Structure](#a-project-structure)
2. [Phase 1A — Theme & Design Tokens](#phase-1a)
3. [Phase 1B — Reusable Components](#phase-1b)
4. [Phase 1C — MVI Infrastructure & Domain Models](#phase-1c)
5. [Phase 1D — Screens](#phase-1d)
6. [Phase 1E — Navigation & App Shell](#phase-1e)
7. [Phase 1F — Integration & Polish](#phase-1f)
8. [DoR Gaps & Missing Info](#dor-gaps)
9. [Risks & Mitigations](#risks)

---

<a id="a-project-structure"></a>
## A) Project Structure (target state)

```
composeApp/src/commonMain/kotlin/pl/quizpszczelarski/app/
├── App.kt                              # Root composable (theme + navigation)
├── navigation/
│   ├── AppNavigation.kt                # Navigation graph / route switching
│   └── Route.kt                        # Sealed route definitions
├── presentation/
│   ├── base/
│   │   └── MviViewModel.kt            # Base MVI ViewModel
│   ├── home/
│   │   ├── HomeState.kt
│   │   ├── HomeIntent.kt
│   │   └── HomeViewModel.kt
│   ├── quiz/
│   │   ├── QuizState.kt
│   │   ├── QuizIntent.kt
│   │   ├── QuizEffect.kt
│   │   └── QuizViewModel.kt
│   ├── result/
│   │   ├── ResultState.kt
│   │   ├── ResultIntent.kt
│   │   ├── ResultEffect.kt
│   │   └── ResultViewModel.kt
│   └── leaderboard/
│       ├── LeaderboardState.kt
│       ├── LeaderboardIntent.kt
│       └── LeaderboardViewModel.kt
└── ui/
    ├── theme/
    │   ├── AppTheme.kt                 # Root theme composable
    │   ├── AppColors.kt                # Color scheme factory + extended colors
    │   ├── AppTypography.kt            # Typography scale (Inter)
    │   ├── AppShapes.kt                # Shape overrides
    │   └── AppSpacing.kt              # 8dp grid spacing via CompositionLocal
    ├── components/
    │   ├── AppButton.kt                # Primary / Secondary / Tertiary
    │   ├── AnswerOption.kt             # Answer selection component
    │   ├── QuizTopBar.kt               # Top bar with back navigation
    │   ├── QuizProgressBar.kt          # Question progress indicator
    │   ├── ActionCard.kt               # Home screen action card
    │   ├── ResultCard.kt               # Score display card
    │   ├── LeaderboardEntryRow.kt      # Single leaderboard entry
    │   ├── TabSelector.kt              # Two-option tab selector
    │   └── UserPositionCard.kt         # Current user ranking highlight
    └── screens/
        ├── SplashScreen.kt
        ├── HomeScreen.kt
        ├── QuizScreen.kt
        ├── ResultScreen.kt
        └── LeaderboardScreen.kt

shared/src/commonMain/kotlin/pl/quizpszczelarski/shared/
├── domain/
│   ├── model/
│   │   ├── Question.kt                 # Quiz question entity
│   │   ├── QuizSession.kt             # Active quiz session
│   │   ├── QuizResult.kt              # Completed quiz result
│   │   └── LeaderboardEntry.kt        # Ranking entry
│   └── usecase/
│       ├── GetRandomQuestionsUseCase.kt
│       └── CalculateScoreUseCase.kt
├── data/
│   ├── repository/
│   │   └── QuestionRepositoryImpl.kt
│   └── source/
│       └── LocalQuestionDataSource.kt  # Hardcoded questions (MVP)
└── presentation/                       # (empty — VMs in composeApp for now)
```

### Module boundaries

- `:shared` — domain models, use cases, repository contracts + local data source
- `:composeApp` — theme, components, screens, ViewModels, navigation

---

<a id="phase-1a"></a>
## Phase 1A — Theme & Design Tokens (1 commit)

### Goal
Replace hardcoded gold background with a full theme system derived from Figma.

### Files to create

#### `ui/theme/AppColors.kt`
```kotlin
// Factory functions for light color scheme
// Maps Figma tokens → Material 3 ColorScheme
//
// Key mappings:
//   background = Color(0xFFFFF9ED)      // warm cream
//   onBackground = Color(0xFF2C2C2C)    // dark gray
//   surface = Color(0xFFFFFFFF)         // card white
//   onSurface = Color(0xFF2C2C2C)
//   primary = Color(0xFFFFC933)         // honey gold
//   onPrimary = Color(0xFF2C2C2C)      // dark on gold (!)
//   secondary = Color(0xFF8BA888)       // sage green
//   onSecondary = Color(0xFFFFFFFF)
//   tertiary = Color(0xFFA67C52)        // warm brown
//   onTertiary = Color(0xFFFFFFFF)
//   error = Color(0xFFD4183D)
//   onError = Color(0xFFFFFFFF)
//   surfaceVariant = Color(0xFFFFF9ED)  // muted
//   onSurfaceVariant = Color(0xFF6B6B6B)
//   outline = Color(0x1A2C2C2C)         // rgba(44,44,44,0.1)
//
// Extended colors (via data class + CompositionLocal):
//   correctAnswer = Color(0xFF4CAF50)
//   wrongAnswer = Color(0xFFD4183D)
//   selectedAnswerBorder = Color(0xFFFFC933)
//   selectedAnswerBackground = Color(0x0DFFC933) // 5% alpha
//   primaryContainer = Color(0x33FFC933)          // 20% alpha
```

#### `ui/theme/AppTypography.kt`
```kotlin
// Typography using Inter font family
// Load via compose resources: Font(Res.font.inter_regular), etc.
//
// displayLarge: 56sp, Bold, lineHeight 1.1  — score display
// headlineLarge: 32sp, Bold, lineHeight 1.2  — splash title
// headlineMedium: 24sp, Bold, lineHeight 1.3  — screen titles
// headlineSmall: 20sp, SemiBold, lineHeight 1.3  — card titles
// bodyLarge: 16sp, Medium, lineHeight 1.5  — question text
// bodyMedium: 14sp, Normal, lineHeight 1.5  — descriptions, buttons
// bodySmall: 12sp, Normal, lineHeight 1.5  — captions
// labelLarge: 14sp, SemiBold, lineHeight 1.5  — tab labels, button labels
```

#### `ui/theme/AppShapes.kt`
```kotlin
// Shapes override
// small = RoundedCornerShape(8.dp)    — badges, chips
// medium = RoundedCornerShape(12.dp)  — answer options, buttons, tabs
// large = RoundedCornerShape(16.dp)   — cards (rounded-2xl equivalent)
// extraLarge = RoundedCornerShape(24.dp) — large cards, sheets
```

#### `ui/theme/AppSpacing.kt`
```kotlin
// data class AppSpacing(
//   val xxs: Dp = 2.dp,
//   val xs: Dp = 4.dp,
//   val sm: Dp = 8.dp,
//   val md: Dp = 12.dp,
//   val lg: Dp = 16.dp,
//   val xl: Dp = 24.dp,
//   val xxl: Dp = 32.dp,
//   val xxxl: Dp = 48.dp,
// )
// Provided via LocalAppSpacing CompositionLocal
```

#### `ui/theme/AppTheme.kt`
```kotlin
// @Composable fun AppTheme(content: @Composable () -> Unit)
//   → wraps MaterialTheme(colorScheme, typography, shapes)
//   → provides LocalAppSpacing, LocalExtendedColors
//
// object AppTheme {
//   val spacing: AppSpacing @Composable get() = LocalAppSpacing.current
//   val extendedColors: ExtendedColors @Composable get() = LocalExtendedColors.current
// }
```

### Verification
- Replace `GoldScreen` background with `MaterialTheme.colorScheme.background`
- App should show warm cream background (#FFF9ED) on both platforms
- Verify title uses `MaterialTheme.typography.headlineMedium`

---

<a id="phase-1b"></a>
## Phase 1B — Reusable Components (1 commit)

### Goal
Create the shared component library extracted from Figma screens.

### Components

#### 1. `AppButton.kt`

```kotlin
// enum class AppButtonVariant { Primary, Secondary, Tertiary }
//
// @Composable fun AppButton(
//   text: String,
//   onClick: () -> Unit,
//   modifier: Modifier = Modifier,
//   variant: AppButtonVariant = AppButtonVariant.Primary,
//   enabled: Boolean = true,
//   leadingIcon: @Composable (() -> Unit)? = null,
// )
//
// Primary: bg=primary, text=onPrimary, rounded-xl (12dp)
// Secondary: bg=surface, text=onSurface, border=outline, rounded-xl
// Tertiary: bg=transparent, text=primary, no border
// Disabled: bg=surfaceVariant, text=onSurfaceVariant
// Height: 48-52dp (touch target), fillMaxWidth()
// Font: labelLarge (14sp, SemiBold)
```

Figma source: "Dalej" button (QuizScreen), "Zagraj ponownie" (ResultScreen), "Zobacz ranking" (ResultScreen)

#### 2. `AnswerOption.kt`

```kotlin
// enum class AnswerOptionState { Default, Selected, Correct, Wrong, Disabled }
//
// @Composable fun AnswerOption(
//   text: String,
//   state: AnswerOptionState,
//   onClick: () -> Unit,
//   modifier: Modifier = Modifier,
//   enabled: Boolean = true,
// )
//
// Default: bg=surface, border=outline (2dp), rounded-xl
// Selected: border=primary, bg=selectedAnswerBackground, radio filled primary
// Correct: border=correctAnswer, bg=correctAnswer/10%  (future: after confirm)
// Wrong: border=wrongAnswer, bg=wrongAnswer/10%  (future: after confirm)
// Disabled: opacity 0.5, not clickable
// Inner layout: Row(radioCircle + text), minHeight=48dp
```

Figma source: Answer options in QuizQuestionScreen

#### 3. `QuizTopBar.kt`

```kotlin
// @Composable fun QuizTopBar(
//   title: String,
//   onBackClick: (() -> Unit)? = null,
//   modifier: Modifier = Modifier,
// )
//
// Layout: Row(backButton + title)
// Back button: 40x40dp circle, hover=surfaceVariant
// Title: headlineMedium (24sp, Bold)
// Bottom border: 1dp outline
// Bg: surface (card color)
// Padding: horizontal=lg(16dp), vertical=lg(16dp)
```

Figma source: LeaderboardScreen header

#### 4. `QuizProgressBar.kt`

```kotlin
// @Composable fun QuizProgressBar(
//   currentQuestion: Int,        // 0-based index
//   totalQuestions: Int,
//   modifier: Modifier = Modifier,
// )
//
// Layout:
//   Row(label="Pytanie X / Y" , percentage="XX%")
//   LinearProgressIndicator(progress, height=8dp, trackColor=surfaceVariant, color=primary)
// Font: label=bodyMedium+SemiBold, percentage=bodyMedium
```

Figma source: QuizQuestionScreen progress section

#### 5. `ActionCard.kt`

```kotlin
// @Composable fun ActionCard(
//   title: String,
//   description: String,
//   icon: @Composable () -> Unit,
//   onClick: () -> Unit,
//   modifier: Modifier = Modifier,
//   iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
// )
//
// Layout: Card(rounded-2xl, elevation=shadow-sm, border=outline)
//   → Row(iconBox(48x48, rounded-xl, iconBgColor) + Column(title+desc))
// Padding: xl(24dp)
// Title: headlineSmall (20sp, SemiBold)
// Description: bodyMedium (14sp, Normal), color=onSurfaceVariant
```

Figma source: "Zagraj" and "Ranking / statystyki" cards on HomeScreen

#### 6. `ResultCard.kt`

```kotlin
// @Composable fun ResultCard(
//   score: Int,
//   totalQuestions: Int,
//   percentage: Int,
//   modifier: Modifier = Modifier,
// )
//
// Layout: Card(rounded-2xl, border=outline, shadow-sm)
//   → Column(center aligned):
//       score text: displayLarge (56sp, Bold, primary color)     "3/5"
//       percentage: headlineSmall (20sp, SemiBold)                "60%"
//       label: bodyMedium (14sp), onSurfaceVariant               "poprawnych odpowiedzi"
// Padding: xxl(32dp)
```

Figma source: Score display on ResultScreen

#### 7. `LeaderboardEntryRow.kt`

```kotlin
// @Composable fun LeaderboardEntryRow(
//   rank: Int,
//   name: String,
//   score: Int,
//   isCurrentUser: Boolean = false,
//   modifier: Modifier = Modifier,
// )
//
// Layout: Row inside Card(rounded-xl)
//   → rank circle (40x40, surfaceVariant bg, or icon for top 3)
//   → name (bodyMedium, SemiBold; primary color if current user)
//   → Column(score bodyLarge Bold + "punktów" bodySmall)
// Current user: border=primary (2dp), bg=primary/10%
// Rank icons: 1=Trophy(primary), 2=Medal(onSurfaceVariant), 3=Award(tertiary)
```

Figma source: Leaderboard entries on LeaderboardScreen

#### 8. `TabSelector.kt`

```kotlin
// @Composable fun TabSelector(
//   tabs: List<String>,
//   selectedIndex: Int,
//   onTabSelected: (Int) -> Unit,
//   modifier: Modifier = Modifier,
// )
//
// Layout: Row(equal weight buttons)
// Selected: bg=primary, text=onPrimary
// Unselected: bg=surfaceVariant, text=onSurfaceVariant
// Shape: medium (12dp rounded)
// Font: labelLarge (14sp, SemiBold)
// Gap: xs(4dp) or sm(8dp)
```

Figma source: "All-time" / "Weekly" tabs on LeaderboardScreen

#### 9. `UserPositionCard.kt`

```kotlin
// @Composable fun UserPositionCard(
//   rank: Int,
//   score: Int,
//   modifier: Modifier = Modifier,
// )
//
// Layout: Card(rounded-xl, bg=secondary/10%, border=secondary/30%)
//   → Row:
//       Column("Twoje miejsce", "#X" headlineSmall Bold)
//       Column("Twój wynik", "XX pkt" headlineSmall Bold, secondary color)
// Font: labels=bodySmall, values=headlineSmall
```

Figma source: "Your Position Highlight" on LeaderboardScreen

### Verification
- Each component renders in isolation (preview or test screen)
- Components use only theme tokens — no hardcoded hex values
- All touch targets ≥ 48dp

---

<a id="phase-1c"></a>
## Phase 1C — MVI Infrastructure & Domain Models (1 commit)

### Goal
Create the MVI base class, domain models, and state definitions for all screens.

### Domain models (`shared/src/commonMain/.../domain/model/`)

```kotlin
// Question.kt
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
)

// QuizResult.kt
data class QuizResult(
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
)

// LeaderboardEntry.kt
data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
    val isCurrentUser: Boolean = false,
)
```

### Use cases (`shared/src/commonMain/.../domain/usecase/`)

```kotlin
// GetRandomQuestionsUseCase.kt
// class GetRandomQuestionsUseCase(private val repository: QuestionRepository) {
//     operator fun invoke(count: Int = 5): List<Question>
// }

// CalculateScoreUseCase.kt
// class CalculateScoreUseCase {
//     operator fun invoke(
//         questions: List<Question>,
//         answers: List<Int>,
//     ): QuizResult
// }
```

### Data source (`shared/src/commonMain/.../data/source/`)

```kotlin
// LocalQuestionDataSource.kt
// Hardcoded list of quiz questions (migrated from UI_figma/quizData.ts)
// Implements QuestionRepository interface
```

### MVI Base (`composeApp/src/commonMain/.../presentation/base/`)

```kotlin
// MviViewModel.kt — see ADR-0003 for full pattern
// abstract class MviViewModel<S, I, E>(initialState: S)
```

### State models (`composeApp/src/commonMain/.../presentation/`)

#### HomeState + HomeIntent

```kotlin
data class HomeState(
    val appTitle: String = "Quiz Pszczelarski",
    val appDescription: String = "Sprawdź swoją wiedzę o pszczołach...",
)

sealed interface HomeIntent {
    data object StartQuiz : HomeIntent
    data object ViewLeaderboard : HomeIntent
}

sealed interface HomeEffect {
    data object NavigateToQuiz : HomeEffect
    data object NavigateToLeaderboard : HomeEffect
}
```

#### QuizState + QuizIntent + QuizEffect

```kotlin
data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val isLoading: Boolean = true,
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentQuestionIndex)
    val totalQuestions: Int get() = questions.size
    val progress: Float get() =
        if (totalQuestions == 0) 0f
        else (currentQuestionIndex + 1).toFloat() / totalQuestions
    val isLastQuestion: Boolean get() = currentQuestionIndex + 1 >= totalQuestions
    val canProceed: Boolean get() = selectedAnswerIndex != null
}

sealed interface QuizIntent {
    data class SelectAnswer(val index: Int) : QuizIntent
    data object NextQuestion : QuizIntent
}

sealed interface QuizEffect {
    data class NavigateToResult(val score: Int, val total: Int) : QuizEffect
}
```

#### ResultState + ResultIntent + ResultEffect

```kotlin
data class ResultState(
    val score: Int = 0,
    val totalQuestions: Int = 0,
) {
    val percentage: Int get() =
        if (totalQuestions == 0) 0
        else ((score.toFloat() / totalQuestions) * 100).roundToInt()

    val message: String get() = when {
        percentage == 100 -> "Doskonale!"
        percentage >= 80 -> "Świetna robota!"
        percentage >= 60 -> "Dobrze Ci poszło!"
        percentage >= 40 -> "Nieźle, ale możesz lepiej!"
        else -> "Spróbuj jeszcze raz!"
    }

    val isHighScore: Boolean get() = percentage >= 80
}

sealed interface ResultIntent {
    data object PlayAgain : ResultIntent
    data object ViewLeaderboard : ResultIntent
}

sealed interface ResultEffect {
    data object NavigateToQuiz : ResultEffect
    data object NavigateToLeaderboard : ResultEffect
}
```

### Verification
- Domain models compile in `:shared`
- MviViewModel compiles in `:composeApp`
- State models have computed properties that match UI requirements
- `CalculateScoreUseCase` unit test passes

---

<a id="phase-1d"></a>
## Phase 1D — Screens (1 commit)

### Goal
Implement all 5 screens as stateless composables. Each screen receives state
and emits intents via callbacks. No business logic in UI.

### Screen contracts

#### 1. `SplashScreen.kt`

```kotlin
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
)
// Content: centered Column
//   → HexagonIcon (placeholder — use Material icon or custom SVG)
//   → Title "Quiz Pszczelarski" headlineLarge
//   → Subtitle bodyMedium, onSurfaceVariant
// Auto-navigate after 2s delay (LaunchedEffect)
// Background: MaterialTheme.colorScheme.background
```

#### 2. `HomeScreen.kt`

```kotlin
@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
)
// Content: Column(fillMaxSize, px=lg, py=xxl)
//   → Header: icon + title (headlineMedium) + description (bodyMedium)
//   → ActionCard("Zagraj", ..., onClick = { onIntent(HomeIntent.StartQuiz) })
//   → ActionCard("Ranking / statystyki", ..., onClick = { onIntent(HomeIntent.ViewLeaderboard) })
//   → Footer: bodySmall "Każdy quiz składa się z 5 pytań"
```

#### 3. `QuizScreen.kt`

```kotlin
@Composable
fun QuizScreen(
    state: QuizState,
    onIntent: (QuizIntent) -> Unit,
    modifier: Modifier = Modifier,
)
// Content: Column(fillMaxSize)
//   → QuizProgressBar(state.currentQuestionIndex, state.totalQuestions)
//   → QuestionCard: Card(rounded-2xl) → bodyLarge text
//   → LazyColumn / Column of AnswerOption items
//   → AppButton("Dalej" or "Zakończ quiz", variant=Primary, enabled=state.canProceed)
// Each AnswerOption: state = Default if not selected, Selected if selectedAnswerIndex matches
```

#### 4. `ResultScreen.kt`

```kotlin
@Composable
fun ResultScreen(
    state: ResultState,
    onIntent: (ResultIntent) -> Unit,
    modifier: Modifier = Modifier,
)
// Content: Column(fillMaxSize, center)
//   → Trophy/Award icon (conditional on isHighScore)
//   → message (headlineMedium)
//   → "Zakończyłeś quiz" (bodyMedium)
//   → ResultCard(state.score, state.totalQuestions, state.percentage)
//   → AppButton("Zagraj ponownie", Primary, leadingIcon=RotateCcw)
//   → AppButton("Zobacz ranking", Secondary, leadingIcon=Trophy)
```

#### 5. `LeaderboardScreen.kt`

```kotlin
@Composable
fun LeaderboardScreen(
    state: LeaderboardState,
    onIntent: (LeaderboardIntent) -> Unit,
    modifier: Modifier = Modifier,
)
// Content: Column(fillMaxSize)
//   → QuizTopBar("Ranking", onBackClick = { onIntent(LeaderboardIntent.GoBack) })
//   → TabSelector(["All-time", "Weekly"], state.selectedTabIndex, ...)
//   → LazyColumn of LeaderboardEntryRow items
//   → UserPositionCard(state.userRank, state.userScore)
```

### Stateless screen rules (CRITICAL)
- No `remember`, no `mutableStateOf` for business data
- No direct ViewModel references — screens receive State + callback
- `LaunchedEffect` only for UI concerns (splash delay, scroll)
- All data comes from State parameter
- All user actions go through `onIntent` callback

### Verification
- Each screen renders with sample data (hardcoded preview state)
- Screens compile and display on both platforms
- No business logic in screen files

---

<a id="phase-1e"></a>
## Phase 1E — Navigation & App Shell (1 commit)

### Goal
Wire screens together with a simple navigation system.

### Route definitions

```kotlin
sealed interface Route {
    data object Splash : Route
    data object Home : Route
    data object Quiz : Route
    data class Result(val score: Int, val total: Int) : Route
    data object Leaderboard : Route
}
```

### Navigation approach (simple state-based)

For MVP, use a simple composable navigator with `AnimatedContent`:

```kotlin
// AppNavigation.kt
@Composable
fun AppNavigation() {
    var currentRoute by remember { mutableStateOf<Route>(Route.Splash) }

    AnimatedContent(currentRoute) { route ->
        when (route) {
            Route.Splash -> SplashScreen(onSplashFinished = { currentRoute = Route.Home })
            Route.Home -> {
                val vm = HomeViewModel()
                val state by vm.state.collectAsState()
                LaunchedEffect(Unit) { vm.effect.collect { effect -> /* navigate */ } }
                HomeScreen(state = state, onIntent = vm::onIntent)
            }
            // ... etc
        }
    }
}
```

### App.kt update

```kotlin
@Composable
fun App() {
    AppTheme {
        AppNavigation()
    }
}
```

### Verification
- Splash → Home → Quiz → Result → Home flow works
- Home → Leaderboard → Back works
- Result → Play Again loops through quiz
- Back navigation works on Android (system back)

---

<a id="phase-1f"></a>
## Phase 1F — Integration & Polish (1 commit)

### Goal
Final cleanup, remove Phase 0 placeholder, verify both platforms.

### Tasks
1. Remove `GoldScreen.kt`, `GoldScreenUiState.kt`, `GoldScreenViewModel.kt`
2. Remove `ScreenContent.kt` (replaced by `Question`, `QuizResult` etc.)
3. Verify `./gradlew build` succeeds
4. Verify Android emulator: full flow works
5. Verify iOS simulator: full flow works
6. Update `README.md` with new screen list and architecture summary

### Definition of Done

| # | Criterion | Verification |
|---|---|---|
| 1 | Theme applied on both platforms | Visual: cream background, gold accents |
| 2 | All 5 screens render | Navigate through full flow |
| 3 | Components use theme tokens only | Code review: no hardcoded hex in screens/components |
| 4 | Screens are stateless | Code review: no business logic in UI files |
| 5 | MVI pattern followed | Code review: State + Intent + Effect per screen |
| 6 | Quiz flow works end-to-end | Select answers → see score → play again |
| 7 | Leaderboard displays data | Navigate to leaderboard, see entries |
| 8 | Both platforms build clean | `./gradlew build` + Xcode build succeed |
| 9 | Phase 0 code removed | No GoldScreen references remain |
| 10 | No crashes | 30s smoke test on each platform |

---

<a id="dor-gaps"></a>
## DoR Gaps & Missing Info

### Identified gaps

| # | Gap | Impact | Resolution |
|---|---|---|---|
| 1 | **ModeSelectScreen** requested but not in Figma | Cannot implement without design | **Deferred** — not present in UI_figma. Will implement when Figma design is provided. |
| 2 | **Correct/Wrong answer reveal** flow not fully specified in Figma | QuizScreen only shows selected state, not post-confirmation reveal | **MVP:** Select + proceed immediately. **Future:** ADR for answer confirmation flow with reveal animation. |
| 3 | **Hexagon icon** (splash/home) is a Lucide React icon, not a standard Material icon | Need equivalent in Compose | Use `Icons.Outlined.Hexagon` or a simple custom `Path`-based icon. |
| 4 | **Inter font** loading via Compose Resources | Need font files in resources | Add Inter .ttf files to `composeApp/src/commonMain/composeResources/font/`. |
| 5 | **Leaderboard data** is currently hardcoded | No backend/Firebase yet | Acceptable for MVP. Data source behind repository interface, ready for Firebase. |
| 6 | **Animations** (motion/react equivalents) | Figma uses framer-motion | Use `AnimatedContent`, `animateContentSize`, `AnimatedVisibility` in Compose. Keep subtle. |

---

<a id="risks"></a>
## Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Compose MP rendering inconsistency iOS vs Android | Low | Medium | Test on both platforms each phase. Accept minor visual differences. |
| Inter font not rendering on iOS | Low | Medium | Fall back to system font (San Francisco); verify early in Phase 1A. |
| Navigation complexity grows | Medium | Low | Start with simple state-based nav. Migrate to library (Voyager/Decompose) only if needed (ADR). |
| MVI boilerplate slows delivery | Low | Low | Keep reducer simple. Don't over-engineer base class. |
| AnswerOption state machine edge cases | Medium | Medium | Test reducer with all state transitions. Add `Correct`/`Wrong` states in Phase 2. |

---

## Commit Sequence Summary

| Phase | Commit Message | Files Changed |
|---|---|---|
| 1A | `feat: add design system theme (colors, typography, shapes, spacing)` | 5 new theme files + App.kt update |
| 1B | `feat: add reusable UI components` | 9 new component files |
| 1C | `feat: add MVI base, domain models, state definitions` | ~15 files (domain + presentation) |
| 1D | `feat: add all screens (stateless composables)` | 5 screen files |
| 1E | `feat: add navigation and wire app flow` | Route.kt, AppNavigation.kt, App.kt |
| 1F | `chore: remove phase 0 placeholder, polish` | Delete 3 files, update README |
