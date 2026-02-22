package pl.quizpszczelarski.shared.data.settings

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import pl.quizpszczelarski.shared.domain.model.SettingsState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsRepositoryImplTest {

    private fun createRepo(settings: MapSettings = MapSettings()): SettingsRepositoryImpl {
        return SettingsRepositoryImpl(settings)
    }

    @Test
    fun `default settings have haptics and sound enabled`() {
        val repo = createRepo()
        val state = repo.getSettings()
        assertEquals(SettingsState(hapticsEnabled = true, soundEnabled = true), state)
    }

    @Test
    fun `setHapticsEnabled persists and emits new state`() = runTest {
        val repo = createRepo()

        repo.setHapticsEnabled(false)

        assertFalse(repo.getSettings().hapticsEnabled)
        assertFalse(repo.getSettingsFlow().first().hapticsEnabled)
    }

    @Test
    fun `setSoundEnabled persists and emits new state`() = runTest {
        val repo = createRepo()

        repo.setSoundEnabled(false)

        assertFalse(repo.getSettings().soundEnabled)
        assertFalse(repo.getSettingsFlow().first().soundEnabled)
    }

    @Test
    fun `settings survive re-creation with same Settings backend`() = runTest {
        val mapSettings = MapSettings()
        val repo1 = createRepo(mapSettings)

        repo1.setHapticsEnabled(false)
        repo1.setSoundEnabled(false)

        // Recreate with same backing store
        val repo2 = createRepo(mapSettings)
        val state = repo2.getSettings()

        assertFalse(state.hapticsEnabled)
        assertFalse(state.soundEnabled)
    }

    @Test
    fun `toggling haptics does not affect sound setting`() = runTest {
        val repo = createRepo()

        repo.setHapticsEnabled(false)

        assertFalse(repo.getSettings().hapticsEnabled)
        assertTrue(repo.getSettings().soundEnabled)
    }

    @Test
    fun `toggling sound does not affect haptics setting`() = runTest {
        val repo = createRepo()

        repo.setSoundEnabled(false)

        assertTrue(repo.getSettings().hapticsEnabled)
        assertFalse(repo.getSettings().soundEnabled)
    }
}
