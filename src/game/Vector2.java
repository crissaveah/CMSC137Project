
package game;

import util.Poolable;

/**
 * A 2-component vector.
 * <p>
 * @author John Paul Quijano
 */
public final class Vector2 implements Cloneable, Poolable
{
    public static final float EPSILON = 1.1920928955078125E-7f;
    public static final Vector2 ZERO = new Vector2(0f, 0f);
    public static final Vector2 ONE = new Vector2(1f, 1f);
    public static final Vector2 NEG_ONE = new Vector2(-1f, -1f);
    public static final Vector2 UNIT_X = new Vector2(1f, 0f);
    public static final Vector2 NEG_UNIT_X = new Vector2(-1f, 0f);
    public static final Vector2 UNIT_Y = new Vector2(0f, 1f);
    public static final Vector2 NEG_UNIT_Y = new Vector2(0f, -1f);

    private double x;
    private double y;

    public Vector2()
    {
        x = 0;
        y = 0;
    }

    public Vector2(Vector2 template)
    {
        x = template.x;
        y = template.y;
    }

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public Vector2 set(double x, double y)
    {
        this.x = x;
        this.y = y;
        
        return this;
    }
    
    public Vector2 set(Vector2 source)
    {
        x = source.x;
        y = source.y;
        
        return this;
    }

    public Vector2 add(double x, double y, Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(this.x + x, this.y + y);
    }

    public Vector2 add(Vector2 source, Vector2 output)
    {
        return add(source.x, source.y, output);
    }
    
    public Vector2 add(double x, double y)
    {
        return set(this.x + x, this.y + y);
    }

    public Vector2 add(Vector2 source)
    {
        return add(source.x, source.y);
    }

    public Vector2 subtract(double x, double y, Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(this.x - x, this.y - y);
    }

    public Vector2 subtract(Vector2 source, Vector2 output)
    {
        return subtract(source.x, source.y, output);
    }
    
    public Vector2 subtract(double x, double y)
    {
        return set(this.x - x, this.y - y);
    }

    public Vector2 subtract(Vector2 source)
    {
        return subtract(source.x, source.y);
    }

    public Vector2 multiply(double scalar, Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(x * scalar, y * scalar);
    }

    public Vector2 multiply(double scalar)
    {
        return set(x * scalar, y * scalar);
    }

    public Vector2 multiply(Vector2 scale, Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(x * scale.x, y * scale.y);
    }

    public Vector2 multiply(Vector2 scale)
    {
        return set(x * scale.x, y * scale.y);
    }

    public Vector2 divide(double scalar, Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(x / scalar, y / scalar);
    }

    public Vector2 divide(double scalar)
    {
        double invScalar = 1f / scalar;
        return set(x * invScalar, y * invScalar);
    }

    public Vector2 divide(Vector2 scale, Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(x / scale.x, y / scale.y);
    }

    public Vector2 divide(Vector2 scale)
    {
        return set(x / scale.x, y / scale.y);
    }
    
    public Vector2 divide(double x, double y)
    {
        return set(this.x / x, this.y / y);
    }
    
    public Vector2 negate(Vector2 output)
    {
        if(output == null)
            output = new Vector2();

        return output.set(-x, -y);
    }
    
    public Vector2 negate()
    {
        return set(-x, -y);
    }

    public Vector2 normalize(Vector2 output)
    {
        double squaredLength = squaredLength();

        if(Math.abs(squaredLength) > EPSILON)
            return multiply(1d/Math.sqrt(squaredLength), output);

        return output != null ? output.set(ZERO) : new Vector2();
    }

    public Vector2 normalize()
    {
        double lengthSq = squaredLength();

        if(Math.abs(lengthSq) > EPSILON)
            return multiply(1d/Math.sqrt(lengthSq));

        return this;
    }
    
    public double angle()
    {
        return Math.atan2(x, y);
    }

    public double length()
    {
        return Math.sqrt(squaredLength());
    }

    public double squaredLength()
    {
        return x * x + y * y;
    }

    public double squaredDistance(double x, double y)
    {
        double dx = this.x - x;
        double dy = this.y - y;

        return dx * dx + dy * dy;
    }

    public double squaredDistance(Vector2 destination)
    {
        return squaredDistance(destination.x, destination.y);
    }

    public double distance(double x, double y)
    {
        return Math.sqrt(squaredDistance(x, y));
    }

    public double distance(Vector2 destination)
    {
        return Math.sqrt(squaredDistance(destination));
    }

    public double dot(double x, double y)
    {
        return this.x * x + this.y * y;
    }

    public double dot(Vector2 vec)
    {
        return dot(vec.x, vec.y);
    }
    
    public Vector2 absolute()
    {
        return set(Math.abs(x), Math.abs(y));
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "(" + x + ", " + y + ")";
    }

    public boolean equals(Vector2 vec)
    {
        return vec.x == x && vec.y == y;
    }
    
    @Override
    public Vector2 clone()
    {
        return new Vector2().set(this);
    }
}
