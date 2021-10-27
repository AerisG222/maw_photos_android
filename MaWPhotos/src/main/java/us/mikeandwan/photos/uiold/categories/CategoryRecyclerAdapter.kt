package us.mikeandwan.photos.uiold.categories

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.services.DataServices

abstract class CategoryRecyclerAdapter<T : RecyclerView.ViewHolder?>(
    activity: ICategoryListActivity,
    dataServices: DataServices
) : RecyclerView.Adapter<T>() {
    private val _disposables = CompositeDisposable()
    protected val _context: Context
    @JvmField
    protected val _dataServices: DataServices
    private val _categorySubject = PublishSubject.create<Category>()
    private var _categoryList: List<Category>? = null
    var _activity: ICategoryListActivity

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T
    protected abstract fun displayCategory(category: Category?, imageFile: String?, viewHolder: T)
    protected abstract fun downloadCategoryTeaser(category: Category?): String?

    override fun onBindViewHolder(viewHolder: T, position: Int) {
        val category = _categoryList!![position]
        viewHolder!!.itemView.setOnClickListener { v: View? -> _categorySubject.onNext(category) }
        _disposables.add(Flowable.fromCallable { downloadCategoryTeaser(category) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { x: String? -> displayCategory(category, x, viewHolder) }
            ) { ex: Throwable? -> _activity.onApiException(ex) }
        )
    }

    override fun getItemCount(): Int {
        return _categoryList!!.size
    }

    fun setCategoryList(categoryList: List<Category>?) {
        _categoryList = categoryList
    }

    fun onCategorySelected(): Observable<Category> {
        return _categorySubject.hide()
    }

    fun dispose() {
        _disposables.dispose()
    }

    init {
        _context = activity as Context
        _activity = activity
        _dataServices = dataServices
    }
}