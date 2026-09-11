package idx2d.app.demo;

import idx2d.Texture;
import idx2d.app.DemoView;
import idx2d.app.Params;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.GridOscillator;
import idx2d.grid.IsoGrid8x8;
import idx2d.grid.PhongGridDistorter;
import idx2d.physics.Oscillator;
import java.awt.event.KeyEvent;

/** Port of the original {@code LiquidApplet} (chrome-mapped water droplets). */
public final class LiquidDemo extends DemoView {

    private final Texture source;
    private final Grid8x8 grid;
    private final GridDistorter distorter;
    private final Oscillator oscX;
    private final Oscillator oscY;
    private final double pressure;
    private final double dropsize;

    private Texture output;
    private long time = 0;
    private boolean autodrop = true;
    private boolean random = false;

    public LiquidDemo(Params params) {
        super(480, 480);
        source = loadTexture(params.getString("texture", "textures/horny.jpg"));
        double speedfactor = params.getDouble("speedfactor", 1);
        pressure = params.getDouble("pressure", 0.64);
        dropsize = params.getDouble("dropsize", 1.44);

        grid = new IsoGrid8x8(canvasWidth(), canvasHeight());
        distorter = new PhongGridDistorter(
                loadTexture(params.getString("envmap", "textures/chrome.jpg")),
                loadTexture(params.getString("lightmap", "textures/lightmap.jpg")),
                canvasWidth(), canvasHeight());

        oscX = new Oscillator(50, canvasWidth() - 50, 3000 / speedfactor);
        oscY = new Oscillator(50, canvasHeight() - 50, 2000 / speedfactor);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            applyEffect();
            render(output);
            sleep(10);
        }
    }

    private void applyEffect() {
        time = System.currentTimeMillis();
        if (autodrop) {
            if (random) {
                if (time % 200 == 0) {
                    setDrop(20 + (int) (Math.random() * (canvasWidth() - 40)),
                            20 + (int) (Math.random() * (canvasHeight() - 40)),
                            pressure * 2 * Math.random());
                }
            } else {
                setDrop((int) oscX.getValue(time), (int) oscY.getValue(time), pressure);
            }
        }
        GridOscillator.oscillate(grid, 1, 0.95);

        output = distorter.distort(source, grid);
    }

    private void setDrop(int gridx, int gridy, double pressure) {
        gridx /= 8;
        gridy /= 8;

        int dx, dy;
        double dist;

        for (int x = 0; x < grid.width; x++) {
            for (int y = 0; y < grid.height; y++) {
                dx = gridx - x;
                dy = gridy - y;
                dist = Math.sqrt(dx * dx + dy * dy);
                grid.node[x][y].z -= pressure * Math.exp(-(dist * dist) / (dropsize * dropsize));
            }
        }
    }

    @Override
    protected void onMouseEntered() {
        autodrop = false;
    }

    @Override
    protected void onMouseExited() {
        autodrop = true;
    }

    @Override
    protected void onMousePressed(int x, int y) {
        setDrop(x, y, pressure * 2);
    }

    @Override
    protected void onMouseDragged(int x, int y) {
        setDrop(x, y, 0.4 * pressure);
    }

    @Override
    protected void onKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_M) {
            random = !random;
        }
    }
}
