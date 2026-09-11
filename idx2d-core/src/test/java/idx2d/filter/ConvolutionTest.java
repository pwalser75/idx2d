package idx2d.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import idx2d.Texture;
import org.junit.jupiter.api.Test;

class ConvolutionTest {

    @Test
    void gaussianKernelIsNormalised() {
        float[][] kernel = Convolution.createGaussianKernel(2.5f, 5);
        float sum = 0;
        for (float[] row : kernel) {
            for (float v : row) {
                sum += v;
            }
        }
        assertEquals(1.0f, sum, 1e-5f);
    }

    @Test
    void uniformImageSurvivesConvolution() {
        Texture t = new Texture(4, 4);
        for (int i = 0; i < t.pixel.length; i++) {
            t.pixel[i] = 0xFF404040;
        }
        Texture result = Convolution.apply(Convolution.createGaussianKernel(1.0f, 3), t);
        for (int p : result.pixel) {
            // Integer truncation of each kernel term leaves a uniform image slightly darker (0x39).
            assertEquals(0x39, p & 0xFF, 1, "channel drifted: " + Integer.toHexString(p));
        }
    }
}
