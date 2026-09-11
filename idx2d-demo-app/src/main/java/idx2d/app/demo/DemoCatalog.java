package idx2d.app.demo;

import idx2d.app.Demo;
import idx2d.app.DemoProvider;
import idx2d.app.Params;
import java.util.List;

/**
 * The built-in demo catalog. It is discovered by the application through the {@link DemoProvider}
 * service interface, so the app shell has no compile-time dependency on the demos.
 */
public final class DemoCatalog implements DemoProvider {

    @Override
    public List<Demo> demos() {
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
                        "Droplets falling onto a chrome-mapped liquid surface (drag the mouse to interact, click to add drops).",
                        () -> new LiquidDemo(Params.of(
                                "texture", "textures/horny.jpg",
                                "envmap", "textures/chrome.jpg",
                                "lightmap", "textures/lightmap.jpg",
                                "pressure", "0.8",
                                "dropsize", "2",
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
