package idx2d.app.demo;

import idx2d.Color24;
import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.filter.FastBlur;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.RotoZoomer;
import idx2d.physics.Oscillator;

/** Port of the original {@code FeedbackApplet}. */
public final class FeedbackDemo extends DemoView {

    private final Texture texture;
    private final Grid8x8 grid;
    private final GridDistorter distorter;
    private final Oscillator angleOscillator;
    private final Oscillator zoomOscillator;
    private final Oscillator oscx;
    private final Oscillator oscy;
    private final FastBlur fastBlur;

    private Texture output;

    public FeedbackDemo() {
        super(480, 480);
        texture = loadTexture("textures/horny.jpg");
        grid = new Grid8x8(canvasWidth(), canvasHeight());
        distorter = new FastGridDistorter(canvasWidth(), canvasHeight());
        zoomOscillator = new Oscillator(0.92, 0.96, 5000);
        angleOscillator = new Oscillator(-0, 0, 16000);
        oscx = new Oscillator(0.2, 0.8, 8999);
        oscy = new Oscillator(0.2, 0.8, 13111);

        texture.resize(canvasWidth(), canvasHeight());
        output = new Texture(canvasWidth(), canvasHeight());
        fastBlur = new FastBlur(output);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            long time = System.currentTimeMillis();
            mix();
            render(output);
            distort(time);
            blur();
        }
    }

    private void distort(long time) {
        double zoom = zoomOscillator.getValue(time);
        double angle = angleOscillator.getValue(time);
        double x = oscx.getValue(time);
        double y = oscy.getValue(time);

        output = distorter.distort(output, RotoZoomer.rotoZoom(grid, angle, zoom, x, y));
    }

    private void mix() {
        for (int i = texture.pixel.length - 1; i >= 0; i--) {
            output.pixel[i] = Color24.mix(output.pixel[i], texture.pixel[i], 192);
        }
    }

    private void blur() {
        output = fastBlur.blur(output);
    }
}
