package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.IsoGrid8x8;
import idx2d.physics.Oscillator;

/** Port of the original {@code DistorterApplet}. */
public final class DistorterDemo extends DemoView {

    private static final double DEG2RAD = 3.14159265 / 180;

    private final Texture source;
    private final Grid8x8 grid;
    private final GridDistorter distorter;
    private final Oscillator oscX;
    private final Oscillator oscY;

    private Texture output;
    private long time = 0;
    private double rot = 0;
    private double zoomfact = 1;

    public DistorterDemo() {
        super(640, 480);
        source = loadTexture("textures/idx3d.jpg");
        grid = new IsoGrid8x8(canvasWidth(), canvasHeight());
        distorter = new FastGridDistorter(canvasWidth(), canvasHeight());
        oscX = new Oscillator(50, canvasWidth() - 50, 8000);
        oscY = new Oscillator(50, canvasHeight() - 50, 13000);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            applyEffect();
            render(output);
        }
    }

    private void applyEffect() {
        time += 50;

        double px = oscX.getValue(time) / 8;
        double py = oscY.getValue(time) / 8;

        double xdist, ydist, dist;
        double rot2 = Math.sin(rot * DEG2RAD) * 360;
        double sin = Math.sin(rot2 * DEG2RAD) * zoomfact;
        double cos = Math.cos(rot2 * DEG2RAD) * zoomfact;

        for (int j = 0; j < grid.height; j++) {
            ydist = ((double) j - py) / grid.height;
            for (int i = 0; i < grid.width; i++) {
                xdist = ((double) i - px) / grid.width;
                grid.node[i][j].u = xdist * sin - ydist * cos;
                grid.node[i][j].v = xdist * cos + ydist * sin;
            }
        }
        zoomfact = Math.sin(rot * DEG2RAD) + 1.2;

        for (int j = 0; j < grid.height; j++) {
            ydist = ((double) j / grid.height) - 0.5;
            for (int i = 0; i < grid.width; i++) {
                xdist = ((double) i / grid.width) - 0.5;
                dist = Math.sqrt(xdist * xdist + ydist * ydist);
                grid.node[i][j].u *= dist * Math.sin(rot2 * DEG2RAD * dist);
                grid.node[i][j].v *= Math.cos(2 * rot2 * DEG2RAD * dist);
                grid.node[i][j].z = 1 + 8 * grid.radius(i, j) * Math.exp(-Math.sin(0.00001 * time));
            }
        }
        rot = (rot + 1) % 360;
        output = distorter.distort(source, grid);
    }
}
