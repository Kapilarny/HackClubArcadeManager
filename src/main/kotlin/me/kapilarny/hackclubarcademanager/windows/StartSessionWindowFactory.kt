package me.kapilarny.hackclubarcademanager.windows

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.util.ui.JBUI
import me.kapilarny.hackclubarcademanager.HackClubAPI
import me.kapilarny.hackclubarcademanager.PluginSettings
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*


class StartSessionWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val w = StartSessionWindow(toolWindow);
        val content = toolWindow.contentManager.factory.createContent(w.contentPanel, "", false);
        toolWindow.contentManager.addContent(content);
    }

    enum class WindowState {
        NOT_STARTED,
        IN_PROGRESS,
        PAUSED,
        ERROR
    }

    class StartSessionWindow(toolWindow: ToolWindow) {
        val contentPanel = JPanel();

        var state = WindowState.NOT_STARTED;
        val remainingTime = JLabel("Remaining Time: ");
        val descText = JTextField(1);

        private var displayPanel = JPanel();
        init {
            contentPanel.layout = BorderLayout(0, 20);
            contentPanel.border = BorderFactory.createEmptyBorder(40, 0, 0, 0);

            // Check if the API is online
            val apiOnline = HackClubAPI.PingAPI();
//            println("API Online: $apiOnline");
            if(!apiOnline) {
                state = WindowState.ERROR;
            }

            updateSession();

            // Update the session every 60 seconds
            Timer(60000) {
                updateSession();
            }.start();
        }

        fun update() {
            if(state == WindowState.ERROR) {
                // Display an error message
                displayPanel.add(JLabel("An error occurred while accessing the API. Ensure that your API key and Slack ID are correct and try again!"));
                return;
            }

            // Get the API key and Slack ID from the PluginSettings
            var apiKey = PluginSettings.instance.state.apiKey;
            var slackID = PluginSettings.instance.state.slackID;

            // Add them to the display panel as labels
            if(apiKey.equals("") || slackID.equals("")) {
                // If the API key or Slack ID is empty, display a message to the user
                displayPanel.add(JLabel("Please enter your API key and Slack ID in the settings panel to start a session!"));
                // Add refresh button
                val refreshButton = JButton("Refresh");
                displayPanel.add(refreshButton);
                refreshButton.addActionListener {
                    // Load the API key and Slack ID from the PluginSettings
                    apiKey = PluginSettings.instance.state.apiKey;
                    slackID = PluginSettings.instance.state.slackID;

                    // Print the API key and Slack ID to the console
//                    println("API Key: $apiKey");
//                    println("Slack ID: $slackID");
                }

                return;
            }

            if(state == WindowState.NOT_STARTED ) {
                // Create a form
                val btn = JButton("Start Session");

                displayPanel = JPanel(BorderLayout())


                // Create a panel for the form with padding
                val formPanel = JPanel(GridBagLayout())
                formPanel.border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
                val gbc = GridBagConstraints()
                gbc.fill = GridBagConstraints.HORIZONTAL
                gbc.insets = JBUI.insets(5)


                // Session Description Label
                gbc.gridx = 0
                gbc.gridy = 0
                gbc.gridwidth = 1
                formPanel.add(JLabel("Session Description:"), gbc)


                // Session Description Field
                gbc.gridx = 1
                gbc.gridy = 0
                gbc.gridwidth = 2
                formPanel.add(descText, gbc)


                // Send Button
                gbc.gridx = 1
                gbc.gridy = 1
                gbc.gridwidth = 1
                formPanel.add(btn, gbc)


                // Add form panel to the main content panel
                displayPanel.add(formPanel, BorderLayout.NORTH)

                btn.addActionListener {
                    if(descText.text.equals("")) {
                        return@addActionListener;
                    }

                    if(!HackClubAPI.StartSession(descText.text) || !updateSession()) {
                        state = WindowState.ERROR;
                        render();
                        return@addActionListener;
                    }

                    render();
                }
            }

            if(state == WindowState.IN_PROGRESS || state == WindowState.PAUSED) {
                // Create a panel for the form with padding
                val formPanel = JPanel(GridBagLayout())
                formPanel.border = BorderFactory.createEmptyBorder(10, 5, 10, 5)
                val gbc = GridBagConstraints()
                gbc.fill = GridBagConstraints.HORIZONTAL
                gbc.insets = JBUI.insets(5)

                // Session Description Label
                gbc.gridx = 0
                gbc.gridy = 0
                gbc.gridwidth = 1
                formPanel.add(JLabel("Session Description: ${descText.text}"), gbc)

                // Remaining Time Label
                gbc.gridx = 0
                gbc.gridy = 1
                gbc.gridwidth = 1
                formPanel.add(remainingTime, gbc)

                // End, Pause/Resume Button
                val endButton = JButton("End Session")
                val pauseResumeButton = JButton(if (state == WindowState.PAUSED) "Resume Session" else "Pause Session");
                gbc.gridx = 0
                gbc.gridy = 2
                gbc.gridwidth = 1
                formPanel.add(endButton, gbc)
                gbc.gridx = 2
                formPanel.add(pauseResumeButton, gbc)

                endButton.addActionListener {
                    if(!HackClubAPI.EndSession()) {
                        state = WindowState.ERROR;
                        render();
                        return@addActionListener;
                    }

                    render();
                }

                pauseResumeButton.addActionListener {
                    if(!HackClubAPI.PauseSession()) {
                        state = WindowState.ERROR;
                        render();
                        return@addActionListener;
                    }

                    state = if(state == WindowState.PAUSED) WindowState.IN_PROGRESS else WindowState.PAUSED;
                    HackClubAPI.session.data.paused = !HackClubAPI.session.data.paused;

                    render();
                }

                // Add form panel to the main content panel
                displayPanel.add(formPanel, BorderLayout.NORTH)
            }
        }

        fun render() {
            // Clear the display panel
            contentPanel.removeAll();

            displayPanel = JPanel();

            update();

            // Re-render the display panel
            contentPanel.add(displayPanel, BorderLayout.CENTER);

            contentPanel.revalidate();
            contentPanel.repaint();
        }

        fun updateSession() : Boolean {
//            println("Updating session...");

            // Update the session
            if(!HackClubAPI.UpdateSession()) {
                state = WindowState.ERROR;
                return false;
            }

            if(HackClubAPI.session.data.completed) {
                state = WindowState.NOT_STARTED;
            } else {
                state = WindowState.IN_PROGRESS;

                descText.text = HackClubAPI.session.data.work;

                if(HackClubAPI.session.data.paused) {
                    state = WindowState.PAUSED;
                } else {
                    state = WindowState.IN_PROGRESS;
                }
            }

            remainingTime.text = "Remaining Time: ${HackClubAPI.session.data.remaining}";

            render();

            return true;
        }
    }
}