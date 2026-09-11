package idx2d;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextureBlenderTest {

    private static Texture solid(int color) {
        Texture t = new Texture(2, 2);
        for (int i = 0; i < t.pixel.length; i++) {
            t.pixel[i] = color;
        }
        return t;
    }

    @Test
    void blendZeroReturnsSecondTexture() {
        TextureBlender blender = new TextureBlender(solid(0xFFFFFFFF), solid(0xFF000000));
        assertEquals(0xFF000000, blender.blend(0).pixel[0]);
    }

    @Test
    void blendMaxReturnsFirstTexture() {
        TextureBlender blender = new TextureBlender(solid(0xFFFFFFFF), solid(0xFF000000));
        assertEquals(0xFFFEFEFE, blender.blend(255).pixel[0]);
    }
}
