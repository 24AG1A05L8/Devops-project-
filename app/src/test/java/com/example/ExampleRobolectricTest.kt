package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.CostSimulationContract
import com.example.domain.model.MetricsSummaryContract
import com.example.domain.model.Permission
import com.example.domain.model.RbacRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DevOps Core", appName)
    }

    @Test
    fun `verify RBAC permissions enforcement and blueprint API contract defaults`() {
        assertTrue(RbacRole.PLATFORM_ADMIN.can(Permission.TEARDOWN_INFRA))
        assertFalse(RbacRole.READ_ONLY_AUDITOR.can(Permission.TRIGGER_PIPELINE))

        val summary = MetricsSummaryContract()
        assertEquals(395.00, summary.monthly_cost, 0.01)
        assertEquals(0, summary.critical_vulnerabilities)
        assertEquals(14, summary.deployment_frequency_per_day)

        val sim = CostSimulationContract()
        assertEquals(420.00, sim.proposed_infra_cost, 0.01)
        assertEquals(25.00, sim.cost_difference, 0.01)
        assertFalse(sim.budget_violation)
    }
}
