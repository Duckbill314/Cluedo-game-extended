public class Item
{
  private String name;
  private String displayIcon;
  private int x;
  private int y;
  private Estate estate; // for the sake of teleporting objects, it's easiest to just have an estate in the Item superclass

  public Item(String aName, String aDisplayIcon, int aX, int aY)
  {
    name = aName;
    displayIcon = aDisplayIcon;
    x = aX;
    y = aY;
    estate = null;
  }

  public boolean setX(int aX)
  {
    boolean wasSet = false;
    x = aX;
    wasSet = true;
    return wasSet;
  }

  public boolean setY(int aY)
  {
    boolean wasSet = false;
    y = aY;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getDisplayIcon()
  {
    return displayIcon;
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public boolean setEstate(Estate aEstate)
  {
    boolean wasSet = false;
    estate = aEstate;
    wasSet = true;
    return wasSet;
  }

  public Estate getEstate()
  {
    return estate;
  }

  public String toString()
  {
    String res = String.format("[name: %s | display icon: %s | x: %d | y: %d]", getName(), getDisplayIcon(), getX(), getY());
    return res;
  }
}