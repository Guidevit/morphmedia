package com.example.lhm3d.service

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.lhm3d.model.SubscriptionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Service for in-app billing operations.
 */
class BillingService(private val context: Context) : PurchasesUpdatedListener {
    
    private val TAG = "BillingService"
    
    companion object {
        const val PREMIUM_MONTHLY = "premium_monthly"
        const val PREMIUM_YEARLY = "premium_yearly"
    }
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val firebaseService = FirebaseService()
    
    // Billing client
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    
    // Product details
    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails
    
    // Subscription state
    private val _subscriptionState = MutableStateFlow<SubscriptionType>(SubscriptionType.FREE_TRIAL)
    val subscriptionState: StateFlow<SubscriptionType> = _subscriptionState
    
    // Purchase callback
    private var purchaseCallback: ((Result<Purchase>) -> Unit)? = null

    init {
        connectToGooglePlay()
    }

    /**
     * Connect to Google Play.
     */
    private fun connectToGooglePlay() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The billing client is ready
                    queryAvailableProducts()
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Try to reconnect
                connectToGooglePlay()
            }
        })
    }

    /**
     * Query available products.
     */
    private fun queryAvailableProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = productDetailsList
            }
        }
    }

    /**
     * Query existing purchases.
     */
    private fun queryPurchases() {
        coroutineScope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            
            val result = billingClient.queryPurchasesAsync(params)
            
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(result.purchasesList)
            }
        }
    }

    /**
     * Process purchases.
     */
    private fun processPurchases(purchases: List<Purchase>) {
        coroutineScope.launch {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Acknowledge purchase if not already acknowledged
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase.purchaseToken)
                    }
                    
                    // Update subscription state based on purchased product
                    when {
                        purchase.products.contains(PREMIUM_MONTHLY) -> {
                            _subscriptionState.value = SubscriptionType.PREMIUM_MONTHLY
                            firebaseService.updateSubscription(SubscriptionType.PREMIUM_MONTHLY)
                        }
                        purchase.products.contains(PREMIUM_YEARLY) -> {
                            _subscriptionState.value = SubscriptionType.PREMIUM_YEARLY
                            firebaseService.updateSubscription(SubscriptionType.PREMIUM_YEARLY)
                        }
                    }
                }
            }
        }
    }

    /**
     * Acknowledge a purchase.
     */
    private suspend fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        
        withContext(Dispatchers.IO) {
            billingClient.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    // Handle error
                }
            }
        }
    }

    /**
     * Launch the purchase flow.
     */
    fun launchPurchaseFlow(
        activity: Activity,
        productId: String,
        callback: (Result<Purchase>) -> Unit
    ) {
        purchaseCallback = callback
        
        val productDetail = _productDetails.value.find { it.productId == productId }
        
        if (productDetail != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetail)
                    .build()
            )
            
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            
            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            callback(Result.failure(Exception("Product not found")))
        }
    }

    /**
     * Handle purchase updates.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            // Process the purchases
            processPurchases(purchases)
            
            // Notify the callback
            if (purchases.isNotEmpty()) {
                purchaseCallback?.invoke(Result.success(purchases[0]))
            }
        } else {
            // Handle error
            purchaseCallback?.invoke(
                Result.failure(
                    Exception("Purchase failed: ${billingResult.responseCode}")
                )
            )
        }
    }
}
