package com.example.newsapplication.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapplication.repository.NewsRepository

// ViewModelProviderFactory is a class that instantiates and returns view model
class NewsViewModelProviderFactory(val app: Application, val newsRepository: NewsRepository):
    ViewModelProvider.Factory {
       override fun <T : ViewModel> create(modelClass: Class<T>): T {
           return NewsViewModel(app, newsRepository) as T
       }
}