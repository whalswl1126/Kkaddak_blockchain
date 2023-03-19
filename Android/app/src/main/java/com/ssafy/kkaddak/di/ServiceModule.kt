package com.ssafy.kkaddak.di

import com.ssafy.kkaddak.AuthInterceptorClient
import com.ssafy.kkaddak.NoAuthInterceptorClient
import com.ssafy.kkaddak.RefreshInterceptorClient
import com.ssafy.kkaddak.data.remote.service.AuthApiService
import com.ssafy.kkaddak.data.remote.service.RefreshApiService
import com.ssafy.kkaddak.data.remote.service.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideAuthApiService(
        @NoAuthInterceptorClient retrofit: Retrofit
    ): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideRefreshApiService(
        @RefreshInterceptorClient retrofit: Retrofit
    ): RefreshApiService =
        retrofit.create(RefreshApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(
        @AuthInterceptorClient retrofit: Retrofit
    ): UserApiService =
        retrofit.create(UserApiService::class.java)
}