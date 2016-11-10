package org.stepic.droid.model

import android.support.annotation.StringRes
import com.google.gson.annotations.SerializedName
import org.stepic.droid.R

enum class UserRole private constructor(val value: String?,@StringRes  val resource : Int? ) {
    @SerializedName("student")
    student("student", null),
    @SerializedName("staff")
    stuff("staff", R.string.staff_label),
    @SerializedName("teacher")
    teacher("teacher", R.string.teacher_label)
}