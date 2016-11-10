package org.stepic.droid.model.comments

import android.support.annotation.StringRes
import org.stepic.droid.R

enum class DiscussionOrder private constructor(val id: Int, @StringRes val stringResId : Int, val menuId : Int) {
    lastDiscussion(0, R.string.last_discussion, R.id.menu_item_last_discussion),
    mostLiked(1, R.string.most_liked_discussion, R.id.menu_item_most_liked),
    mostActive(2, R.string.most_active_discussion, R.id.menu_item_most_active),
    recentActive(3, R.string.recent_activity_discussion, R.id.menu_item_recent_activity);

    fun getOrder(dp: DiscussionProxy) =
            when (id) {
                0 -> dp.discussions
                1 -> dp.discussions_most_liked
                2 -> dp.discussions_most_active
                3 -> dp.discussions_recent_activity
                else -> dp.discussions }

    companion object {
        fun getById(idFromUser: Int) =
                when (idFromUser) {
                    0 -> lastDiscussion
                    1 -> mostLiked
                    2 -> mostActive
                    3 -> recentActive
                    else -> lastDiscussion
                }
    }
}