package hu.mucsi.airshark.autoload.internal;

import hu.mucsi.airshark.autoload.ArtifactInstaller;
import hu.mucsi.airshark.autoload.ArtifactListener;
import hu.mucsi.airshark.autoload.ArtifactTransformer;
import hu.mucsi.airshark.autoload.ArtifactUrlTransformer;
import hu.mucsi.airshark.autoload.utils.collections.DictionaryAsMap;
import hu.mucsi.airshark.autoload.utils.properties.InterpolationHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@SuppressWarnings({"unchecked","rawtypes","deprecation"})
public class FileInstall implements BundleActivator, ServiceTrackerCustomizer, FrameworkListener
{
    static ServiceTracker padmin;
    static ServiceTracker startLevel;
    static Runnable cmSupport;
    static final Map /* <ServiceReference, ArtifactListener> */ listeners = new TreeMap /* <ServiceReference, ArtifactListener> */();
    static final BundleTransformer bundleTransformer = new BundleTransformer();
    BundleContext context;
    Map watchers = new HashMap();
    ServiceTracker listenersTracker;
    static boolean initialized;
    static final Object barrier = new Object();
    static final Object refreshLock = new Object();

    public void start(BundleContext context) throws Exception
    {
        this.context = context;
        context.addFrameworkListener(this);

        Hashtable props = new Hashtable();
        props.put("url.handler.protocol", JarDirUrlHandler.PROTOCOL);
        context.registerService(org.osgi.service.url.URLStreamHandlerService.class.getName(), new JarDirUrlHandler(), props);

        padmin = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        padmin.open();
        startLevel = new ServiceTracker(context, StartLevel.class.getName(), null);
        startLevel.open();

        String flt = "(|(" + Constants.OBJECTCLASS + "=" + ArtifactInstaller.class.getName() + ")"
                     + "(" + Constants.OBJECTCLASS + "=" + ArtifactTransformer.class.getName() + ")"
                     + "(" + Constants.OBJECTCLASS + "=" + ArtifactUrlTransformer.class.getName() + "))";
        listenersTracker = new ServiceTracker(context, FrameworkUtil.createFilter(flt), this);
        listenersTracker.open();

        // Created the initial configuration
        Hashtable ht = new Hashtable();

        set(ht, DirectoryWatcher.POLL);
        set(ht, DirectoryWatcher.DIR);
        set(ht, DirectoryWatcher.LOG_LEVEL);
        set(ht, DirectoryWatcher.FILTER);
        set(ht, DirectoryWatcher.TMPDIR);
        set(ht, DirectoryWatcher.START_NEW_BUNDLES);
        set(ht, DirectoryWatcher.USE_START_TRANSIENT);
        set(ht, DirectoryWatcher.NO_INITIAL_DELAY);
        set(ht, DirectoryWatcher.START_LEVEL);

        // check if dir is an array of dirs
        String dirs = (String)ht.get(DirectoryWatcher.DIR);
        if ( dirs != null && dirs.indexOf(',') != -1 )
        {
            StringTokenizer st = new StringTokenizer(dirs, ",");
            int index = 0;
            while ( st.hasMoreTokens() )
            {
                final String dir = st.nextToken().trim();
                ht.put(DirectoryWatcher.DIR, dir);

                String name = "initial";
                if ( index > 0 ) name = name + index;
                updated(name, new Hashtable(ht));

                index++;
            }
        }
        else
        {
            updated("initial", ht);
        }
        // now notify all the directory watchers to proceed
        // We need this to avoid race conditions observed in FELIX-2791
        synchronized (barrier) {
            initialized = true;
            barrier.notifyAll();
        }
    }

    public Object addingService(ServiceReference serviceReference)
    {
        ArtifactListener listener = (ArtifactListener) context.getService(serviceReference);
        addListener(serviceReference, listener);
        return listener;
    }
    public void modifiedService(ServiceReference reference, Object service)
    {
        removeListener(reference, (ArtifactListener) service);
        addListener(reference, (ArtifactListener) service);
    }
    public void removedService(ServiceReference serviceReference, Object service)
    {
        removeListener(serviceReference, (ArtifactListener) service);
    }

    // Adapted for FELIX-524
    private void set(Hashtable ht, String key)
    {
        Object o = context.getProperty(key);
        if (o == null)
        {
           o = System.getProperty(key.toUpperCase().replace('.', '_'));
            if (o == null)
            {
                return;
            }
        }
        ht.put(key, o);
    }

    public void stop(BundleContext context) throws Exception
    {
        synchronized (barrier) {
            initialized = false;
        }
        List /*<DirectoryWatcher>*/ toClose = new ArrayList /*<DirectoryWatcher>*/();
        synchronized (watchers)
        {
            toClose.addAll(watchers.values());
            watchers.clear();
        }
        for (Iterator w = toClose.iterator(); w.hasNext();)
        {
            try
            {
                DirectoryWatcher dir = (DirectoryWatcher) w.next();
                dir.close();
            }
            catch (Exception e)
            {
                // Ignore
            }
        }
        if (listenersTracker != null)
        {
            listenersTracker.close();
        }
        if (cmSupport != null)
        {
            cmSupport.run();
        }
        if (padmin != null)
        {
            padmin.close();
        }
    }

    public void deleted(String pid)
    {
        DirectoryWatcher watcher;
        synchronized (watchers)
        {
            watcher = (DirectoryWatcher) watchers.remove(pid);
        }
        if (watcher != null)
        {
            watcher.close();
        }
    }

    public void updated(String pid, Dictionary properties)
    {
        InterpolationHelper.performSubstitution(new DictionaryAsMap(properties), context);
        DirectoryWatcher watcher = null;
        synchronized (watchers)
        {
            watcher = (DirectoryWatcher) watchers.get(pid);
            if (watcher != null && watcher.getProperties().equals(properties))
            {
                return;
            }
        }
        if (watcher != null)
        {
            watcher.close();
        }
        watcher = new DirectoryWatcher(properties, context);
        watcher.setDaemon(true);
        synchronized (watchers)
        {
            watchers.put(pid, watcher);
        }
        watcher.start();
    }

    public void updateChecksum(File file)
    {
        List /*<DirectoryWatcher>*/ toUpdate = new ArrayList /*<DirectoryWatcher>*/();
        synchronized (watchers)
        {
            toUpdate.addAll(watchers.values());
        }
        for (Iterator w = toUpdate.iterator(); w.hasNext();)
        {
            DirectoryWatcher watcher = (DirectoryWatcher) w.next();
            watcher.scanner.updateChecksum(file);
        }
    }

    private void addListener(ServiceReference reference, ArtifactListener listener)
    {
        synchronized (listeners)
        {
            listeners.put(reference, listener);
        }
        
        long currentStamp = reference.getBundle().getLastModified();

        List /*<DirectoryWatcher>*/ toNotify = new ArrayList /*<DirectoryWatcher>*/();
        synchronized (watchers)
        {
            toNotify.addAll(watchers.values());
        }
        for (Iterator w = toNotify.iterator(); w.hasNext();)
        {
            DirectoryWatcher dir = (DirectoryWatcher) w.next();
            dir.addListener( listener, currentStamp );
        }
    }

    private void removeListener(ServiceReference reference, ArtifactListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(reference);
        }
        List /*<DirectoryWatcher>*/ toNotify = new ArrayList /*<DirectoryWatcher>*/();
        synchronized (watchers)
        {
            toNotify.addAll(watchers.values());
        }
        for (Iterator w = toNotify.iterator(); w.hasNext();)
        {
            DirectoryWatcher dir = (DirectoryWatcher) w.next();
            dir.removeListener(listener);
        }
    }

    static List getListeners()
    {
        synchronized (listeners)
        {
            List l = new ArrayList(listeners.values());
            Collections.reverse(l);
            l.add(bundleTransformer);
            return l;
        }
    }

    /**
     * Convenience to refresh the packages
     */
    static void refresh(Bundle[] bundles)
    {
        PackageAdmin padmin = getPackageAdmin();
        if (padmin != null)
        {
            synchronized (refreshLock) {
                padmin.refreshPackages(bundles);
                try {
                    refreshLock.wait(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void frameworkEvent(FrameworkEvent event)
    {
        if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED
                || event.getType() == FrameworkEvent.ERROR)
        {
            synchronized (refreshLock)
            {
                refreshLock.notifyAll();
            }
        }
    }

    static PackageAdmin getPackageAdmin()
    {
        return getPackageAdmin(10000);
    }

    static PackageAdmin getPackageAdmin(long timeout)
    {
        try
        {
            return (PackageAdmin) padmin.waitForService(timeout);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    static StartLevel getStartLevel()
    {
        return getStartLevel(10000);
    }

    static StartLevel getStartLevel(long timeout)
    {
        try
        {
            return (StartLevel) startLevel.waitForService(timeout);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return null;
        }
    }

}