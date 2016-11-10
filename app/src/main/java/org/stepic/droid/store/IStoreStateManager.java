package org.stepic.droid.store;

import org.stepic.droid.model.Step;

public interface IStoreStateManager {
    void updateUnitLessonState(long lessonId);

    void updateUnitLessonAfterDeleting(long lessonId);

    void updateStepAfterDeleting(Step step);

    void updateSectionAfterDeleting(long sectionId);

    void updateSectionState(long sectionId);
}
