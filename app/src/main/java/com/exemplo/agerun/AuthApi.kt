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

data class Escala(
    val id: String,
    val titulo: String,
    val descricao: String?,
    val local: String?,
    val inicioAt: String,
    val fimAt: String?,
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

    suspend fun criarEscala(
        accessToken: String,
        titulo: String,
        descricao: String,
        local: String,
        inicioAt: String,
        fimAt: String,
    ): Result<Escala> = withContext(Dispatchers.IO) {
        runCatching {
            val json = requestJson(
                path = "/escalas",
                method = "POST",
                body = JSONObject()
                    .put("titulo", titulo)
                    .put("descricao", descricao)
                    .put("local", local)
                    .put("inicio_at", inicioAt)
                    .put("fim_at", fimAt),
                accessToken = accessToken,
            )

            json.getJSONObject("escala").toEscala()
        }
    }

    suspend fun listarEscalas(accessToken: String): Result<List<Escala>> = withContext(Dispatchers.IO) {
        runCatching {
            val json = requestJson(
                path = "/escalas",
                method = "GET",
                accessToken = accessToken,
            )
            val escalasJson = json.getJSONArray("escalas")

            buildList {
                for (index in 0 until escalasJson.length()) {
                    add(escalasJson.getJSONObject(index).toEscala())
                }
            }
        }
    }

    private suspend fun post(path: String, body: JSONObject): Result<AuthResponse> = withContext(Dispatchers.IO) {
        runCatching {
            requestJson(path = path, method = "POST", body = body).toAuthResponse()
        }
    }

    private fun requestJson(
        path: String,
        method: String,
        body: JSONObject? = null,
        accessToken: String? = null,
    ): JSONObject {
        val connection = (URL("$BASE_URL$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = body != null
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json")
            if (!accessToken.isNullOrBlank()) {
                setRequestProperty("Authorization", "Bearer $accessToken")
            }
        }

        if (body != null) {
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(body.toString())
            }
        }

        val responseText = readResponse(connection)
        val json = if (responseText.isBlank()) JSONObject() else JSONObject(responseText)

        if (connection.responseCode !in 200..299) {
            throw IllegalStateException(json.optString("error", "Nao foi possivel completar a operacao."))
        }

        return json
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

    private fun JSONObject.toEscala(): Escala {
        return Escala(
            id = getString("id"),
            titulo = getString("titulo"),
            descricao = optString("descricao").takeUnless { it == "null" || it.isBlank() },
            local = optString("local").takeUnless { it == "null" || it.isBlank() },
            inicioAt = getString("inicio_at"),
            fimAt = optString("fim_at").takeUnless { it == "null" || it.isBlank() },
        )
    }
}
