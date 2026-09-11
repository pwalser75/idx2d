package idx2d;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;

/**
 * Loads {@link Texture}s and images from the application classpath (for example
 * {@code /textures/horny.jpg}), replacing the applet-era {@code getImage(getDocumentBase(), ...)}
 * mechanism.
 */
public final class TextureLoader {

    private TextureLoader() {
    }

    public static Texture load(String resourcePath) {
        return new Texture(loadImage(resourcePath));
    }

    public static BufferedImage loadImage(String resourcePath) {
        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        try (InputStream in = TextureLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
            }
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new IllegalArgumentException("Unsupported or corrupt image: " + resourcePath);
            }
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load image: " + resourcePath, e);
        }
    }
}
