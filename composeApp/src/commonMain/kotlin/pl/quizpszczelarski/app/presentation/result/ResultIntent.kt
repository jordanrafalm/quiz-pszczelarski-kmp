package pl.quizpszczelarski.app.presentation.result

/**
 * User actions on the Result screen.
 */
sealed interface ResultIntent {
    data object PlayAgain : ResultIntent
    data object ViewLeaderboard : ResultIntent
    data object ShowNicknameDialog : ResultIntent
    data class UpdateNicknameInput(val text: String) : ResultIntent
    data object ConfirmNickname : ResultIntent
    data object DismissNicknameDialog : ResultIntent
}
