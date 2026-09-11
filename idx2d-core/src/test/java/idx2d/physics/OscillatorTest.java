package idx2d.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OscillatorTest {

    @Test
    void startsAtMinimum() {
        Oscillator osc = new Oscillator(10, 30, 1000);
        assertEquals(10.0, osc.getValue(0), 1e-9);
    }

    @Test
    void staysWithinBounds() {
        Oscillator osc = new Oscillator(5, 15, 1000);
        for (long t = 0; t < 5000; t += 37) {
            double value = osc.getValue(t);
            assertTrue(value >= 5 - 1e-9 && value <= 15 + 1e-9, "value out of range: " + value);
        }
    }
}
