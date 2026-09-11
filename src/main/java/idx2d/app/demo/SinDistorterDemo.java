package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.app.Params;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.RotoZoomer;
import idx2d.grid.SinDistorter;
import idx2d.physics.Oscillator;

/** Port of the original {@code SinDistorterApplet}. */
public final class SinDistorterDemo extends DemoView {

    private final Texture texture;
    private Grid8x8 grid;
    private final GridDistorter distorter;
    private final Oscillator angleOscillator;

    private Texture output;
    private long time = 0;

    public SinDistorterDemo(Params params) {
        super(640, 480);
        texture = loadTexture(params.getString("texture", "textures/escape.jpg"));

        grid = new Grid8x8(canvasWidth(), canvasHeight());
        distorter = new FastGridDistorter(canvasWidth(), canvasHeight());
        angleOscillator = new Oscillator(-480, 480, 20000);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            sinDistort();
            render(output);
        }
    }

    private void sinDistort() {
        time += 50;

        double angle = angleOscillator.getValue(time);

        grid.reset();
        grid = RotoZoomer.rotoZoom(grid, angle, 1);

        output = distorter.distort(texture, SinDistorter.distort(grid, 0.04, 3, 0, 0.04, 2, 0));
    }
}
