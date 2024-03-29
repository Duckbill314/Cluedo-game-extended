import java.util.*;

/**
 * The Board class represents the game board for Hobby Detective.
 * It contains methods for initializing the board, creating estates,
 * managing tiles and estates, and checking the safety of moves.
 *
 * @author Mazen Khallaf
 * @author William Huang
 * @author James Goode
 * @author Finley Neilson
 */
public class Board {
    /**
     * The number of rows on the game board
     */
    private static final int ROWS = 24;

    /**
     * The number of columns on the game board
     */
    private static final int COLS = 24;

    /**
     * The border character used for visual representation of the game board.
     */
    private String BORDER = "|";

    /**
     * The two-dimensional array representing the game board with tiles.
     */
    private Tile[][] board = new Tile[ROWS][COLS];

    /**
     * A list of estates present on the game board.
     */
    private List<Estate> estates = new ArrayList<Estate>();

    /**
     * Constructor for the Board class. Initializes the game board and estates.
     */
    public Board() {
        buildGameSpace();

        // Create and initialize estates
        initializeEstates();
        // Build estate structures on the board
        buildEstateStructures();

        // Build entrances and grey areas
        buildEntrances();
        buildGreyAreas();
    }

    /**
     * Initializes the game board by filling it with GameTile objects.
     */
    private void buildGameSpace() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = new GameTile(i, j);
            }
        }
    }

    /**
     * Initializes the list of estates.
     */
    private void initializeEstates() {
        estates.add(new Estate("Haunted House"));
        estates.add(new Estate("Calamity Castle"));
        estates.add(new Estate("Manic Manor"));
        estates.add(new Estate("Peril Palace"));
        estates.add(new Estate("Visitation Villa"));
    }

    /**
     * Builds estate structures on the board.
     */
    private void buildEstateStructures() {
        buildSquareEstate(2, 2, estates.get(0));
        buildSquareEstate(17, 2, estates.get(1));
        buildSquareEstate(2, 17, estates.get(2));
        buildSquareEstate(17, 17, estates.get(3));
        buildRectangleEstate(10, 9, estates.get(4));
    }

    /**
     * Builds a square-shaped estate on the board starting at the specified coordinates.
     * This method also adds the inside tiles to the estate.
     *
     * @param y      The starting row index.
     * @param x      The starting column index.
     * @param estate The Estate object to which the tiles belong.
     */
    private void buildSquareEstate(int y, int x, Estate estate) {
        //build column
        for (int i = y; i < y + 5; i++) {
            int j = x;
            board[i][j] = new WallTile(i, j);
            j = x + 4;
            board[i][j] = new WallTile(i, j);
        }

        //build row
        for (int j = x + 1; j < x + 4; j++) {
            int i = y;
            board[i][j] = new WallTile(i, j);
            i = y + 4;
            board[i][j] = new WallTile(i, j);
        }

        //add inside tiles to the estate
        for (int i = y + 1; i < y + 4; i++) {
            for (int j = x + 1; j < x + 4; j++) {
                estate.addEstateTile((GameTile)board[i][j]);
            }
        }
    }

    /**
     * Builds a rectangle-shaped estate on the board starting at the specified coordinates.
     * This method also adds the inside tiles to the estate.
     *
     * @param y      The starting row index.
     * @param x      The starting column index.
     * @param estate The Estate object to which the tiles belong.
     */
    private void buildRectangleEstate(int y, int x, Estate estate) {
        //build column
        for (int i = y; i < y + 4; i++) {
            int j = x;
            board[i][j] = new WallTile(i, j);
            j = x + 5;
            board[i][j] = new WallTile(i, j);
        }

        //build row
        for (int j = x + 1; j < x + 5; j++) {
            int i = y;
            board[i][j] = new WallTile(i, j);
            i = y + 3;
            board[i][j] = new WallTile(i, j);
        }

        //add inside tiles to the estate
        for (int i = y + 1; i < y + 3; i++) {
            for (int j = x + 1; j < x + 5; j++) {
                estate.addEstateTile((GameTile)board[i][j]);
            }
        }
    }

    /**
     * Builds grey areas on the board.
     */
    private void buildGreyAreas() {
        buildGreyArea(5, 11);
        buildGreyArea(11, 5);
        buildGreyArea(11, 17);
        buildGreyArea(17, 11);
    }

    /**
     * This may get a bit confusing but here's the idea:
     * - Firstly, create the entranceTile objects and add them to the board
     * - Secondly, add them to their corresponding estates
     * - Add them in such a way that the list of entranceTiles inside the Estate object is in order of North, East, South, West
     * - For the directions that don't have entrances, add the closest entrance
     */
    private void buildEntrances() {
        // Haunted House
        Estate estate = estates.get(0);
        EntranceTile entrance1 = new EntranceTile(3, 6, estate, 3, 7);
        EntranceTile entrance2 = new EntranceTile(6, 5, estate, 7, 5);
        board[3][6] = entrance1;
        board[6][5] = entrance2;
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance2);

        // Calamity Castle
        estate = estates.get(1);
        entrance1 = new EntranceTile(17, 3, estate, 16, 3);
        entrance2 = new EntranceTile(18, 6, estate, 18, 7);
        board[17][3] = entrance1;
        board[18][6] = entrance2;
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance1);

        // Manic Manor
        estate = estates.get(2);
        entrance1 = new EntranceTile(5, 17, estate, 5, 16);
        entrance2 = new EntranceTile(6, 20, estate, 7, 20);
        board[5][17] = entrance1;
        board[6][20] = entrance2;
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance1);

        // Peril Palace
        estate = estates.get(3);
        entrance1 = new EntranceTile(17, 18, estate, 16, 18);
        entrance2 = new EntranceTile(20, 17, estate, 20, 16);
        board[17][18] = entrance1;
        board[20][17] = entrance2;
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance2);

        // Visitation Villa
        estate = estates.get(4);
        entrance1 = new EntranceTile(10, 12, estate, 9, 12);
        entrance2 = new EntranceTile(11, 14, estate, 11, 15);
        EntranceTile entrance3 = new EntranceTile(13, 11, estate, 14, 11);
        EntranceTile entrance4 = new EntranceTile(12, 9, estate, 12, 8);
        board[10][12] = entrance1;
        board[11][14] = entrance2;
        board[13][11] = entrance3;
        board[12][9] = entrance4;
        estate.addEntrance(entrance1);
        estate.addEntrance(entrance2);
        estate.addEntrance(entrance3);
        estate.addEntrance(entrance4);
    }

    /**
     * Builds a grey area on the board starting at the specified coordinates.
     *
     * @param y The starting row index.
     * @param x The starting column index.
     */
    private void buildGreyArea(int y, int x) {
        for (int i = y; i <= y + 1; i++) {
            for (int j = x; j <= x + 1; j++) {
                board[i][j] = new WallTile(i, j);
            }
        }
    }

    /**
     * Checks if the provided coordinates are within the valid bounds of the game board.
     *
     * @param y The row index.
     * @param x The column index.
     * @return true if the coordinates are valid; otherwise, false.
     */    private boolean isValidPosition(int y, int x) {
        return y >= 0 && y < ROWS && x >= 0 && x < COLS;
    }

    /**
     * Returns the tile at the specified position.
     *
     * @param y     The row index.
     * @param x     The column index.
     * @return      The tile at the specified position.
     */
    public Tile getTile(int y, int x) {
        if (isValidPosition(y, x)) {
            return board[y][x];
        } else {
            return null; // Temporary solution for now
        }
    }

    /**
     * Checks if a move to the specified position is safe (i.e. not occupied by a wall, gray-space, or a player).
     *
     * @param y The row index.
     * @param x The column index.
     * @return  True if the move is safe; otherwise, false.
     */
    public boolean isSafeMove(int y, int x) {
        // Checks if the provided coordinates are within the bounds of the board
        if (!isValidPosition(y, x)) {
            return false;
        }

        Tile target = getTile(y, x);

        // Tile is considered safe if it is an EntranceceTile
        if (target instanceof EntranceTile) {
            return true;
        }

        // If the tile is not a GameTile, it is not safe
        if (!(target instanceof GameTile)) {
            return false;
        }

        // If the tile is considered 'empty', it is safe
        return ((GameTile) target).getStored().getName().equals("empty");
    }

    /**
     * Returns a list of estates on the board.
     *
     * @return A list of Estate objects representing the estates on the board.
     */
    public List<Estate> getEstates() {
        return estates;
    }
}
