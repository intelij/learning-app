package org.stepic.droid.ui.fragments;

import com.squareup.otto.Subscribe;

import org.stepic.droid.events.InternetIsEnabledEvent;
import org.stepic.droid.events.comments.NewCommentWasAddedOrUpdateEvent;
import org.stepic.droid.events.steps.StepWasUpdatedEvent;
import org.stepic.droid.model.Reply;

public class MathStepFragment extends SingleLineSendStepFragment {

    @Override
    protected Reply generateReply() {
        return new Reply.Builder()
                .setFormula(answerField.getText().toString())
                .build();
    }

    @Override
    protected void onRestoreSubmission() {
        Reply reply = submission.getReply();
        if (reply == null) return;

        String text = reply.getFormula();
        answerField.setText(text);
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
