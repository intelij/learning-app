package org.stepic.droid.web;

import org.stepic.droid.model.Assignment;
import org.stepic.droid.model.Meta;

import java.util.List;

public class AssignmentResponse extends StepicResponseBase {
    private final List<Assignment> assignments;

    public AssignmentResponse(Meta meta, List<Assignment> assignmentList) {
        super(meta);
        this.assignments = assignmentList;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}
