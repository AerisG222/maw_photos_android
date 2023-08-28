package us.mikeandwan.photos.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Context.getTextColor(isActive: Boolean): Int {
    val colorAttribute = if(isActive) com.google.android.material.R.attr.colorPrimary else com.google.android.material.R.attr.colorOnSurface

    return getColorFromAttribute(colorAttribute)
}

fun Context.getColorFromAttribute(colorAttribute: Int): Int {
    val typedValue = TypedValue()
    val theme: Resources.Theme = this.theme

    theme.resolveAttribute(colorAttribute, typedValue, true)

    return typedValue.data
}