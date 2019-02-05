package pro.mezentsev.newsapp.articles

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import pro.mezentsev.newsapp.data.NewsRepository
import pro.mezentsev.newsapp.model.Article

class ArticlesPresenter constructor(private val newsRepository: NewsRepository) : ArticlesContract.Presenter() {
    private val subscriptions = CompositeDisposable()

    override fun load(count: Int, sourceId: String, force: Boolean) {
        subscriptions.clear()

        if (!force) {
            return
        }

        view?.showProgress()

        val subscribe = newsRepository.loadArticles(sourceId, count)
                .map {
                    it.filter { article ->
                        article.title.isNotEmpty()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ articles: List<Article> ->
                    view?.showArticles(articles)
                }, {
                    view?.showError()
                })

        subscriptions.add(subscribe)
    }

    override fun detach() {
        subscriptions.clear()
        super.detach()
    }

    companion object {
        const val TAG = "SourcesPresenter"
    }
}