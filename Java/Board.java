import java.util.*;

public class Board {
    private final int ROWS = 24;
    private final int COLS = 24;
    private String BORDER = "|";

    Tile[][] board = new Tile[ROWS][COLS];
    private List<Estate> estates = new ArrayList<Estate>();

    public Board() {
        buildGameSpace();

        estates.add(buildEstate("Haunted House"));
        estates.add(buildEstate("Calamity Castle"));
        estates.add(buildEstate("Manic Manor"));
        estates.add(buildEstate("Peril Palace"));
        estates.add(buildEstate("Visitation Villa"));

        buildSquareEstate(2, 2, estates.get(0));
        buildSquareEstate(17, 2, estates.get(1));
        buildSquareEstate(2, 17, estates.get(2));
        buildSquareEstate(17, 17, estates.get(3));

        buildRectangleEstate(10, 9, estates.get(4));

        buildEntrances();

        buildGreyArea(5, 11);
        buildGreyArea(11, 5);
        buildGreyArea(11, 17);
        buildGreyArea(17, 11);

    }

    private void buildGameSpace() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = new GameTile(i, j);
            }
        }
    }

    private Estate buildEstate(String name) {
        return new Estate(name);
    }

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

    private void buildGreyArea(int y, int x) {
        for (int i = y; i <= y + 1; i++) {
            for (int j = x; j <= x + 1; j++) {
                board[i][j] = new WallTile(i, j);
            }
        }
    }

    /**
     * Draws the board
     */
    public void draw() {
        // draws the Board including the players and items
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                System.out.print(BORDER);
                board[row][col].draw();
            }
            System.out.println(BORDER);
        }
    }

    /**
     * Sets tile at pos x,y
     */
    public void setTile(int y, int x, Tile tile) {
        board[y][x] = tile;
    }

    /**
     * Returns tile at pos x,y
     */
    public Tile getTile(int y, int x) {
        return board[y][x];
    }

    public void deleteBoard() {
        board = new Tile[ROWS][COLS];
    }

    /**
     * Turns every border on the board invisible so players can get a better grasp of their respective positions.
     */
    public void gridOff() {
        BORDER = " ";
    }

    /**
     * Turns every border on the board back on
     */
    public void gridOn() {
        if (BORDER.equals("|")) {
            gridOff();
        } else {
            BORDER = "|";
        }
    }

    /**
     * makes sure that the value in board at x and y is not occupied by a wall or grayspace or player
     */
    public boolean isSafeMove(int y, int x) {
        if (x > 23 || x < 0 || y > 23 || y < 0) {
            return false;
        }

        Tile target = getTile(y, x);
        
        if (target instanceof EntranceTile) {
            return true;
        }

        if (!(target instanceof GameTile)) {
            return false;
        }

        if (((GameTile)target).getStored() != null) {
            return false;
        }
        
        return true;
    }

    public List<Estate> getEstates() {
        return estates;
    }
}
