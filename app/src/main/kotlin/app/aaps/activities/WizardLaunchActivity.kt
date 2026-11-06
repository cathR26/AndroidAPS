package app.aaps.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.aaps.ComposeMainActivity

/**
 * Trampoline that lets whitelisted external apps (10BE CarbCam) prefill the
 * Compose Bolus Wizard without being able to open any other screen.
 *
 * Flow:
 *   CarbCam -> this activity -> ComposeMainActivity with open_wizard_carbs/notes extras
 *   -> ComposeMainActivity navigates to AppRoute.WizardDialog.createRoute(carbs, notes)
 *   -> WizardDialogViewModel.initialize() reads carbs/notes from SavedStateHandle
 *
 * The user confirms in the native wizard; this activity never queues insulin.
 */
class WizardLaunchActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CARBS = "carbs"
        const val EXTRA_NOTES = "notes"

        private val ALLOWED_CALLERS = setOf(
            "de.be10.carbcam",
            "de.be10.carbcam.debug",
            "de.be10.carbcam-debug",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val caller = callingPackage ?: referrer?.host
        if (caller !in ALLOWED_CALLERS) {
            finish()
            return
        }

        val carbs = intent.getIntExtra(EXTRA_CARBS, 0)
        val notes = intent.getStringExtra(EXTRA_NOTES)?.take(200) ?: ""

        if (carbs <= 0 || carbs > 80) {
            finish()
            return
        }

        startActivity(Intent(this, ComposeMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("open_wizard_carbs", carbs)
            putExtra("open_wizard_notes", notes)
        })
        finish()
    }
}
