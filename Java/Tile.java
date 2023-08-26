public class Tile {

    public int xCoord;
    public int yCoord;
    public String output;

    public Tile(int yCoord, int xCoord) {
        this.yCoord = yCoord;
        this.xCoord = xCoord;
        this.output = "";
    }

    public int getYCoord() {
        return this.xCoord;
    }

    public int getXCoord() {
        return this.yCoord;
    }

    public String getOutput() {
        return this.output;
    }

    public String draw() {
        return (getOutput());
    }
}
