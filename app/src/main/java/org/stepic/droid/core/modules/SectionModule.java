package org.stepic.droid.core.modules;


import android.content.Context;

import com.squareup.otto.Bus;

import org.stepic.droid.analytic.Analytic;
import org.stepic.droid.concurrency.IMainHandler;
import org.stepic.droid.configuration.IConfig;
import org.stepic.droid.core.PerFragment;
import org.stepic.droid.core.presenters.CalendarPresenter;
import org.stepic.droid.core.presenters.SectionsPresenter;
import org.stepic.droid.preferences.SharedPreferenceHelper;
import org.stepic.droid.preferences.UserPreferences;
import org.stepic.droid.store.operations.DatabaseFacade;
import org.stepic.droid.core.presenters.CourseFinderPresenter;
import org.stepic.droid.core.presenters.CourseJoinerPresenter;
import org.stepic.droid.web.IApi;

import java.util.concurrent.ThreadPoolExecutor;

import dagger.Module;
import dagger.Provides;

@Module
public class SectionModule {

    @PerFragment
    @Provides
    CourseJoinerPresenter provideCourseJoiner(
            SharedPreferenceHelper sharedPreferenceHelper,
            IApi api,
            ThreadPoolExecutor threadPoolExecutor,
            Bus bus,
            DatabaseFacade databaseFacade) {
        return new CourseJoinerPresenter(sharedPreferenceHelper, api, threadPoolExecutor, bus, databaseFacade);
    }

    @PerFragment
    @Provides
    CourseFinderPresenter provideCourseFinderPresenter(
            ThreadPoolExecutor threadPoolExecutor,
            DatabaseFacade databaseFacade,
            IApi api,
            IMainHandler mainHandler) {
        return new CourseFinderPresenter(threadPoolExecutor, databaseFacade, api, mainHandler);
    }

    @PerFragment
    @Provides
    CalendarPresenter provideCalendarPresenter(
            IConfig config,
            IMainHandler mainHandler,
            Context context,
            ThreadPoolExecutor threadPoolExecutor,
            DatabaseFacade databaseFacade,
            UserPreferences userPreferences,
            Analytic analytic) {
        return new CalendarPresenter(config, mainHandler, context, threadPoolExecutor, databaseFacade, userPreferences, analytic);
    }

    @PerFragment
    @Provides
    SectionsPresenter provideSectionsPresenter(ThreadPoolExecutor threadPoolExecutor,
                                               IMainHandler mainHandler,
                                               IApi api,
                                               DatabaseFacade databaseFacade) {
        return new SectionsPresenter(
                threadPoolExecutor,
                mainHandler,
                api,
                databaseFacade);
    }
}
