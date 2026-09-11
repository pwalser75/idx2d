package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.app.Params;
import idx2d.grid.GouraudGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.Tunnel;
import idx2d.physics.Oscillator;

/** Port of the original {@code TunnelApplet}. Click the canvas for hyperspeed. */
public final class TunnelDemo extends DemoView {

    private final Texture texture;
    private Grid8x8 grid;
    private final GouraudGridDistorter distorter;
    private final Oscillator angleOscillator;

    private Texture output;
    private long lastTime = 0;
    private double pos = 0;
    private boolean speedup = false;
    private long speedtime;
    private final long speedintervall = 30000;
    private Oscillator speedOscillator;
    private Oscillator fovOscillator;

    public TunnelDemo(Params params) {
        super(640, 480);
        texture = loadTexture(params.getString("texture", "textures/tunnel.jpg"));

        grid = new Grid8x8(canvasWidth(), canvasHeight());
        distorter = new GouraudGridDistorter(canvasWidth(), canvasHeight());

        angleOscillator = new Oscillator(20, 60, 50000);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            apply();
            render(output);
        }
    }

    private void apply() {
        long time = elapsedMillis();
        long delta = time - lastTime;
        lastTime = time;
        double angle = angleOscillator.getValue(time);
        if (speedup) {
            pos += speedOscillator.getValue(speedtime) * delta / 50.0;
            speedtime += delta;
            if (speedtime > speedintervall) speedup = false;
        } else {
            pos += 0.016 * delta / 50.0;
        }
        double fov = speedup ? fovOscillator.getValue(speedtime) : 90;

        grid = Tunnel.create(grid, fov, angle, pos, 0.5, 1);
        output = distorter.distort(texture, grid);
    }

    @Override
    protected void onMousePressed(int x, int y) {
        if (speedup) return;
        speedtime = 0;
        speedOscillator = new Oscillator(0.016, 0.42, speedintervall);
        fovOscillator = new Oscillator(90, 179.99, speedintervall);
        speedup = true;
    }
}
