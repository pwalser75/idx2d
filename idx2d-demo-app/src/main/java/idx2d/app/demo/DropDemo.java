package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.app.Params;
import idx2d.grid.BilinearGridDistorter;
import idx2d.grid.Drop;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.IsoGrid8x8;
import idx2d.physics.Oscillator;
import java.awt.Color;

/** Port of the original {@code DropApplet} (uses the bilinear variant by default). */
public final class DropDemo extends DemoView {

    private final Texture source;
    private final GridDistorter distorter;
    private final Oscillator ampOscillator;
    private final Oscillator wavesOscillator;

    private Grid8x8 grid;
    private Texture output;
    private boolean mouseInside = false;

    public DropDemo(Params params) {
        super(600, 324);
        setBackground(new Color(params.getHex("background", 0xFFFFFF)));
        source = loadTexture(params.getString("texture", "textures/car.jpg"));
        grid = new IsoGrid8x8(canvasWidth(), canvasHeight());

        boolean bilinear = params.getBoolean("bilinear", true);
        distorter = bilinear
                ? new BilinearGridDistorter(canvasWidth(), canvasHeight())
                : new FastGridDistorter(canvasWidth(), canvasHeight());

        double interval = params.getDouble("interval", 8000);
        double minwave = params.getDouble("minwave", 3);
        double maxwave = params.getDouble("maxwave", 12);
        double minamp = params.getDouble("minamp", 0.02);
        double maxamp = params.getDouble("maxamp", 0.12);

        ampOscillator = new Oscillator(minamp, maxamp, interval);
        wavesOscillator = new Oscillator(maxwave, minwave, 1.3 * interval);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            sinDistort();
            render(output);
            if (mouseInside) {
                setStatus("DropApplet (c)2001 by Peter Walser [www2.active.ch/proxima]");
            }
        }
    }

    private void sinDistort() {
        long time = elapsedMillis();
        double amp = ampOscillator.getValue(time);
        double waves = wavesOscillator.getValue(time);

        grid.reset();
        grid = Drop.create(grid, amp, waves, ((double) time) / 300d);

        output = distorter.distort(source, grid);
    }

    @Override
    protected void onMouseEntered() {
        mouseInside = true;
    }

    @Override
    protected void onMouseExited() {
        mouseInside = false;
    }
}
