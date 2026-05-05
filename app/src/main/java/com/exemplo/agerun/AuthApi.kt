package com.exemplo.agerun

import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class AuthUser(
    val id: String,
    val nome: String,
    val email: String,
    val role: String,
)

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
)

data class AuthResponse(
    val user: AuthUser,
    val session: AuthSession? = null,
)

object AuthApi {
    private const val BASE_URL = "http://10.0.2.2:3000"

    suspend fun login(email: String, senha: String): Result<AuthResponse> {
        return post(
            path = "/login",
            body = JSONObject()
                .put("email", email)
                .put("senha", senha),
        )
    }

    suspend fun cadastro(nome: String, email: String, senha: String): Result<AuthResponse> {
        return post(
            path = "/cadastro",
            body = JSONObject()
                .put("nome", nome)
                .put("email", email)
                .put("senha", senha),
        )
    }

    private suspend fun post(path: String, body: JSONObject): Result<AuthResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL("$BASE_URL$path").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(body.toString())
            }

            val responseText = readResponse(connection)
            val json = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)

            if (connection.responseCode !in 200..299) {
                throw IllegalStateException(json.optString("error", "Nao foi possivel completar a operacao."))
            }

            json.toAuthResponse()
        }
    }

    private fun readResponse(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        } ?: return ""

        return BufferedReader(stream.reader()).use { reader ->
            reader.readText()
        }
    }

    private fun JSONObject.toAuthResponse(): AuthResponse {
        val userJson = getJSONObject("user")
        val sessionJson = optJSONObject("session")

        return AuthResponse(
            user = AuthUser(
                id = userJson.getString("id"),
                nome = userJson.getString("nome"),
                email = userJson.getString("email"),
                role = userJson.getString("role"),
            ),
            session = sessionJson?.let {
                AuthSession(
                    accessToken = it.optString("access_token"),
                    refreshToken = it.optString("refresh_token"),
                )
            },
        )
    }
}
