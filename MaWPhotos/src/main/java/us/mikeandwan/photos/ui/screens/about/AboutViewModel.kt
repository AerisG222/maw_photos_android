package us.mikeandwan.photos.ui.screens.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import us.mikeandwan.photos.BuildConfig
import us.mikeandwan.photos.domain.NavigationStateRepository
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val navigationStateRepository: NavigationStateRepository
) : ViewModel() {
    val version = "v${BuildConfig.VERSION_NAME}"

    var history = """
***: NEW VERSION IN PROGRESS

6.2:
- sdk + library updates

6.1:
- improved logging

6.0:
- introduce ability to receive photos and videos shared from other apps, then upload these to mikeandwan.us

5.7:
- remove permissions that are no longer necessary
- improved launch and status icons
- update app name to mirror what is displayed on Google Play
- simplify settings and always check for new categories (every 4hrs)

5.6:
- try to improve notifications on newer android versions

5.5:
- another attempt to get refresh tokens working properly

5.4:
- migrate from poller to jobservice

5.3:
- fix a lame error introduced in the last fix >=|

5.2:
- try to fix a threading issue when refreshing the access token
- provide way to force re-authentication

5.1:
- go immediately to mode screen if already logged in (improved experience when there is poor network condition)

5.0:
- authentication overhaul to coincide with new website
- update dependencies

4.0:
- rewrite / re-imagining of the app design
- fix crash on rotate on the photo list screen
- add more customization options to the photo list screen
- using jack+jill w/ java 8
- leverage OSS libraries:
  - butterknife
  - dagger 2
  - okhttp
  - retrofit (w/ jackson)
  - photoview
  - rxjava 2 / rxandroid 2
- remove complicated threading code
- use constraint layouts
- use vector graphics where possible
- removed use of fragments
- optimize some code paths (ex. initialization is fast now)


3.2:
- fix issue with category thumbnails

3.1:
- updates based on website changes
- fix an issue in random mode
- minor code cleanup / fixes

3.0:
- us.mikeandwan.photos.ui updates to better align with material design
- update category list page to show larger thumbnails
- add new category list view to show only thumbnails
- improve photo screen to show image on larger surface, and to fade out toolbars
- improved tablet support
- store photos in a private folder to avoid polluting other apps like Gallery - photos also deleted on uninstall
- delete photos stored in the old location (restart device to clear Gallery cache)
- update code to use picasso
- update code to use android annotations

2.2:
- now use HTTPS when connecting to services
- internal code naming changes

2.1:
- support for zoom / pan, and now animates the rotation
- now can view set of random images
- new interaction when flinging between images
- optionally hide the toolbar and/or thumbnail list to allow for larger main image
- restructured code to leverage fragments, resulting in much cleaner code/organization
- minor performance optimization for list views
- support older devices (now support sdk9+)

2.0:
- improved performance through use of priority queued parallel background tasks
- added support for older devices thanks to android support libraries
- improved handling of password change related issues
- reduced the processing required when first using application
- new setting to control slideshow interval
- main image prefetch

1.6:
- notification improvements

1.5:
- rotate images
- add comments
- rate picture
- view exif data
- slideshow mode
- notify when network is unavailable
- try to seamlessly handle session timeouts
- improve notifications, hopefully now you are sent into the app

1.4:
- store images in a directory that is clearly associated with this application

1.3:
- fixed crash due to build process stripping details required by jackson parsing library

1.2:
- updates to support older runtimes

1.1:
- improve the progress indicators
- add ability to swipe main image to navigate through pictures

1.0:
First release!  Please let me know if you run into any problems - thanks!
    """.trimIndent()
}