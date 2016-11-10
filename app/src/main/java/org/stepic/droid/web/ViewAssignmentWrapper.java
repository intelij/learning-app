package org.stepic.droid.web;

public class ViewAssignmentWrapper {
    private final ViewAssignment view;

    public ViewAssignmentWrapper(Long assignmentId, long stepId) {
        view = new ViewAssignment(assignmentId, stepId);
    }

    public long getStep() {
        return view.getStep();
    }
}
