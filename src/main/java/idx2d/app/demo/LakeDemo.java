package idx2d.app.demo;

import idx2d.Color24;
import idx2d.Texture;
import idx2d.TextureLoader;
import idx2d.app.DemoView;
import idx2d.app.Params;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/** Port of the original {@code LakeApplet}: an image above its animated water reflection. */
public final class LakeDemo extends DemoView {

    private final Texture source;
    private final Texture output;
    private final double wavesize;
    private final double periodTime;
    private final double waves;

    public LakeDemo(Params params) {
        super(canvasSize(params));
        source = loadTexture(params.getString("image", "textures/lakegirl.jpg"));
        double maxamp = params.getDouble("maxamp", 12);
        wavesize = maxamp / (canvasHeight() / 2.0);
        waves = params.getDouble("waves", 12);
        periodTime = params.getDouble("T", 800);
        output = new Texture(source.width, source.height);
    }

    private static Dimension canvasSize(Params params) {
        BufferedImage image = TextureLoader.loadImage(params.getString("image", "textures/lakegirl.jpg"));
        return new Dimension(image.getWidth(), image.getHeight() * 2);
    }

    @Override
    protected void loop() {
        while (isRunning()) {
            createLake();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(source.getImage(), 0, 0, null);
        g.drawImage(output.getImage(), 0, source.height, null);
    }

    private void createLake() {
        int y2;
        double omega = 2d * Math.PI / periodTime;
        double time = System.currentTimeMillis();
        int width = source.width;
        int height = source.height;
        int sourceOffset, destOffset;

        for (int y = 0; y < height; y++) {
            y2 = (height - 1 - y)
                    + (int) ((double) y * wavesize * Math.sin(omega * time + waves * (double) (height - y) / (double) y));
            y2 = Math.max(y2, 0);
            y2 = Math.min(y2, height - 1);
            sourceOffset = y2 * width;
            destOffset = y * width;

            for (int x = 0; x < width; x++) {
                output.pixel[x + destOffset] = Color24.mix(output.pixel[x + destOffset], source.pixel[x + sourceOffset]);
            }
        }
    }
}
