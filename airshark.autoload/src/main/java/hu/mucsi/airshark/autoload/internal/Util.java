package hu.mucsi.airshark.autoload.internal;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Util
{
    private static final String CHECKSUM_SUFFIX = ".checksum";

    /**
     * Returns the log level as defined in the BundleContext or System properties.
     * @param context {@link BundleContext} of the FileInstall bundle.
     * @return the global log level, or {@link Logger#LOG_ERROR}.
     */
    public static int getGlobalLogLevel(BundleContext context)
    {
        String s = (String) context.getProperty(DirectoryWatcher.LOG_LEVEL);
        s = (s == null)
            ? System.getProperty(DirectoryWatcher.LOG_LEVEL.toUpperCase().replace('.', '_'))
            : s;
        s = (s == null) ? "1" : s;
        int logLevel = Logger.LOG_ERROR;
        try
        {
            logLevel = Integer.parseInt(s);
        }
        catch (NumberFormatException ex)
        {
        }
        return logLevel;
    }

    /**
     * Log a message and optional throwable. If there is a log service we use
     * it, otherwise we log to the console
     *
     * @param message
     *            The message to log
     * @param e
     *            The throwable to log
     */
    public static void log(BundleContext context, int logLevel,
        int msgLevel, String message, Throwable e)
    {
        getLogger(context).log(logLevel, msgLevel, message, e);
    }

    private static Logger getLogger(BundleContext context)
    {
        try
        {
            context.getBundle();
            if (logger != null && logger.isValidLogger(context))
            {
                return logger;
            }
            logger = new OsgiLogger(context);
        }
        catch (Throwable t)
        {
            logger = new StdOutLogger();
        }
        return logger;
    }

    private static Logger logger;

    interface Logger
    {
        static final int LOG_ERROR = 1;
        static final int LOG_WARNING = 2;
        static final int LOG_INFO = 3;
        static final int LOG_DEBUG = 4;

        boolean isValidLogger(BundleContext context);
        void log(int logLevel, int msgLevel, String message, Throwable throwable);
    }

    static class StdOutLogger implements Logger
    {
        public boolean isValidLogger(BundleContext context)
        {
            return true;
        }

        public void log(int logLevel, int msgLevel, String message, Throwable throwable)
        {
            // Only print the message if logging is enabled and
            // the message level is less than or equal to the log
            // level.
            if ((logLevel > 0) && (msgLevel <= logLevel))
            {
                System.out.println(message + ((throwable == null) ? "" : ": " + throwable));
                if (throwable != null)
                {
                    throwable.printStackTrace(System.out);
                }
            }
        }
    }

    static class OsgiLogger extends StdOutLogger
    {
        private BundleContext context;

        OsgiLogger(BundleContext context)
        {
            this.context = context;
            // Now make sure we can access the LogService class
            try
            {
                getClass().getClassLoader().loadClass(LogService.class.getName());
            }
            catch (ClassNotFoundException e)
            {
                throw new NoClassDefFoundError(e.getMessage());
            }
        }

        public boolean isValidLogger(BundleContext context)
        {
            return context == this.context;
        }

        public void log(int logLevel, int msgLevel, String message, Throwable throwable)
        {
            // Only print the message if logging is enabled and
            // the message level is less than or equal to the log
            // level.
            if ((logLevel > 0) && (msgLevel <= logLevel))
            {
                LogService log = getLogService();
                if (log != null)
                {
                    log.log(msgLevel, message, throwable);
                }
                else
                {
                    super.log(logLevel, msgLevel, message, throwable);
                }
            }
        }

        private LogService getLogService()
        {
            ServiceReference ref = context.getServiceReference(LogService.class.getName());
            if (ref != null)
            {
                LogService log = (LogService) context.getService(ref);
                return log;
            }
            return null;
        }
    }

    /**
     * Jar up a directory
     *
     * @param directory
     * @param zipName
     * @throws IOException
     */
    public static void jarDir(File directory, File zipName) throws IOException
    {
        jarDir(directory, new BufferedOutputStream(new FileOutputStream(zipName)));
    }

    public static void jarDir(File directory, OutputStream os) throws IOException
    {
        // create a ZipOutputStream to zip the data to
        JarOutputStream zos = new JarOutputStream(os);
        zos.setLevel(Deflater.NO_COMPRESSION);
        String path = "";
        File manFile = new File(directory, JarFile.MANIFEST_NAME);
        if (manFile.exists())
        {
            byte[] readBuffer = new byte[8192];
            FileInputStream fis = new FileInputStream(manFile);
            try
            {
                ZipEntry anEntry = new ZipEntry(JarFile.MANIFEST_NAME);
                zos.putNextEntry(anEntry);
                int bytesIn = fis.read(readBuffer);
                while (bytesIn != -1)
                {
                    zos.write(readBuffer, 0, bytesIn);
                    bytesIn = fis.read(readBuffer);
                }
            }
            finally
            {
                fis.close();
            }
            zos.closeEntry();
        }
        zipDir(directory, zos, path, Collections.singleton(JarFile.MANIFEST_NAME));
        // close the stream
        zos.close();
    }

    /**
     * Zip up a directory path
     * @param directory
     * @param zos
     * @param path
     * @param exclusions
     * @throws IOException
     */
    public static void zipDir(File directory, ZipOutputStream zos, String path, Set/* <String> */ exclusions) throws IOException
    {
        // get a listing of the directory content
        File[] dirList = directory.listFiles();
        byte[] readBuffer = new byte[8192];
        int bytesIn = 0;
        // loop through dirList, and zip the files
        for (int i = 0; i < dirList.length; i++)
        {
            File f = dirList[i];
            if (f.isDirectory())
            {
                String prefix = path + f.getName() + "/";
                zos.putNextEntry(new ZipEntry(prefix));
                zipDir(f, zos, prefix, exclusions);
                continue;
            }
            String entry = path + f.getName();
            if (!exclusions.contains(entry))
            {
                FileInputStream fis = new FileInputStream(f);
                try
                {
                    ZipEntry anEntry = new ZipEntry(entry);
                    zos.putNextEntry(anEntry);
                    bytesIn = fis.read(readBuffer);
                    while (bytesIn != -1)
                    {
                        zos.write(readBuffer, 0, bytesIn);
                        bytesIn = fis.read(readBuffer);
                    }
                }
                finally
                {
                    fis.close();
                }
            }
        }
    }

    /**
     * Stores the checksum into a bundle data file.
     * @param b The bundle whose checksum must be stored
     * @param checksum the lastModified date to be stored in bc
     * @param bc the FileInstall's bundle context where to store the checksum.
     */
    public static void storeChecksum( Bundle b, long checksum, BundleContext bc )
    {
        String key = getBundleKey(b);
        File f = bc.getDataFile( key + CHECKSUM_SUFFIX );
        DataOutputStream dout = null;
        try
        {
            dout = new DataOutputStream( new FileOutputStream( f ) );
            dout.writeLong( checksum );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( dout != null )
            {
                try
                {
                    dout.close();
                }
                catch ( IOException ignored )
                {
                }
            }
        }
    }

    /**
     * Returns the stored checksum of the bundle.
     * @param b the bundle whose checksum must be returned
     * @param bc the FileInstall's bundle context.
     * @return the stored checksum of the bundle
     */
    public static long loadChecksum( Bundle b, BundleContext bc )
    {
        String key = getBundleKey(b);
        File f = bc.getDataFile( key + CHECKSUM_SUFFIX );
        DataInputStream in = null;
        try
        {
            in = new DataInputStream( new FileInputStream( f ) );
            return in.readLong();
        }
        catch ( Exception e )
        {
            return Long.MIN_VALUE;
        }
        finally
        {
            if ( in != null )
            {
                try
                {
                    in.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
    }

    private static String getBundleKey(Bundle b)
    {
        return Long.toString(b.getBundleId());
    }

}
