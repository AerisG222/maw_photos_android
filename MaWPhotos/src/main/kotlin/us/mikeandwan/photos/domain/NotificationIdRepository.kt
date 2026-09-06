package us.mikeandwan.photos.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationIdRepository
    @Inject
    constructor() {
        private val _lockObject = Any()
        private var _id = 1

        fun getAndInc(): Int {
            synchronized(_lockObject) {
                if (_id == Int.MAX_VALUE) {
                    _id = 1
                }

                _id++

                return _id
        }
    }
}
