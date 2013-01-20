package hu.mucsi.airshark.autoload.utils.collections;


import java.util.*;


/**
 * This is a simple class that implements a <tt>Dictionary</tt>
 * from a <tt>Map</tt>. The resulting dictionary is immutatable.
**/
public class MapToDictionary extends Dictionary
{
    /**
     * Map source.
    **/
    private Map map = null;

    public MapToDictionary(Map map)
    {
        this.map = map;
    }

    public void setSourceMap(Map map)
    {
        this.map = map;
    }

    public Enumeration elements()
    {
        if (map == null)
        {
            return null;
        }
        return new IteratorToEnumeration(map.values().iterator());
    }

    public Object get(Object key)
    {
        if (map == null)
        {
            return null;
        }
        return map.get(key);
    }

    public boolean isEmpty()
    {
        if (map == null)
        {
            return true;
        }
        return map.isEmpty();
    }

    public Enumeration keys()
    {
        if (map == null)
        {
            return null;
        }
        return new IteratorToEnumeration(map.keySet().iterator());
    }

    public Object put(Object key, Object value)
    {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key)
    {
        throw new UnsupportedOperationException();
    }

    public int size()
    {
        if (map == null)
        {
            return 0;
        }
        return map.size();
    }
}