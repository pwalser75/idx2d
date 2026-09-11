package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.filter.Convolution;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.IsoGrid8x8;
import idx2d.physics.Oscillator;

/** Port of the original {@code Distorter2Applet}. */
public final class Distorter2Demo extends DemoView {

    private static final double DEG2RAD = 3.14159265 / 180;

    private final Texture source;
    private final Grid8x8 grid;
    private final GridDistorter distorter;
    private final Oscillator oscX;
    private final Oscillator oscY;

    private Texture output;

    public Distorter2Demo() {
        super(640, 480);
        source = Convolution.apply(Convolution.createGaussianKernel(2.5f, 5), loadTexture("textures/idx3d.jpg"));
        grid = new IsoGrid8x8(canvasWidth(), canvasHeight());
        distorter = new FastGridDistorter(canvasWidth(), canvasHeight());
        oscX = new Oscillator(50, canvasWidth() - 50, 36997);
        oscY = new Oscillator(50, canvasHeight() - 50, 24989);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            applyEffect();
            render(output);
        }
    }

    private void applyEffect() {
        long time = elapsedMillis();
        double rot = (time / 50.0) % 360;

        double px = oscX.getValue(time) / 8;
        double py = oscY.getValue(time) / 8;

        double xdist, ydist, dist;
        double zoomfact = Math.sin(rot * DEG2RAD) + 1.2;
        double rot2 = Math.sin(rot * DEG2RAD) * 360;

        for (int j = 0; j < grid.height; j++) {
            ydist = ((double) j - py) / grid.height;
            for (int i = 0; i < grid.width; i++) {
                xdist = ((double) i - px) / grid.width;
                double twirl = 64 * Math.sin(8 * grid.radius(i, j));
                double angle = (rot + twirl) * DEG2RAD;
                grid.node[i][j].u = xdist * Math.sin(angle) - ydist * Math.cos(angle);
                grid.node[i][j].v = xdist * Math.cos(angle) + ydist * Math.sin(angle);
                grid.node[i][j].z = 6 + 3 * (Math.sin(xdist * 8) + Math.cos(ydist * 8)) * grid.radius(i, j)
                        + 2.4 * Math.sin(angle);
            }
        }

        output = distorter.distort(source, grid);
    }
}
