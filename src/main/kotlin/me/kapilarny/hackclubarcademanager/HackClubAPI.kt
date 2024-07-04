package me.kapilarny.hackclubarcademanager

object HackClubAPI {
    class Session {
        var paused = false
        var time = 0
        var elapsed = 0
        var remaining = 0
    }

    fun startSession(desc: String) {
        // Start a new session with the given description
    }

    fun pauseSession() {
        // Pause the current session
    }

    fun resumeSession() {
        // Resume the current session
    }

    fun endSession() {
        // End the current session
    }
}