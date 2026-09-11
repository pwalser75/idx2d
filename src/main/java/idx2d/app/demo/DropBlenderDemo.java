package idx2d.app.demo;

import idx2d.Texture;
import idx2d.TextureBlender;
import idx2d.app.DemoView;
import idx2d.app.Params;
import idx2d.grid.Drop;
import idx2d.grid.FastGridDistorter;
import idx2d.grid.Grid8x8;
import idx2d.grid.GridDistorter;
import idx2d.grid.IsoGrid8x8;
import idx2d.physics.Oscillator;

/** Port of the original {@code DropBlenderApplet}. */
public final class DropBlenderDemo extends DemoView {

    private final TextureBlender textureBlender;
    private final GridDistorter distorter;
    private final Oscillator ampOscillator;
    private final Oscillator blendOscillator;
    private final Oscillator wavesOscillator;

    private Grid8x8 grid;
    private Texture output;
    private long startTime;

    public DropBlenderDemo(Params params) {
        super(600, 450);
        textureBlender = new TextureBlender(
                loadTexture(params.getString("texture1", "textures/dropsource.jpg")),
                loadTexture(params.getString("texture2", "textures/dropdest.jpg")));

        grid = new IsoGrid8x8(canvasWidth(), canvasHeight());
        distorter = new FastGridDistorter(canvasWidth(), canvasHeight());

        double interval = params.getDouble("interval", 10000);
        ampOscillator = new Oscillator(0, 0.4, interval);
        wavesOscillator = new Oscillator(9, 4, 0.8 * interval);
        blendOscillator = new Oscillator(0, 255, 2 * interval);
    }

    @Override
    protected void loop() {
        startTime = System.currentTimeMillis();
        while (isRunning()) {
            sinDistort();
            render(output);
        }
    }

    private void sinDistort() {
        long time = System.currentTimeMillis() - startTime;
        double amp = ampOscillator.getValue(time);
        int alpha = (int) blendOscillator.getValue(time);
        double waves = wavesOscillator.getValue(time);

        grid.reset();
        grid = Drop.create(grid, amp, waves, ((double) time) / 300d);

        output = distorter.distort(textureBlender.blend(alpha), grid);
    }
}
