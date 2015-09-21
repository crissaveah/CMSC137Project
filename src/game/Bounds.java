
package game;

public class Bounds 
{
    private double extentX;
    private double extentY;
    private Vector2 location;
    
    public Bounds()
    {
        location = new Vector2();
    }
    
    public void setExtent(double x, double y)
    {
        extentX = x;
        extentY = y;
    }
    
    public double getExtentX()
    {
        return extentX;
    }
    
    public double getExtentY()
    {
        return extentY;
    }
    
    public void setLocation(double x, double y)
    {
        location.set(x, y);
    }
    
    public void setLocation(Vector2 loc)
    {
        location.set(loc);
    }
    
    public Vector2 getLocation()
    {
        return location;
    }
    
    public boolean intersects(Bounds bounds)
    {
        double dx = Math.abs(location.getX() - bounds.location.getX());
        double dy = Math.abs(location.getY() - bounds.location.getY());
        
        return dx < extentX + bounds.extentX && dy < extentY + bounds.extentY;
    }
}
