package com.example.learnerapplication.dependency

import android.app.Application
import com.example.learnerapplication.LearnerApplication
import com.example.learnerapplication.data.network.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL: String = "https://api.elearning.alpha.logidots.com/api/"

    @Singleton
    @Provides
    fun providesRetrofit() : Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesRestApiInterface(retrofit: Retrofit) : ApiInterface = retrofit.create(ApiInterface::class.java)

    @Singleton
    @Provides
    fun providesStudentApplication(application: Application): LearnerApplication = application as LearnerApplication


}