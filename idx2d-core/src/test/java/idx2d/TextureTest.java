package idx2d;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class TextureTest {

    @Test
    void allocatesPixelBuffer() {
        Texture t = new Texture(4, 3);
        assertEquals(4, t.width);
        assertEquals(3, t.height);
        assertEquals(12, t.pixel.length);
    }

    @Test
    void bufferedImageSharesBackingPixelArray() {
        Texture t = new Texture(2, 2);
        t.pixel[0] = 0xFFFF0000;
        BufferedImage image = t.getImage();
        assertEquals(0xFFFF0000, image.getRGB(0, 0));

        t.pixel[0] = 0xFF00FF00;
        assertEquals(0xFF00FF00, image.getRGB(0, 0), "image must reflect live pixel updates");
    }

    @Test
    void imageRebuiltWhenPixelArrayReplaced() {
        Texture t = new Texture(2, 2);
        BufferedImage first = t.getImage();
        t.flipHorizontal();
        assertNotSame(first, t.getImage());
    }

    @Test
    void flipHorizontalMirrorsRows() {
        Texture t = new Texture(2, 1);
        t.pixel[0] = 0xFF000001;
        t.pixel[1] = 0xFF000002;
        t.flipHorizontal();
        assertArrayEquals(new int[]{0xFF000002, 0xFF000001}, t.pixel);
    }

    @Test
    void resizeChangesDimensions() {
        Texture t = new Texture(4, 4);
        t.resize(8, 2);
        assertEquals(8, t.width);
        assertEquals(2, t.height);
        assertEquals(16, t.pixel.length);
    }

    @Test
    void cloneIsIndependent() {
        Texture t = new Texture(2, 2);
        t.pixel[0] = 0xFF112233;
        Texture clone = t.getClone();
        clone.pixel[0] = 0xFF445566;
        assertEquals(0xFF112233, t.pixel[0]);
    }

    @Test
    void bilinearPixelStaysInRange() {
        Texture t = new Texture(2, 2);
        for (int i = 0; i < t.pixel.length; i++) {
            t.pixel[i] = 0xFF808080;
        }
        assertEquals(0xFF7F7F7F, t.getBilinearPixel(0.5f, 0.5f));
    }
}
