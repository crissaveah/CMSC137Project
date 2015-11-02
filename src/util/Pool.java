package util;

import java.util.ArrayDeque;

/**
 * Utility class for object pooling.
 * <p>
 * @author John Paul Quijano
 */
public abstract class Pool<T extends Poolable>
{
    private int maxSize;
    
    private ThreadLocal<ArrayDeque<T>> pool = new ThreadLocal<ArrayDeque<T>>()
    {
        @Override
        protected ArrayDeque<T> initialValue()
        {
            ArrayDeque<T> objects = new ArrayDeque<>(maxSize);
            
            for(int i = 0; i < maxSize; i++)
                objects.push(newInstance());
            
            return objects;
        }
    };

    protected Pool(int maxSize)
    {
        this.maxSize = maxSize;
    }
    
    public int getSize()
    {
        return maxSize;
    }

    public T fetch()
    {
        ArrayDeque<T> objects = pool.get();
        return objects.pop();
    }

    public void release(T object)
    {
        ArrayDeque<T> objects = pool.get();
        objects.push(object);
    }
    
    protected abstract T newInstance();

    public static <T extends Poolable> Pool<T> create(Class<T> poolable, int maxSize)
    {
        return new Pool<T>(maxSize)
        {
            @Override
            protected T newInstance()
            {
                try
                {
                    return poolable.newInstance();
                }
                catch(InstantiationException | IllegalAccessException e)
                {
                    return null;
                }
            }
        };
    }
}
