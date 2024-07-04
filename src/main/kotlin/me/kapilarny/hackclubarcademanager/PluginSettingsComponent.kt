package me.kapilarny.hackclubarcademanager

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


class PluginSettingsComponent {
    val panel: JPanel;
    private val apiKeyField = JBTextField();
    private val slackIDField = JBTextField();
//    private val apiCheckBox = JBCheckBox("API Key");

    init {
        @Suppress("DialogTitleCapitalization")
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("API Key:"), apiKeyField, 1, false)
                .addLabeledComponent(JBLabel("Slack ID:"), slackIDField, 1, false)
//                .addComponent(apiCheckBox, 1)
                .addComponentFillVertically(JPanel(), 0)
                .panel
    }

    var apiKey: String
        get() = apiKeyField.text
        set(value) {
            apiKeyField.text = value
        }

    var slackID: String
        get() = slackIDField.text
        set(value) {
            slackIDField.text = value
        }

    fun createComponent(): JComponent = panel
}