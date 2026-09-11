package idx2d.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Grid8x8Test {

    @Test
    void computesGridDimensions() {
        Grid8x8 grid = new Grid8x8(64, 32);
        assertEquals(9, grid.width);
        assertEquals(5, grid.height);
        assertEquals(8, grid.gridwidth);
        assertEquals(4, grid.gridheight);
    }

    @Test
    void resetInitialisesTextureCoordinates() {
        Grid8x8 grid = new Grid8x8(64, 64);
        grid.reset();
        assertEquals(0.0, grid.node[0][0].u, 1e-9);
        assertEquals(0.0, grid.node[0][0].v, 1e-9);
        assertEquals(255, grid.node[0][0].intensity);
    }

    @Test
    void isoGridSpansZeroToOne() {
        IsoGrid8x8 grid = new IsoGrid8x8(64, 64);
        grid.reset();
        assertEquals(0.0, grid.node[0][0].u, 1e-9);
        assertEquals(1.0, grid.node[grid.gridwidth][grid.gridheight].u, 1e-9);
    }
}
