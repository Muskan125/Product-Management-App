package com.practice.trialapp.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Transaction
import com.practice.trialapp.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class NoInternetException(message: String = "No internet connection") : IOException(message)

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val productsCollection
        get() = firestore.collection(PRODUCTS_COLLECTION)

    fun observeProducts(): Flow<List<DocumentSnapshot>> = callbackFlow {
        val registration = productsCollection.addSnapshotListener(ioDispatcher.asExecutor()) { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.documents.orEmpty())
        }
        awaitClose { registration.remove() }
    }

    fun generateProductId(): String = productsCollection.document().id

    suspend fun addProduct(id: String, data: Map<String, Any>) {
        writeOnServer { it.set(productsCollection.document(id), data) }
    }

    suspend fun updateProduct(id: String, data: Map<String, Any>) {
        writeOnServer { it.update(productsCollection.document(id), data) }
    }

    suspend fun deleteProduct(id: String) {
        writeOnServer { it.delete(productsCollection.document(id)) }
    }

    private suspend fun writeOnServer(block: (Transaction) -> Unit) = withContext(ioDispatcher) {
        if (!isOnline()) throw NoInternetException()

        try {
            withTimeout(WRITE_TIMEOUT_MS) {
                firestore.runTransaction { transaction -> block(transaction) }.await()
            }
        } catch (e: TimeoutCancellationException) {
            throw NoInternetException("Request timed out, check your connection")
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) throw NoInternetException()
            throw e
        }
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    companion object {
        private const val PRODUCTS_COLLECTION = "products"
        private const val WRITE_TIMEOUT_MS = 10_000L
    }
}
