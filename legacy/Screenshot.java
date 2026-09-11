import javax.imageio.ImageIO;
import java.io.File;
import java.awt.*;
import java.awt.image.BufferedImage;
public class Screenshot {
  public static void main(String[] args) {
    try {
      Robot robot = new Robot();
      Rectangle screenSize = new Rectangle(
        Toolkit.getDefaultToolkit().getScreenSize());
      BufferedImage screen = robot.createScreenCapture(screenSize);
      ImageIO.write(screen, "jpg", new File("screenshot.jpg"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
