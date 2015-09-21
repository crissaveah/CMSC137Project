
package util;

import game.Vector2;

/**
 * @author John Paul Quijano
 */
public final class EnginePool
{
    private EnginePool(){}
    
    public static final int MAX_POOL_SIZE = 10;
    public static final Pool<Vector2> Vector2 = Pool.create(Vector2.class, MAX_POOL_SIZE);
}
