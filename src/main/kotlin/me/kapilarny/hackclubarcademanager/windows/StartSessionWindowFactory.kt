package me.kapilarny.hackclubarcademanager.windows

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import me.kapilarny.hackclubarcademanager.PluginSettings
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class StartSessionWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val w = StartSessionWindow(toolWindow);
        val content = toolWindow.contentManager.factory.createContent(w.contentPanel, "", false);
        toolWindow.contentManager.addContent(content);
    }

    class StartSessionWindow(toolWindow: ToolWindow) {
        val contentPanel = JPanel();

        private var displayPanel = JPanel();
        init {
            // Get the API key and Slack ID from the PluginSettings
            val apiKey = PluginSettings.instance.state.apiKey;
            val slackID = PluginSettings.instance.state.slackID;

            // Add them to the display panel as labels
            displayPanel.add(JLabel("API Key: $apiKey"));
            displayPanel.add(JLabel("Slack ID: $slackID"));

            contentPanel.layout = BorderLayout(0, 20);
            contentPanel.border = BorderFactory.createEmptyBorder(40, 0, 0, 0);
//            contentPanel.add(JPanel(), BorderLayout.NORTH);
            contentPanel.add(displayPanel, BorderLayout.CENTER);
        }
    }
}