package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "name") val name: String = "N/A",
    @Json(name = "username") val username: String = "N/A"
)

@JsonClass(generateAdapter = true)
data class ChatItem(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "name") val name: String = "N/A",
    @Json(name = "is_user") val is_user: Boolean = false,
    @Json(name = "is_group") val is_group: Boolean = false,
    @Json(name = "is_channel") val is_channel: Boolean = false,
    @Json(name = "unread") val unread: Int = 0,
    @Json(name = "last_message") val last_message: String = "N/A",
    @Json(name = "photo_url") val photo_url: String? = null
)

@JsonClass(generateAdapter = true)
data class MessageItem(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "sender_id") val sender_id: Long = 0L,
    @Json(name = "text") val text: String = "",
    @Json(name = "date") val date: String = "N/A",
    @Json(name = "out") val out: Boolean = false,
    @Json(name = "has_photo") val has_photo: Boolean = false,
    @Json(name = "photo_url") val photo_url: String? = null,
    @Json(name = "reply_to_msg_id") val reply_to_msg_id: Long? = null
)

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
    @Json(name = "chat_id") val chat_id: Long,
    @Json(name = "text") val text: String,
    @Json(name = "reply_to_msg_id") val reply_to_msg_id: Long? = null
)

@JsonClass(generateAdapter = true)
data class SendMessageResponse(
    @Json(name = "ok") val ok: Boolean = false,
    @Json(name = "id") val id: Long = 0L
)

@JsonClass(generateAdapter = true)
data class SendPhotoPayload(
    @Json(name = "chat_id") val chat_id: Long,
    @Json(name = "caption") val caption: String,
    @Json(name = "photo_base64") val photo_base64: String,
    @Json(name = "reply_to_msg_id") val reply_to_msg_id: Long? = null
)

@JsonClass(generateAdapter = true)
data class SendPhotoResponse(
    @Json(name = "ok") val ok: Boolean = false,
    @Json(name = "id") val id: Long? = null
)

interface NxPanelApi {
    @GET("me")
    suspend fun getMe(): UserProfile

    @GET("chats")
    suspend fun getChats(
        @Query("limit") limit: Int = 50
    ): List<ChatItem>

    @GET("messages/{chat_id}")
    suspend fun getMessages(
        @Path("chat_id") chatId: Long,
        @Query("limit") limit: Int = 50
    ): List<MessageItem>

    @POST("send")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): SendMessageResponse

    @POST("send_photo")
    suspend fun sendPhoto(
        @Body payload: SendPhotoPayload
    ): SendPhotoResponse
}

class NxNetworkRepository {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    @Volatile
    private var cachedBaseUrl: String = ""

    @Volatile
    private var cachedApi: NxPanelApi? = null

    private fun getApi(rawBaseUrl: String): NxPanelApi {
        val normalized = normalizeBaseUrl(rawBaseUrl)
        val currentApi = cachedApi
        if (currentApi != null && cachedBaseUrl == normalized) {
            return currentApi
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(normalized)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val newApi = retrofit.create(NxPanelApi::class.java)
        cachedBaseUrl = normalized
        cachedApi = newApi
        return newApi
    }

    private fun normalizeBaseUrl(url: String): String {
        return try {
            var clean = url.trim()
            if (clean.isEmpty()) {
                clean = "http://127.0.0.1:8080"
            }
            if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
                clean = "http://$clean"
            }
            if (!clean.endsWith("/")) {
                clean = "$clean/"
            }
            clean
        } catch (_: Exception) {
            "http://127.0.0.1:8080/"
        }
    }

    /**
     * Executes a network call wrapped in try-catch with automatic retry up to [maxRetries] times (default 2x).
     */
    private suspend fun <T> executeWithRetry(
        maxRetries: Int = 2,
        block: suspend () -> T
    ): Result<T> {
        var lastError: Throwable? = null
        for (attempt in 0..maxRetries) {
            try {
                val response = block()
                return Result.success(response)
            } catch (e: Throwable) {
                lastError = e
                if (attempt < maxRetries) {
                    delay(350L * (attempt + 1))
                }
            }
        }
        return Result.failure(lastError ?: RuntimeException("Koneksi ke server gagal"))
    }

    suspend fun fetchMe(baseUrl: String): Result<UserProfile> {
        return executeWithRetry(maxRetries = 2) {
            val profile = getApi(baseUrl).getMe()
            UserProfile(
                id = profile.id,
                name = profile.name.ifBlank { "N/A" },
                username = profile.username.ifBlank { "N/A" }
            )
        }
    }

    suspend fun fetchChats(baseUrl: String, limit: Int = 50): Result<List<ChatItem>> {
        return executeWithRetry(maxRetries = 2) {
            getApi(baseUrl).getChats(limit = limit).map { item ->
                item.copy(
                    name = item.name.ifBlank { "N/A" },
                    last_message = item.last_message.ifBlank { "N/A" }
                )
            }
        }
    }

    suspend fun fetchMessages(baseUrl: String, chatId: Long, limit: Int = 50): Result<List<MessageItem>> {
        return executeWithRetry(maxRetries = 2) {
            getApi(baseUrl).getMessages(chatId = chatId, limit = limit).map { msg ->
                msg.copy(
                    text = if (msg.has_photo) msg.text else msg.text.ifBlank { "N/A" },
                    date = msg.date.ifBlank { "N/A" }
                )
            }
        }
    }

    suspend fun postMessage(
        baseUrl: String,
        chatId: Long,
        text: String,
        replyToMsgId: Long? = null
    ): Result<SendMessageResponse> {
        return executeWithRetry(maxRetries = 2) {
            getApi(baseUrl).sendMessage(
                SendMessageRequest(
                    chat_id = chatId,
                    text = text,
                    reply_to_msg_id = replyToMsgId
                )
            )
        }
    }

    suspend fun postPhoto(
        baseUrl: String,
        chatId: Long,
        caption: String,
        photoBase64: String,
        replyToMsgId: Long? = null
    ): Result<SendPhotoResponse> {
        return executeWithRetry(maxRetries = 2) {
            getApi(baseUrl).sendPhoto(
                SendPhotoPayload(
                    chat_id = chatId,
                    caption = caption,
                    photo_base64 = photoBase64,
                    reply_to_msg_id = replyToMsgId
                )
            )
        }
    }
}
