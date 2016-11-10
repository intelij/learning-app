package org.stepic.droid.ui.dialogs

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.squareup.otto.Bus
import org.stepic.droid.R
import org.stepic.droid.analytic.Analytic
import org.stepic.droid.base.MainApplication
import org.stepic.droid.concurrency.IMainHandler
import org.stepic.droid.events.loading.FinishLoadEvent
import org.stepic.droid.events.loading.StartLoadEvent
import org.stepic.droid.events.steps.ClearAllDownloadWithoutAnimationEvent
import org.stepic.droid.preferences.UserPreferences
import org.stepic.droid.store.CleanManager
import org.stepic.droid.store.operations.DatabaseFacade
import org.stepic.droid.util.DbParseHelper
import org.stepic.droid.util.FileUtil
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Inject

class ClearVideosDialog : DialogFragment() {

    companion object {
        val KEY_STRING_IDS = "step_ids"

        fun newInstance(): DialogFragment {
            return ClearVideosDialog()
        }
    }

    @Inject
    lateinit var databaseFacade: DatabaseFacade
    @Inject
    lateinit var cleanManager: CleanManager
    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var threadPoolExecutor: ThreadPoolExecutor
    @Inject
    lateinit var mainHandler: IMainHandler

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var analytic: Analytic

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainApplication.component().inject(this)
        val bundle = arguments
        val stringIds = bundle?.getString(KEY_STRING_IDS)


        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.title_confirmation).setMessage(R.string.clear_videos).setPositiveButton(R.string.yes) { dialog, which ->
            analytic.reportEvent(Analytic.Interaction.YES_CLEAR_VIDEOS)

            val task = object : AsyncTask<Void, Void, Void>() {
                override fun onPreExecute() {
                    super.onPreExecute()
                    bus.post(StartLoadEvent())
                }

                override fun doInBackground(params: Array<Void>): Void? {
                    val stepIds: LongArray?
                    if (stringIds != null) {

                        stepIds = DbParseHelper.parseStringToLongArray(stringIds)
                        if (stepIds == null) return null
                        for (stepId in stepIds) {
                            val step = databaseFacade.getStepById(stepId)
                            cleanManager.removeStep(step)
                        }
                    } else {
                        stepIds = null
                        FileUtil.cleanDirectory(userPreferences.userDownloadFolder);
                        FileUtil.cleanDirectory(userPreferences.sdCardDownloadFolder)
                        databaseFacade.dropDatabase();
                    }

                    mainHandler.post {
                        bus.post(ClearAllDownloadWithoutAnimationEvent(stepIds))
                    }
                    return null
                }

                override fun onPostExecute(o: Void?) {
                    bus.post(FinishLoadEvent())
                }
            }
            task.executeOnExecutor(threadPoolExecutor)
        }.setNegativeButton(R.string.no, null)

        return builder.create()
    }

}
