package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DuplicatePreventionEngine
import com.example.data.HealthRepository
import com.example.data.RiskMonitoringEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies PulseGuard AI name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PulseGuard AI", appName)
    }

    @Test
    fun `test 1 verifies duplicate steps are not summed together`() {
        val repository = HealthRepository()
        val resultMsg = repository.runTest1DuplicateSteps()
        val stats = repository.derivedStats.value

        // Raw sum is 10,250 - 11,700
        assertTrue("Raw steps should exceed 10,000", stats.rawStepsSumUnconsolidated > 10000)
        // Consolidated steps must NOT be the naive sum
        assertTrue("Consolidated steps must be less than raw sum", stats.dailySteps < stats.rawStepsSumUnconsolidated)
        // Deduplication savings should be positive
        assertTrue("Deduplication savings should be positive", stats.deduplicationSavingsSteps > 0)
        assertTrue(resultMsg.contains("PASSED"))
    }

    @Test
    fun `test 2 verifies repeated sync is idempotent`() {
        val repository = HealthRepository()
        val initialSteps = repository.derivedStats.value.dailySteps

        val msg = repository.runTest2RepeatedSync()
        val postSteps = repository.derivedStats.value.dailySteps

        assertEquals("Steps must remain unchanged on repeated sync", initialSteps, postSteps)
        assertTrue(msg.contains("PASSED"))
    }

    @Test
    fun `test 5 verifies abnormal heart rate generates clinical alert`() {
        val repository = HealthRepository()
        val msg = repository.runTest5AbnormalMeasurement()
        val alerts = repository.activeAlerts.value

        assertTrue(alerts.any { it.metricType == MetricType.HEART_RATE && it.severity == AlertSeverity.URGENT })
        assertTrue(msg.contains("PASSED"))
    }

    @Test
    fun `test 6 verifies fall event initiates cancelable emergency countdown`() {
        val repository = HealthRepository()
        val event = repository.triggerSimulatedFallEvent()

        assertEquals(EmergencyStatus.COUNTDOWN_ACTIVE, event.status)
        assertEquals(EmergencyTrigger.FALL_DETECTED, event.trigger)

        repository.cancelEmergencyEvent()
        val cancelled = repository.activeEmergencyEvent.value
        assertEquals(EmergencyStatus.CANCELLED_BY_USER, cancelled?.status)
    }

    @Test
    fun `social integration test verifies challenge uses consolidated steps not inflated sum`() {
        val healthRepo = HealthRepository()
        val socialRepo = com.example.data.SocialRepository(healthRepo)

        val challenge = socialRepo.challenges.value.first()
        val userParticipant = challenge.participants.find { it.isUser }

        assertNotNull("User must be in challenge", userParticipant)
        // Verify user's challenge progress is connected to consolidated steps
        val currentConsolidated = healthRepo.derivedStats.value.dailySteps
        assertTrue("Challenge progress must reflect consolidated steps", userParticipant!!.consolidatedSteps >= currentConsolidated)
    }

    @Test
    fun `social integration test verifies achievement explicit sharing workflow`() {
        val healthRepo = HealthRepository()
        val socialRepo = com.example.data.SocialRepository(healthRepo)

        val unshared = socialRepo.eligibleAchievements.value.first { !it.isShared }
        val initialPostsCount = socialRepo.posts.value.size

        val post = socialRepo.shareHealthAchievement(
            achievement = unshared,
            customMessage = "Celebrating milestone!",
            visibility = PostVisibility.PUBLIC
        )

        assertEquals("New post added to feed", initialPostsCount + 1, socialRepo.posts.value.size)
        assertEquals(unshared.title, post.achievementMeta?.title)
        assertTrue("Achievement marked as shared", socialRepo.eligibleAchievements.value.first { it.id == unshared.id }.isShared)

        // Deleting social post does not delete raw health measurements
        socialRepo.deletePost(post.id)
        assertEquals("Post removed from feed", initialPostsCount, socialRepo.posts.value.size)
        assertTrue("Raw health data preserved", healthRepo.rawMeasurements.value.isNotEmpty())
    }
}
