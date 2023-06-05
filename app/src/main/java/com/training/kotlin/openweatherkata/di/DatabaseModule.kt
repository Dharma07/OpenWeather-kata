package com.training.kotlin.openweatherkata.di

import android.content.Context
import androidx.room.Room
import com.training.kotlin.loginmvvm.data.local.WeatherItemDatabase
import com.training.kotlin.openweatherkata.data.local.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
 class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): WeatherItemDatabase{
        return Room.databaseBuilder(context,WeatherItemDatabase::class.java,"shoppingDB")
            .build()
    }

    @Singleton
    @Provides
    fun provideProductDao(shoppingDatabase: WeatherItemDatabase): WeatherDao{
        return shoppingDatabase.weatherDao()
    }
}