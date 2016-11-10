package org.stepic.droid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.otto.Bus;

import org.stepic.droid.analytic.Analytic;
import org.stepic.droid.base.MainApplication;
import org.stepic.droid.concurrency.IMainHandler;
import org.stepic.droid.core.LocalProgressManager;
import org.stepic.droid.events.InternetIsEnabledEvent;
import org.stepic.droid.events.steps.UpdateStepEvent;
import org.stepic.droid.model.Step;
import org.stepic.droid.store.IStoreStateManager;
import org.stepic.droid.store.operations.DatabaseFacade;
import org.stepic.droid.util.resolvers.StepHelper;
import org.stepic.droid.web.IApi;
import org.stepic.droid.web.ViewAssignment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class InternetConnectionEnabledReceiver extends BroadcastReceiver {


    @Inject
    IApi api;
    @Inject
    DatabaseFacade databaseFacade;
    @Inject
    IStoreStateManager storeStateManager;

    @Inject
    Bus bus;

    @Inject
    Analytic analytic;

    @Inject
    ThreadPoolExecutor threadPoolExecutor;

    @Inject
    LocalProgressManager unitProgressManager;

    @Inject
    IMainHandler mainHandler;

    private AtomicBoolean inWork = new AtomicBoolean(false);


    public InternetConnectionEnabledReceiver() {
        MainApplication.component().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isOnline(MainApplication.getAppContext()) || inWork.get()) return;
        inWork.set(true);
        mainHandler.post(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                bus.post(new InternetIsEnabledEvent());
                return Unit.INSTANCE;
            }
        });


        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<ViewAssignment> list = databaseFacade.getAllInQueue();
                for (ViewAssignment item : list) {
                    try {
                        retrofit.Response<Void> response = api.postViewed(item).execute();
                        if (response.isSuccess()) {
                            databaseFacade.removeFromQueue(item);
                            Step step = databaseFacade.getStepById(item.getStep());
                            if (step != null) {
                                final long stepId = step.getId();
                                if (StepHelper.isViewedStatePost(step)) {
                                    if (item.getAssignment() != null) {
                                        databaseFacade.markProgressAsPassed(item.getAssignment());
                                    } else {
                                        if (step.getProgressId() != null) {
                                            databaseFacade.markProgressAsPassedIfInDb(step.getProgressId());
                                        }
                                    }
                                    unitProgressManager.checkUnitAsPassed(step.getId());
                                }
                                // Get a handler that can be used to post to the main thread

                                mainHandler.post(new Function0<Unit>() {
                                                     @Override
                                                     public Unit invoke() {
                                                         bus.post(new UpdateStepEvent(stepId, false));
                                                         return Unit.INSTANCE;
                                                     }
                                                 }
                                );
                            }
                        }
                    } catch (IOException e) {
                        analytic.reportError(Analytic.Error.PUSH_STATE_EXCEPTION, e);
                    }
                }
                inWork.set(false);
            }
        });
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

}
