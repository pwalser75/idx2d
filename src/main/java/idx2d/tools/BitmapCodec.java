package idx2d.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;

/**
 * Small image I/O helper. The original idx2d shipped a hand-written 24/8-bit Windows bitmap
 * reader/writer; {@code javax.imageio} covers that (and more) on the modern JDK, so this class is a
 * thin, static facade.
 */
public final class BitmapCodec {

    private BitmapCodec() {
    }

    public static BufferedImage loadBitmap(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IllegalArgumentException("Unsupported image file: " + file);
            }
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + file, e);
        }
    }

    public static void saveBitmap(File file, BufferedImage image) {
        try {
            if (!ImageIO.write(image, "bmp", file)) {
                throw new IllegalArgumentException("No BMP writer available for " + file);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write " + file, e);
        }
    }
}
