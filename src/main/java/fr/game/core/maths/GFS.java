package fr.game.core.maths;

import fr.game.core.entity.terrain.ChunkCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * | 00 01 02 03 04
 * | 10 11 12 13 14
 * | 20 21 22 23 24
 * | 30 31 32 33 34
 * | 40 41 42 43 44
 * | 50 51 52 53 54
 * | 60 61 62 63 64
 * <p>
 * 00 neighbours : 01, 10, 11
 * 01 neighbours : 00, 02, 10, 11, 12
 * 02 neighbours : 01, 03, 11, 12, 13
 * 03 neighbours : 02, 04, 12, 13, 14
 * 04 neighbours : 03, 13, 14
 * 10 neighbours : 00, 01, 11, 20, 21
 * 11 neighbours : 00, 01, 02, 10, 12, 20, 21, 22
 * 12 neighbours : 01, 02, 03, 11, 13, 21, 22, 23
 * 13 neighbours : 02, 03, 04, 12, 14, 22, 23, 24
 * 14 neighbours : 03, 04, 13, 23, 24
 * 20 neighbours : 10, 11, 21, 30, 31
 * 21 neighbours : 10, 11, 12, 20, 22, 30, 31, 32
 * 22 neighbours : 11, 12, 13, 21, 23, 31, 32, 33
 * 23 neighbours : 12, 13, 14, 22, 24, 32, 33, 34
 * ...
 */
public class GFS {

    private int size;
    private ChunkCoordinates[][] grid;

    public GFS(int size, ChunkCoordinates[][] grid) {
        this.size = size;
        this.grid = grid;
    }


    public void set(int x, int y, ChunkCoordinates value) {
        grid[x][y] = value;
    }

    public ChunkCoordinates get(int x, int y) {
        return grid[x][y];
    }

    public ChunkCoordinates[][] getNeighbours(int x, int y) {
        ChunkCoordinates[][] neighbours = new ChunkCoordinates[3][3];
        int i = 0;
        int j = 0;
        for (int k = x - 1; k <= x + 1; k++) {
            for (int l = y - 1; l <= y + 1; l++) {
                if (k >= 0 && k < size && l >= 0 && l < size) {
                    neighbours[i][j] = grid[k][l];
                } else {
                    neighbours[i][j] = null;
                }
                j++;
            }
            j = 0;
            i++;
        }
        return neighbours;
    }

    public List<ChunkCoordinates> getNeighbours(int x, int y, int radius) {
        List<ChunkCoordinates> neighbours = new ArrayList<>();
        //ChunkCoordinates[][] neighbours = new ChunkCoordinates[radius * 2 + 1][radius * 2 + 1];
        int i = 0;
        int j = 0;
        for (int k = x - radius; k <= x + radius; k++) {
            for (int l = y - radius; l <= y + radius; l++) {
                if (k >= 0 && k < size && l >= 0 && l < size) {
                    //neighbours[i][j] = grid[k][l];
                    neighbours.add(grid[k][l]);
                }
                j++;
            }
            j = 0;
            i++;
        }
        return neighbours;
    }

    public ChunkCoordinates[][] getNeighbours(int x, int y, int radius, ChunkCoordinates[][] grid) {
        ChunkCoordinates[][] neighbours = new ChunkCoordinates[radius * 2 + 1][radius * 2 + 1];
        int i = 0;
        int j = 0;
        for (int k = x - radius; k <= x + radius; k++) {
            for (int l = y - radius; l <= y + radius; l++) {
                if (k >= 0 && k < size && l >= 0 && l < size) {
                    neighbours[i][j] = grid[k][l];
                } else {
                    neighbours[i][j] = null;
                }
                j++;
            }
            j = 0;
            i++;
        }
        return neighbours;
    }

}
