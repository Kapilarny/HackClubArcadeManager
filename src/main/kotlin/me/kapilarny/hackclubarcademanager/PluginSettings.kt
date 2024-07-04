package me.kapilarny.hackclubarcademanager

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service

@State(
        name = "PluginSettings",
        storages = [com.intellij.openapi.components.Storage("HackClubArcadeManager.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    // Static INSTANCE variable
    companion object {
        val instance: PluginSettings
            get() = service();
    }

    class State {
//        @NonNls
        var apiKey: String? = null;
        var slackID: String? = null;
    }

    private var state = State();

    override fun getState(): State {
        return state;
    }

    override fun loadState(state: State) {
        this.state = state;
    }
}