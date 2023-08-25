public class EntranceTile extends Tile {

    /**
     * It's not enough to just have the coordinates of where the tile is...
     * We need to have the coordinates of where we would want the player to be upon
     * taking the exit (the tile adjacent to this), otherwise we would end up with
     * movement inside the actual estate's tiles, which is super messy...
     */
    public Estate estate;
    public int exitY;
    public int exitX;
    public EntranceTile(int yCoord, int xCoord, Estate estate, int exitY, int exitX) {
        super(yCoord, xCoord);
        this.estate = estate;
        this.exitY = exitY;
        this.exitX = exitX;
    }

    public Estate getEstate() {
        return this.estate;
    }

    public int getExitY() {
        return this.exitY;
    }

    public int getExitX() {
        return this.exitX;
    }
}
