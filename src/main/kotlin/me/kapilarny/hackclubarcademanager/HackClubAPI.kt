package me.kapilarny.hackclubarcademanager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URI

object HackClubAPI {
    @Serializable
    data class SessionData (
        val id: String,
        @SerialName("createdAt") val createdAt: String,
        val time: Int,
        val elapsed: Int,
        val remaining: Int,
        @SerialName("endTime") val endTime: String,
        var paused: Boolean,
        val completed: Boolean,
        val goal: String,
        val work: String,
        @SerialName("messageTs") val messageTs: String
    )

    @Serializable
    data class Session(
        val ok: Boolean = false,
        val data: SessionData = SessionData("", "", 0, 0, 0, "", false, false, "", "", "")
    )

    var session = Session();

    fun PingAPI() : Boolean {
        val url = URI("https://hackhour.hackclub.com/ping").toURL();

        // Make a GET request to the URL
        // If the request is successful, the API is online
        val conn : HttpURLConnection = url.openConnection() as HttpURLConnection;
        conn.requestMethod = "GET";

        return conn.responseCode == 200;
    }

    fun UpdateSession() : Boolean {
        try {
            val slackID = PluginSettings.instance.state.slackID;
            val url = URI("https://hackhour.hackclub.com/api/session/$slackID").toURL();

            // Add the key as the Authorization header
            val conn : HttpURLConnection = url.openConnection() as HttpURLConnection;
            conn.requestMethod = "GET";
            conn.setRequestProperty("Authorization", "Bearer ${PluginSettings.instance.state.apiKey}");

            // Get the response
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            println(response);

            // Parse the response
            session = Json.decodeFromString(response);

            return conn.responseCode == 200;
        } catch (e: Exception) {
            println(e);
            return false;
        }
    }

    fun PauseSession() : Boolean {
        try {
            val slackID = PluginSettings.instance.state.slackID;
            val url = URI("https://hackhour.hackclub.com/api/pause/$slackID").toURL();

            // Add the key as the Authorization header
            val conn : HttpURLConnection = url.openConnection() as HttpURLConnection;
            conn.requestMethod = "POST";
            conn.setRequestProperty("Authorization", "Bearer ${PluginSettings.instance.state.apiKey}");

            conn.connect();

            // Get the response
            return conn.responseCode == 200;
        } catch (e: Exception) {
            println(e);
        }

        return false;
    }

    fun EndSession() : Boolean {
        try {
            val slackID = PluginSettings.instance.state.slackID;
            val url = URI("https://hackhour.hackclub.com/api/cancel/$slackID").toURL();

            // Add the key as the Authorization header
            val conn : HttpURLConnection = url.openConnection() as HttpURLConnection;
            conn.requestMethod = "POST";
            conn.setRequestProperty("Authorization", "Bearer ${PluginSettings.instance.state.apiKey}");

            conn.connect();

            // Get the response
            return conn.responseCode == 200;
        } catch (e: Exception) {
            println(e);
        }

        return false;
    }
}