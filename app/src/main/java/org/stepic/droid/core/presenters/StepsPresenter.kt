package org.stepic.droid.core.presenters

import org.stepic.droid.concurrency.IMainHandler
import org.stepic.droid.core.presenters.contracts.StepsView
import org.stepic.droid.model.Lesson
import org.stepic.droid.model.Section
import org.stepic.droid.model.Step
import org.stepic.droid.model.Unit
import org.stepic.droid.preferences.SharedPreferenceHelper
import org.stepic.droid.store.operations.DatabaseFacade
import org.stepic.droid.util.ProgressUtil
import org.stepic.droid.web.IApi
import org.stepic.droid.web.LessonStepicResponse
import org.stepic.droid.web.StepResponse
import retrofit.Response
import java.util.*
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean

class StepsPresenter(val threadPoolExecutor: ThreadPoolExecutor,
                     val mainHandler: IMainHandler,
                     val databaseFacade: DatabaseFacade,
                     val api: IApi,
                     val sharedPreferenceHelper: SharedPreferenceHelper) : PresenterBase<StepsView>() {

    var lesson: Lesson? = null
        private set

    var isLoading = AtomicBoolean(false)

    var unit: Unit? = null

    var section: Section? = null

    val stepList = ArrayList<Step>()


    @JvmOverloads
    fun init(outLesson: Lesson? = null,
             outUnit: Unit? = null,
             simpleLessonId: Long = -1,
             simpleUnitId: Long = -1,
             defaultStepPositionStartWithOne: Long = -1,
             fromPreviousLesson: Boolean = false,
             section: Section? = null) {

        if (isLoading.get()) {
            return
        }

        if (lesson != null) {
            //already loaded if THIS.Lesson != null -> show
            view?.onLessonUnitPrepared(lesson, unit, this.section)
            view?.showSteps(fromPreviousLesson, defaultStepPositionStartWithOne)
            return
        }

        isLoading.set(true)
        view?.onLoading()
        lesson = outLesson
        unit = outUnit
        threadPoolExecutor.execute {
            try {
                val profileResponse = sharedPreferenceHelper.authResponseFromStore
                if (profileResponse == null) {
                    mainHandler.post {
                        view?.onUserNotAuth()
                    }
                    return@execute
                }

                if (lesson == null) {
                    initUnitLessonWithIds(simpleLessonId, simpleUnitId)
                }

                //after that Lesson should be not null
                if (lesson == null) {
                    return@execute
                }

                val sectionId = unit?.section ?: -1L

                if (section == null && sectionId >= 0) {
                    this.section = databaseFacade.getSectionById(sectionId)
                    if (this.section == null) {
                        try {
                            this.section = api.getSections(longArrayOf(sectionId)).execute().body().sections.firstOrNull()
                            // do not add to cache section in this way, because we need to support loading/caching state :<
                        } catch (ignored: Exception) {
                            // ok, section is optional
                        }
                    }
                } else {
                    this.section = section
                }

                mainHandler.post {
                    view?.onLessonUnitPrepared(lesson, unit, this.section)
                }

                loadSteps(defaultStepPositionStartWithOne, fromPreviousLesson)

            } finally {
                isLoading.set(false)
            }
        }
    }

    private fun loadSteps(defaultStepPositionStartWithOne: Long, fromPreviousLesson: Boolean) {
        lesson?.let {
            val stepList: MutableList<Step> = databaseFacade.getStepsOfLesson(it.id).filterNotNull().toMutableList()
            stepList.sortWith(Comparator { lhs, rhs ->
                if (lhs == null || rhs == null) {
                    0.toInt()
                } else {
                    val lhsPos = lhs.position
                    val rhsPos = rhs.position
                    (lhsPos - rhsPos).toInt()
                }
            })

            var isStepsShown = false
            if (stepList.isNotEmpty() && it.steps?.size ?: -1 == stepList.size) {
                stepList.forEach {
                    it.is_custom_passed = databaseFacade.isStepPassed(it)
                }
                isStepsShown = true
                //if we get steps from database -> progresses and assignments were stored
                mainHandler.post {
                    this.stepList.clear()
                    this.stepList.addAll(stepList)
                    view?.showSteps(fromPreviousLesson, defaultStepPositionStartWithOne)
                }
            }

            // and try to update from internet
            var response: Response<StepResponse>? = null
            try {
                response = api.getSteps(it.steps).execute()
            } catch (ex: Exception) {
                if (!isStepsShown) {
                    mainHandler.post {
                        view?.onConnectionProblem()
                    }
                    return
                }
            }
            if (response == null) {
                if (!isStepsShown) {
                    mainHandler.post {
                        view?.onConnectionProblem()
                    }
                }
                return
            } else {
                val stepListFromInternet = response.body().steps
                if (stepListFromInternet.isEmpty()) {
                    if (!isStepsShown) {
                        mainHandler.post {
                            view?.onEmptySteps()
                        }
                    }
                    return
                } else {
                    updateAssignmentsAndProgresses(stepListFromInternet, unit)
                    //only after getting progresses and assignments we can get steps
                    if (!isStepsShown) {
                        mainHandler.post {
                            this.stepList.clear()
                            this.stepList.addAll(stepListFromInternet)
                            view?.showSteps(fromPreviousLesson, defaultStepPositionStartWithOne)
                        }
                    }
                }
            }
        }
    }

    fun refreshWhenOnConnectionProblem(outLesson: Lesson?, outUnit: Unit?, simpleLessonId: Long, simpleUnitId: Long, defaultStepPositionStartWithOne: Long = -1, fromPreviousLesson: Boolean = false, section: Section?) {
        if (isLoading.get()) {
            return
        }

        if (lesson == null) {
            init(outLesson, outUnit, simpleLessonId, simpleUnitId, defaultStepPositionStartWithOne, fromPreviousLesson, section)

        } else {
            isLoading.set(true)
            view?.onLoading()
            threadPoolExecutor.execute {
                try {
                    loadSteps(defaultStepPositionStartWithOne, fromPreviousLesson)
                } finally {
                    isLoading.set(false)
                }
            }
        }

    }

    private fun updateAssignmentsAndProgresses(stepListFromInternet: List<Step>, unit: Unit?) {
        try {
            val progressIds: Array<out String?>
            if (unit != null) {
                val assignments = api.getAssignments(unit.assignments).execute().body().assignments
                assignments.filterNotNull().forEach {
                    databaseFacade.addAssignment(assignment = it)
                }
                progressIds = ProgressUtil.getAllProgresses(assignments)
            } else {
                progressIds = ProgressUtil.getAllProgresses(stepListFromInternet)
            }


            val progresses = api.getProgresses(progressIds).execute().body().progresses
            progresses.filterNotNull().forEach {
                databaseFacade.addProgress(progress = it)
            }

            //FIXME: Warning, it is mutable objects, which we show on StepsFragment and change here or not show, if we shown from database
            stepListFromInternet.forEach {
                it.is_custom_passed = databaseFacade.isStepPassed(it)
                databaseFacade.addStep(it) // update step in db
            }
        } catch (exception: Exception) {
            //we already show steps, and we don't need onConnectionError
            //just return
            return
        }


    }

    private fun initUnitLessonWithIds(simpleLessonId: Long, simpleUnitId: Long) {
        if (simpleLessonId < 0) {
            mainHandler.post {
                view?.onLessonCorrupted()
            }
            return
        }

        lesson = databaseFacade.getLessonById(simpleLessonId)
        if (lesson == null) {
            //not in database yet
            val response: Response<LessonStepicResponse?>?
            try {
                response = api.getLessons(longArrayOf(simpleLessonId)).execute()
            } catch (ex: Exception) {
                mainHandler.post {
                    view?.onConnectionProblem()
                }
                return
            }

            try {
                lesson = response?.body()?.lessons?.firstOrNull()
                if (lesson == null) {
                    mainHandler.post {
                        view?.onLessonCorrupted()
                    }
                    return
                } else {
                    lesson?.let {
                        databaseFacade.addLesson(it)
                    }
                }
            } catch (ex: Exception) {
                mainHandler.post {
                    view?.onLessonCorrupted()
                }
                return
            }
        }

        //now lesson is parsed. Try to parse optional unit

        if (simpleUnitId >= 0) {
            unit = databaseFacade.getUnitById(simpleUnitId)
        }

        if (unit == null) {
            if (simpleUnitId >= 0) {
                //get by unitId
                try {
                    unit = api.getUnits(longArrayOf(simpleUnitId)).execute()?.body()?.units?.firstOrNull()
                } catch (ignored: Exception) {
                    // unit can be null for lesson, which is not in Course
                }
                if (!(unit?.lesson?.equals(simpleLessonId) ?: false)) {
                    //if lesson is not equal unit.lesson or something null
                    loadUnitByLessonId(simpleLessonId)
                }
            } else {
                loadUnitByLessonId(simpleLessonId)
            }

            if (!(unit?.lesson?.equals(simpleLessonId) ?: false)) {
                unit = null
            }
        }
    }

    private fun loadUnitByLessonId(simpleLessonId: Long) {
        try {
            unit = api.getUnitByLessonId(simpleLessonId).execute()?.body()?.units?.firstOrNull()
        } catch (ignored: Exception) {
            // unit can be null for lesson, which is not in Course
        }
    }

}
