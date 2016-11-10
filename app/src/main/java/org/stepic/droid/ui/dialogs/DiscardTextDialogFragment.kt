package org.stepic.droid.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import org.stepic.droid.R


class DiscardTextDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(): DialogFragment {
            return DiscardTextDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.title_confirmation)
                .setMessage(R.string.are_you_sure_remove_comment_text)
                .setPositiveButton(R.string.delete_label) { dialog, which ->
                    targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                }
                .setNegativeButton(R.string.cancel, null)

        return builder.create()
    }
}