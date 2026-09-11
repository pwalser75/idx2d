package idx2d;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Color24Test {

    @Test
    void packsAndUnpacksChannels() {
        int c = Color24.getColor(0x12, 0x34, 0x56);
        assertEquals(0xFF123456, c);
        assertEquals(0x12, Color24.getRed(c));
        assertEquals(0x34, Color24.getGreen(c));
        assertEquals(0x56, Color24.getBlue(c));
    }

    @Test
    void scaleHalvesChannels() {
        assertEquals(0xFF7F0000, Color24.scale(0xFFFF0000, 128));
    }

    @Test
    void multiplyPinsChannelProduct() {
        assertEquals(0xFF404040, Color24.multiply(0xFF808080, 0xFF808080));
    }

    @Test
    void grayUsesWeightedBrightness() {
        assertEquals(0xFFFFFFFF, Color24.getGray(0xFFFFFFFF));
        assertEquals(0xFF000000, Color24.getGray(0xFF000000));
    }

    @Test
    void createGrayReplicatesChannel() {
        assertEquals(0xFF7F7F7F, Color24.createGray(0x7F));
    }
}
