package org.stepic.droid.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.stepic.droid.R;
import org.stepic.droid.events.InternetIsEnabledEvent;
import org.stepic.droid.events.comments.NewCommentWasAddedOrUpdateEvent;
import org.stepic.droid.events.steps.StepWasUpdatedEvent;
import org.stepic.droid.model.Attempt;
import org.stepic.droid.model.Reply;

import butterknife.BindString;
import butterknife.ButterKnife;

public class PyCharmStepFragment extends StepAttemptFragment {

    private TextView messageField;

    @BindString(R.string.py_message)
    String pyMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        messageField = (TextView) ((LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_py_step, attemptContainer, false);
        messageField.setMovementMethod(LinkMovementMethod.getInstance());
        attemptContainer.addView(messageField);
        unbinder = ButterKnife.bind(this, v);
        actionButton.setVisibility(View.GONE);
        return v;
    }

    @Override
    protected void showAttempt(Attempt attempt) {
        messageField.setText(textResolver.fromHtml(pyMessage));
    }

    @Override
    protected Reply generateReply() {
        return new Reply.Builder()
                .build();
    }

    @Override
    protected void blockUIBeforeSubmit(boolean needBlock) {
        messageField.setEnabled(true);
    }

    @Override
    protected void onRestoreSubmission() {
        messageField.setText(textResolver.fromHtml(pyMessage));
    }


    @Subscribe
    @Override
    public void onInternetEnabled(InternetIsEnabledEvent enabledEvent) {
        super.onInternetEnabled(enabledEvent);
    }

    @Subscribe
    public void onNewCommentWasAdded(NewCommentWasAddedOrUpdateEvent event) {
        super.onNewCommentWasAdded(event);

    }

    @Subscribe
    public void onStepWasUpdated(StepWasUpdatedEvent event) {
        super.onStepWasUpdated(event);
    }
}
