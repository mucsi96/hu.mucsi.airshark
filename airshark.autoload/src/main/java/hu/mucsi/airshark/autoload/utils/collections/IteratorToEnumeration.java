package hu.mucsi.airshark.autoload.utils.collections;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorToEnumeration implements Enumeration
{
    private final Iterator iter;

    public IteratorToEnumeration(Iterator iter)
    {
        this.iter = iter;
    }

    public boolean hasMoreElements()
    {
        if (iter == null)
        {
            return false;
        }
        return iter.hasNext();
    }

    public Object nextElement()
    {
        if (iter == null)
        {
            return null;
        }
        return iter.next();
    }
}
