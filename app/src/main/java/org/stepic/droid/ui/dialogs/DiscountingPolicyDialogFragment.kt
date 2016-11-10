package org.stepic.droid.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.CheckBox
import org.stepic.droid.R
import org.stepic.droid.analytic.Analytic
import org.stepic.droid.base.MainApplication
import org.stepic.droid.preferences.UserPreferences
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Inject

class DiscountingPolicyDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(): DialogFragment {
            val dialog = DiscountingPolicyDialogFragment()
            return dialog
        }
    }


    @Inject
    lateinit var analytic: Analytic

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var threadPoolExecutor: ThreadPoolExecutor


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainApplication.component().inject(this)
        val layoutInflater = LayoutInflater.from(context)
        val explanationView = layoutInflater.inflate(R.layout.not_ask_again_view, null)
        val checkbox = explanationView.findViewById(R.id.do_not_ask_checkbox) as CheckBox

        val builder = AlertDialog.Builder(activity)
        builder
                .setTitle(R.string.title_confirmation)
                .setView(explanationView)
                .setMessage(R.string.discounting_policy_message)
                .setNegativeButton(R.string.no) {
                    dialog, which ->
                    analytic.reportEvent(Analytic.Interaction.NO_DISCOUNTING_DIALOG)
                }
                .setPositiveButton(R.string.yes, { dialog, which ->
                    analytic.reportEvent(Analytic.Interaction.YES_DISCOUNTING_DIALOG)
                    targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    val isNeedExplanation = !checkbox.isChecked
                    threadPoolExecutor.execute {
                        userPreferences.isShowDiscountingPolicyWarning = isNeedExplanation
                    }
                })


        return builder.create()
    }

}