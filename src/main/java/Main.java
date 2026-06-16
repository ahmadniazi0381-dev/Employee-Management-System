import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.LoginFrame;

/**
 * Application entry point.
 * Sets the system look-and-feel for a native appearance,
 * then launches the Login screen on the Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {

        // Apply system look-and-feel for better native feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falls back to default Swing look-and-feel
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}