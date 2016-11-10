package org.stepic.droid.core.presenters

import org.stepic.droid.analytic.Analytic
import org.stepic.droid.concurrency.IMainHandler
import org.stepic.droid.core.presenters.contracts.StepQualityView
import org.stepic.droid.model.Video
import org.stepic.droid.preferences.UserPreferences
import org.stepic.droid.store.operations.DatabaseFacade
import java.util.concurrent.ThreadPoolExecutor

class StepQualityPresenter(val threadPoolExecutor: ThreadPoolExecutor,
                           val mainHandler: IMainHandler,
                           val databaseFacade: DatabaseFacade,
                           val userPreferences: UserPreferences,
                           val analytic: Analytic) : PresenterBase<StepQualityView>() {

    fun determineQuality(stepVideo: Video?) {
        if (stepVideo == null) {
            analytic.reportEvent(Analytic.Video.QUALITY_NOT_DETERMINATED)
        } else {
            threadPoolExecutor.execute {
                val video = databaseFacade.getCachedVideoById(stepVideo.id)
                val quality: String
                if (video == null) {
                    val resultQuality: String
                    try {

                        val weWant = Integer.parseInt(userPreferences.qualityVideo)
                        val urls = stepVideo.urls
                        var bestDelta = Integer.MAX_VALUE
                        var bestIndex = 0
                        for (i in urls.indices) {
                            val current = Integer.parseInt(urls[i].quality)
                            val delta = Math.abs(current - weWant)
                            if (delta < bestDelta) {
                                bestDelta = delta
                                bestIndex = i
                            }

                        }
                        resultQuality = urls[bestIndex].quality
                    } catch (e: NumberFormatException) {
                        resultQuality = userPreferences.qualityVideo
                    }


                    quality = resultQuality
                } else {
                    quality = video.quality
                }
                if (quality.isNullOrBlank()) {
                    analytic.reportEvent(Analytic.Video.QUALITY_NOT_DETERMINATED)
                } else {
                    val qualityForView = quality + "p"
                    mainHandler.post {
                        view?.showQuality(qualityForView)
                    }
                }
            }
        }

    }

}
