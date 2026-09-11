package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.app.Params;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.RotoZoomer;
import idx2d.physics.Oscillator;

/** Port of the original {@code RotoZoomerApplet}. */
public final class RotoZoomerDemo extends DemoView {

    private final Texture texture;
    private final Grid8x8 grid;
    private final GridDistorter distorter;
    private final Oscillator zoomOscillator;
    private final Oscillator angleOscillator;

    private Texture output;

    public RotoZoomerDemo(Params params) {
        super(640, 480);
        texture = loadTexture(params.getString("texture", "textures/horny.jpg"));
        double minZoom = params.getDouble("minZoom", 0.5);
        double maxZoom = params.getDouble("maxZoom", 4);
        double angle = params.getDouble("angle", 100);

        grid = new Grid8x8(canvasWidth(), canvasHeight());
        distorter = new FastGridDistorter(canvasWidth(), canvasHeight());

        zoomOscillator = new Oscillator(minZoom, maxZoom, 8000);
        angleOscillator = new Oscillator(-angle, angle, 20000);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            rotoZoom();
            render(output);
        }
    }

    private void rotoZoom() {
        long time = elapsedMillis();

        double zoom = zoomOscillator.getValue(time);
        double angle = angleOscillator.getValue(time);

        output = distorter.distort(texture, RotoZoomer.rotoZoom(grid, angle, zoom, 0.5, 0.5));
    }
}
