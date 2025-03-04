package com.example.newsapplication.repository

import com.example.newsapplication.api.RetrofitInstance
import com.example.newsapplication.db.ArticleDatabase
import com.example.newsapplication.models.Article

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getHeadLines(countryCode: String, pagenumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pagenumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

     fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}
