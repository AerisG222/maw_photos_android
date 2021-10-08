package us.mikeandwan.photos.uinew.ui.about

import androidx.lifecycle.ViewModel
import us.mikeandwan.photos.BuildConfig

class AboutViewModel : ViewModel() {
    val version = "v${BuildConfig.VERSION_NAME}"

    var history = """
        a - 2021/10/20
            asdf
            asdf
            asdf
         
        b - 2021/09/09
            asdf
            asdf
            asdf
            
        c - 2021/08/07
            asdf
            asdf
            asdf
    """.trimIndent()
}