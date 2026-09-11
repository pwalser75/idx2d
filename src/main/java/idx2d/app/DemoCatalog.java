package idx2d.app;

import idx2d.app.demo.Distorter2Demo;
import idx2d.app.demo.DistorterDemo;
import idx2d.app.demo.DropBlenderDemo;
import idx2d.app.demo.DropDemo;
import idx2d.app.demo.FeedbackDemo;
import idx2d.app.demo.LakeDemo;
import idx2d.app.demo.LiquidDemo;
import idx2d.app.demo.RotoZoomerDemo;
import idx2d.app.demo.SinDistorterDemo;
import idx2d.app.demo.TunnelDemo;
import java.util.List;

/** The ordered catalog of demos shown in the sidebar. The first entry is selected on startup. */
public final class DemoCatalog {

    private DemoCatalog() {
    }

    public static List<Demo> all() {
        return List.of(
                new Demo("Distorter",
                        "A warping sine-field that folds a texture through a rotating displacement grid.",
                        DistorterDemo::new),
                new Demo("Distorter 2",
                        "A radial twirl over a Gaussian-blurred source, with an oscillating focal point.",
                        Distorter2Demo::new),
                new Demo("Drop",
                        "Concentric ripples spreading across a reflective surface.",
                        () -> new DropDemo(Params.of(
                                "texture", "textures/car.jpg",
                                "bilinear", "true",
                                "interval", "8000",
                                "minwave", "3",
                                "maxwave", "12",
                                "minamp", "0.02",
                                "maxamp", "0.12"))),
                new Demo("DropBlender",
                        "Two textures cross-faded through animated water ripples.",
                        () -> new DropBlenderDemo(Params.of(
                                "interval", "14400",
                                "texture1", "textures/dropsource.jpg",
                                "texture2", "textures/dropdest.jpg"))),
                new Demo("Feedback",
                        "A recursive zoom / rotate / blur feedback loop that never settles.",
                        FeedbackDemo::new),
                new Demo("Lake",
                        "A photograph sitting above its own animated water reflection.",
                        () -> new LakeDemo(Params.of(
                                "image", "textures/lakegirl.jpg",
                                "maxamp", "12",
                                "waves", "12",
                                "T", "800"))),
                new Demo("Liquid",
                        "Droplets falling onto a chrome-mapped liquid surface (move the mouse to interact).",
                        () -> new LiquidDemo(Params.of(
                                "texture", "textures/horny.jpg",
                                "envmap", "textures/chrome.jpg",
                                "lightmap", "textures/lightmap.jpg",
                                "pressure", "0.4",
                                "dropsize", "1.44",
                                "speedfactor", "1"))),
                new Demo("RotoZoomer",
                        "Endless rotation and zoom while sampling a texture.",
                        () -> new RotoZoomerDemo(Params.of(
                                "texture", "textures/horny.jpg",
                                "minZoom", "0.5",
                                "maxZoom", "4",
                                "angle", "100"))),
                new Demo("SinDistorter",
                        "A sinusoidal shear distortion swept by a rotating field.",
                        () -> new SinDistorterDemo(Params.of(
                                "texture", "textures/escape.jpg"))),
                new Demo("Tunnel",
                        "A classic perspective texture tunnel. Click the canvas for hyperspeed.",
                        () -> new TunnelDemo(Params.of(
                                "texture", "textures/tunnel.jpg"))));
    }
}
