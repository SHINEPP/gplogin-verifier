package com.chat.googleapi.app.module

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.ProductPurchase
import com.google.api.services.androidpublisher.model.SubscriptionPurchase
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials


object GooglePayVerifier {

    private const val PACKAGE_NAME = "com.period.tracker.health.reminder.women"
    private const val CREDENTIALS_PATH = "storage-file-manager-c513341ef071.json"

    // 消耗商品购买状态 - 已购买
    private const val PRODUCT_PURCHASE_PURCHASE_STATE_PURCHASED = 0

    // 消耗商品消耗状态 - 未消耗
    private const val PRODUCT_PURCHASE_CONSUMPTION_STATE_UNCONSUMED = 0

    // 消耗商品消耗状态 - 已消耗
    private const val PRODUCT_PURCHASE_CONSUMPTION_STATE_CONSUMED = 1

    // 消耗商品确认状态 - 待确认
    private const val PRODUCT_PURCHASE_ACKNOWLEDGEMENT_STATE_UNACKED = 0

    // 消耗商品确认状态 - 已确认
    private const val PRODUCT_PURCHASE_ACKNOWLEDGEMENT_STATE_ACKED = 1

    // 订阅商品付款状态 - 已收款 
    private const val SUBSCRIPTIONS_PURCHASE_PAYMENT_STATE_RECEIVED = 1

    // 订阅商品确认状态 - 待确认 
    private const val SUBSCRIPTIONS_PURCHASE_ACKNOWLEDGEMENT_STATE_UNACKED = 0

    // 订阅商品确认状态 - 已确认
    private const val SUBSCRIPTIONS_PURCHASE_ACKNOWLEDGEMENT_STATE_ACKED = 1

    private val androidPublisher: AndroidPublisher

    init {
        val credentials = GoogleCredentials.fromStream(GooglePayVerifier::class.java.classLoader.getResourceAsStream(CREDENTIALS_PATH))
        androidPublisher = AndroidPublisher.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        ).build()
    }

    /**
     * 获取消耗型商品订单
     * @param productId: 商品
     * @param token: 用于校验订单
     *
     * @return 返回空表示订单不存在
     */
    fun getProductPurchase(productId: String, token: String): ProductPurchase? {
        val purchases = try {
            androidPublisher.purchases().products().get(PACKAGE_NAME, productId, token).execute()
        } catch (e: Throwable) {
            println("getProductPurchase(), e = $e")
            null
        }
        println("getProductPurchase(), purchaseState = ${purchases?.purchaseState}")
        return purchases
    }

    /**
     * 获取订阅型商品订单
     * @param productId: 商品
     * @param token: 用于校验订单
     *
     * @return 返回空表示订单不存在
     */
    fun getSubscriptionsPurchase(productId: String, token: String): SubscriptionPurchase? {
        val purchases = try {
            androidPublisher.purchases().subscriptions().get(PACKAGE_NAME, productId, token).execute()
        } catch (e: Throwable) {
            println("getSubscriptionsPurchase(), e = $e")
            null
        }
        println("getSubscriptionsPurchase(), acknowledgementState = ${purchases?.acknowledgementState}")
        return purchases
    }

    /**
     * 消耗型商品订单核销
     * @param productId: 商品
     * @param token: 用于校验订单
     *
     * @return 核销成功返回true，失败返回false
     */
    fun consumeProductPurchase(productId: String, token: String): Boolean {
        val productPurchase = getProductPurchase(productId, token)
            ?: return false
        // 订单未支付
        if (productPurchase.purchaseState != PRODUCT_PURCHASE_PURCHASE_STATE_PURCHASED) {
            println("订单未支付, productId = $productId, token = $token")
            return false
        }
        // 检查订单是否待核销状态
        val consumptionState = productPurchase.consumptionState
        if (consumptionState != PRODUCT_PURCHASE_CONSUMPTION_STATE_UNCONSUMED) {
            if (consumptionState == PRODUCT_PURCHASE_CONSUMPTION_STATE_CONSUMED) {
                println("订单早已核销, productId = $productId, token = $token")
            } else {
                println("订单错误核销状态, productId = $productId, token = $token")
            }
            return false
        }
        // 核销订单
        return try {
            androidPublisher.purchases().products().acknowledge(PACKAGE_NAME, productId, token, null).execute()
            true
        } catch (e: Throwable) {
            println("订单核销失败, productId = $productId, token = $token")
            false
        }
    }

    /**
     * 订阅型商品订单核销
     * @param productId: 商品
     * @param token: 用于校验订单
     *
     * @return 核销成功返回true，失败返回false
     */
    fun acknowledgeSubscriptionsPurchase(productId: String, token: String): Boolean {
        val subscriptionPurchase = getSubscriptionsPurchase(productId, token)
            ?: return false
        // 订单被取消
        if (subscriptionPurchase.cancelReason != null) {
            println("订单早已取消, productId = $productId, token = $token")
            return false
        }
        // 订单未支付
        if (subscriptionPurchase.paymentState != SUBSCRIPTIONS_PURCHASE_PAYMENT_STATE_RECEIVED) {
            println("订单未支付, productId = $productId, token = $token")
            return false
        }
        // 检查订单是否待核销状态
        val acknowledgementState = subscriptionPurchase.acknowledgementState
        if (acknowledgementState != SUBSCRIPTIONS_PURCHASE_ACKNOWLEDGEMENT_STATE_UNACKED) {
            if (acknowledgementState == SUBSCRIPTIONS_PURCHASE_ACKNOWLEDGEMENT_STATE_ACKED) {
                println("订单早已核销, productId = $productId, token = $token")
            } else {
                println("订单错误核销状态, productId = $productId, token = $token")
            }
            return false
        }
        // 核销订单
        return try {
            androidPublisher.purchases().subscriptions().acknowledge(PACKAGE_NAME, productId, token, null).execute()
            true
        } catch (e: Throwable) {
            println("订单核销失败, productId = $productId, token = $token")
            false
        }
    }
}