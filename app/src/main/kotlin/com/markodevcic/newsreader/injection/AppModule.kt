package com.markodevcic.newsreader.injection

import android.content.Context
import com.markodevcic.newsreader.api.ApiFactory
import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.articles.ArticlesUseCase
import com.markodevcic.newsreader.articles.ArticlesUseCaseImpl
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.storage.ArticlesRepository
import com.markodevcic.newsreader.storage.Repository
import com.markodevcic.newsreader.storage.SourcesRepository
import com.markodevcic.newsreader.sync.SyncUseCase
import com.markodevcic.newsreader.sync.SyncUseCaseImpl
import com.markodevcic.newsreader.util.SHARED_PREFS
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
class AppModule(private val context: Context) {

	@Provides
	fun providesSharedPrefs() = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

	@Provides
	fun providesNewsApi() = ApiFactory.create<NewsApi>()

	@Provides
	fun providesSourcesRepo(): Repository<Source> = SourcesRepository()

	@Provides
	fun providesArticlesRepo(): Repository<Article> = ArticlesRepository()

	@Provides
	fun providesSyncUseCase(newsApi: NewsApi,
							sourcesRepository: Provider<Repository<Source>>,
							articlesRepository: Provider<Repository<Article>>): SyncUseCase = SyncUseCaseImpl(newsApi,
			sourcesRepository,
			articlesRepository)

	@Provides
	fun providesArticlesUseCase(articlesRepository: Repository<Article>,
								sourcesRepository: Repository<Source>) : ArticlesUseCase =
			ArticlesUseCaseImpl(articlesRepository, sourcesRepository)
}