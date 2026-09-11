package idx2d.app;

import idx2d.Texture;
import idx2d.TextureLoader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;
import javax.swing.JPanel;

/**
 * Swing replacement for the applet-era {@code ThreadApplet}. It owns a daemon animation thread whose
 * only job is to compute frames (mutating {@link Texture} pixels) and call {@link #render(Texture)};
 * painting always happens on the EDT via {@link #paintComponent(Graphics)}.
 *
 * <p>Subclasses implement {@link #init()} (resource setup, run on the animation thread) and
 * {@link #loop()} (one iteration of the effect). Mouse/keyboard hooks are provided so the ported
 * effects keep their original interactions.
 */
public abstract class DemoView extends JPanel implements Runnable {

    private final int canvasWidth;
    private final int canvasHeight;
    private volatile Texture frame;
    private volatile boolean running;
    private Thread thread;
    private long startNanos;
    private Consumer<String> statusSink = text -> {
    };

    protected DemoView(Dimension size) {
        this(size.width, size.height);
    }

    protected DemoView(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        setOpaque(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setFocusable(true);
        installInputHooks();
    }

    protected final int canvasWidth() {
        return canvasWidth;
    }

    protected final int canvasHeight() {
        return canvasHeight;
    }

    public final void setStatusSink(Consumer<String> statusSink) {
        this.statusSink = statusSink;
    }

    protected final void setStatus(String text) {
        statusSink.accept(text);
    }

    protected final Texture loadTexture(String resourcePath) {
        return TextureLoader.load(resourcePath);
    }

    /**
     * Milliseconds elapsed since this demo started, read from a monotonic clock
     * ({@link System#nanoTime()}). Using real elapsed time instead of a fixed per-frame step keeps the
     * animation speed independent of the frame rate.
     */
    protected final long elapsedMillis() {
        return (System.nanoTime() - startNanos) / 1_000_000L;
    }

    /** Resource setup. Runs once, on the animation thread, before {@link #loop()}. */
    protected void init() {
    }

    /** One iteration of the effect. Check {@link #isRunning()} for long-running loops. */
    protected abstract void loop();

    public final void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, getClass().getSimpleName() + "-animation");
        thread.setDaemon(true);
        thread.start();
    }

    public final void stop() {
        running = false;
        Thread current = thread;
        thread = null;
        if (current != null) {
            current.interrupt();
        }
    }

    public final boolean isRunning() {
        return running;
    }

    protected final void render(Texture texture) {
        this.frame = texture;
        repaint();
    }

    protected final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }

    @Override
    public final void run() {
        startNanos = System.nanoTime();
        try {
            init();
            loop();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            running = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Texture current = frame;
        if (current != null) {
            g.drawImage(current.getImage(), 0, 0, null);
        }
    }

    private void installInputHooks() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                onMousePressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseReleased(e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                onMouseEntered();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                onMouseExited();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                onMouseMoved(e.getX(), e.getY());
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e.getX(), e.getY());
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e.getKeyCode());
            }
        });
    }

    protected void onMousePressed(int x, int y) {
    }

    protected void onMouseReleased(int x, int y) {
    }

    protected void onMouseMoved(int x, int y) {
    }

    protected void onMouseDragged(int x, int y) {
    }

    protected void onMouseEntered() {
    }

    protected void onMouseExited() {
    }

    protected void onKeyPressed(int keyCode) {
    }
}
