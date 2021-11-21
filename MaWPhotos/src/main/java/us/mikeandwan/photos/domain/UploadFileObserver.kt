package us.mikeandwan.photos.domain

import android.os.FileObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter

class UploadFileObserver(private val directory: File)
    : FileObserver(directory, CREATE or DELETE)
{
    private val _fileQueue = MutableStateFlow(emptyList<File>())
    val fileQueue = _fileQueue.asStateFlow()

    init {
        updateFileList()
    }

    // https://titanwolf.org/Network/Articles/Article?AID=9bbd9002-6da1-4008-8aa2-8a516fd60fa9
    override fun onEvent(event: Int, path: String?) {
        if(path == null) {
            return
        }

        /* The value of event is the value after OR operation with 0x40000000, so you need to perform AND operation with FileObserver.ALL_EVENTS before the case */
        when (event and ALL_EVENTS) {
            CREATE -> updateFileList()
            DELETE -> updateFileList()
        }
    }

    private fun updateFileList() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                _fileQueue.value = directory.listFiles(FileFilter { it.isFile })!!.asList()
            }
        }
    }
}