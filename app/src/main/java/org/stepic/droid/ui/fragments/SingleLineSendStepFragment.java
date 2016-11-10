package org.stepic.droid.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.stepic.droid.R;
import org.stepic.droid.events.comments.NewCommentWasAddedOrUpdateEvent;
import org.stepic.droid.events.steps.StepWasUpdatedEvent;
import org.stepic.droid.model.Attempt;

public abstract class SingleLineSendStepFragment extends StepAttemptFragment {

    protected EditText answerField;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        answerField = (EditText) ((LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_single_line_attempt, attemptContainer, false);
        attemptContainer.addView(answerField);
        answerField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    actionButton.performClick();
                    handled = true;
                }
                return handled;
            }
        });
        return v;
    }


    @Override
    protected final void showAttempt(Attempt attempt) {
        answerField.getText().clear();
    }

    @Override
    protected final void blockUIBeforeSubmit(boolean needBlock) {
        answerField.setEnabled(!needBlock);
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
