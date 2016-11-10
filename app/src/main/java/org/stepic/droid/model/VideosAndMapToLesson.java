package org.stepic.droid.model;

import java.util.List;
import java.util.Map;

public class VideosAndMapToLesson {
    private final List<CachedVideo> cachedVideoList;
    private final Map<Long, Lesson> stepIdToLesson;

    public VideosAndMapToLesson(List<CachedVideo> cachedVideoList, Map<Long, Lesson> stepIdToLesson) {
        this.cachedVideoList = cachedVideoList;
        this.stepIdToLesson = stepIdToLesson;
    }

    public List<CachedVideo> getCachedVideoList() {
        return cachedVideoList;
    }

    public Map<Long, Lesson> getStepIdToLesson() {
        return stepIdToLesson;
    }
}
