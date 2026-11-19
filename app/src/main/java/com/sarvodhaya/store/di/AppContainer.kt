package com.sarvodhaya.store.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sarvodhaya.store.data.firebase.CatalogRepositoryImpl
import com.sarvodhaya.store.data.firebase.FirebaseAuthRepository
import com.sarvodhaya.store.data.firebase.OrderRepositoryImpl
import com.sarvodhaya.store.data.firebase.ShopRepositoryImpl
import com.sarvodhaya.store.domain.repository.AuthRepository
import com.sarvodhaya.store.domain.repository.CatalogRepository
import com.sarvodhaya.store.domain.repository.OrderRepository
import com.sarvodhaya.store.domain.repository.ShopRepository

/** Lightweight manual dependency graph. */
class AppContainer(context: Context) {

    val isFirebaseConfigured: Boolean = runCatching {
        FirebaseApp.initializeApp(context)
        FirebaseApp.getInstance()
    }.isSuccess

    private val auth: FirebaseAuth? = if (isFirebaseConfigured) FirebaseAuth.getInstance() else null
    private val firestore: FirebaseFirestore? = if (isFirebaseConfigured) {
        FirebaseFirestore.getInstance()
    } else {
        null
    }

    val authRepository: AuthRepository = FirebaseAuthRepository(auth, firestore)
    val catalogRepository: CatalogRepository = CatalogRepositoryImpl(firestore)
    val shopRepository: ShopRepository = ShopRepositoryImpl(authRepository, firestore)
    val orderRepository: OrderRepository = OrderRepositoryImpl(
        auth = authRepository,
        shop = shopRepository,
        catalog = catalogRepository,
        db = firestore
    )
}

inline fun <reified VM : ViewModel> containerViewModelFactory(
    crossinline create: () -> VM
): ViewModelProvider.Factory = viewModelFactory {
    initializer { create() }
}
