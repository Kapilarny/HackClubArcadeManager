package me.kapilarny.hackclubarcademanager

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class PluginSettingsConfigurable : Configurable {
    private var settingsComponent: PluginSettingsComponent? = null;

    override fun createComponent(): JComponent? {
        settingsComponent = PluginSettingsComponent();
        return settingsComponent?.createComponent();
    }

    override fun isModified(): Boolean {
        return settingsComponent?.apiKey != PluginSettings.instance.state.apiKey || settingsComponent?.slackID != PluginSettings.instance.state.slackID;
    }

    override fun apply() {
        PluginSettings.instance.state.apiKey = settingsComponent?.apiKey;
        PluginSettings.instance.state.slackID = settingsComponent?.slackID;
    }

    override fun reset() {
        settingsComponent?.apiKey = PluginSettings.instance.state.apiKey ?: "";
        settingsComponent?.slackID = PluginSettings.instance.state.slackID ?: "";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Hack Club Arcade Manager";
    }
}