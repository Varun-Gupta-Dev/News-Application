package com.example.newsapplication.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapplication.models.Article
import com.example.newsapplication.models.NewsResponse
import com.example.newsapplication.repository.NewsRepository
import com.example.newsapplication.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val headLines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var headLinesPage = 1 // To track the current page of headlines pagination
    var headLinesResponse: NewsResponse? = null // To store the last recieved response for headlines

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData() // for displaying search news
    var searchNewsPage = 1 // To track the current page of search news pagination
    var searchNewsResponse: NewsResponse? = null // To store the last received response for search news
    var newSearchQuery: String? = null // To store the new search query
    var oldSearchQuery: String? = null // To store the previous search query

    init {
        getHeadlines("in")
    }
    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headLinesInternet(countryCode)

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse>{

        if(response.isSuccessful){
            response.body()?.let{
                resultResponse->
                headLinesPage++
                if(headLinesResponse == null){
                    headLinesResponse = resultResponse
                }else{
                    val oldArticles = headLinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headLinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{resultResponse ->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1;
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)

    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)

    }

    // For checking internet connectivity of a device
    fun internetConnection(context: Context): Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply{
            return getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)-> true
                    else -> false
                }
            }?: false

        }
    }

    // Handle internet connection specifically for headlines
    private suspend fun headLinesInternet(countryCode:String){
        headLines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadLines(countryCode, headLinesPage)
                headLines.postValue(handleHeadlinesResponse(response))
            }else{
                headLines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> headLines.postValue(Resource.Error("Unable to connect"))
                else -> headLines.postValue(Resource.Error("No connection"))
            }
        }
    }

    // Handle internet connection specifically for search news
    private suspend fun searchNewsInternet(searchQuery: String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                headLines.postValue(handleHeadlinesResponse(response))
            }else{
                headLines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> headLines.postValue(Resource.Error("Unable to connect"))
                else -> headLines.postValue(Resource.Error("No connection"))
            }
        }
    }

}