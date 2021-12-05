package luxoft.kirilin;

import java.awt.*;

public class GUI {

    private final TrayIcon trayIcon;

    public GUI() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit()
                    .createImage(getClass().getResource("/icon-github.png"));

            trayIcon = new TrayIcon(image, "MyGIT");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("My GIT icon");

            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void showNotice(String title, String text){
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
