package com.example.telemedicineapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    @UsersReference
    fun provideDatabaseUserReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("Users")
    }

    @Provides
    @Singleton
    @SlotsReference
    fun provideDatabaseSlotsReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("Slots")
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UsersReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SlotsReference