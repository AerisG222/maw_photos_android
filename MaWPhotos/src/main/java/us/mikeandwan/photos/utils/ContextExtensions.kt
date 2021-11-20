package us.mikeandwan.photos.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import us.mikeandwan.photos.R

fun Context.getTextColor(isActive: Boolean): Int {
    val typedValue = TypedValue()
    val theme: Resources.Theme = this.theme
    val colorAttribute = if(isActive) R.attr.colorPrimary else R.attr.colorOnSurface

    theme.resolveAttribute(colorAttribute, typedValue, true)

    return typedValue.data
}