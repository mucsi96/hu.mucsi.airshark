package hu.mucsi.airshark.autoload.internal;

import hu.mucsi.airshark.autoload.ArtifactListener;

import java.io.File;
import java.net.URL;


public class Artifact
{

    private File path;
    private File jaredDirectory;
    private URL jaredUrl;
    private ArtifactListener listener;
    private URL transformedUrl;
    private File transformed;
    private long bundleId = -1;
    private long checksum;

    public File getPath()
    {
        return path;
    }

    public void setPath(File path)
    {
        this.path = path;
    }

    public File getJaredDirectory()
    {
        return jaredDirectory;
    }

    public void setJaredDirectory(File jaredDirectory)
    {
        this.jaredDirectory = jaredDirectory;
    }

    public URL getJaredUrl()
    {
        return jaredUrl;
    }

    public void setJaredUrl(URL jaredUrl)
    {
        this.jaredUrl = jaredUrl;
    }

    public ArtifactListener getListener()
    {
        return listener;
    }

    public void setListener(ArtifactListener listener)
    {
        this.listener = listener;
    }

    public File getTransformed()
    {
        return transformed;
    }

    public void setTransformed(File transformed)
    {
        this.transformed = transformed;
    }

    public URL getTransformedUrl()
    {
        return transformedUrl;
    }

    public void setTransformedUrl(URL transformedUrl)
    {
        this.transformedUrl = transformedUrl;
    }

    public long getBundleId()
    {
        return bundleId;
    }

    public void setBundleId(long bundleId)
    {
        this.bundleId = bundleId;
    }

    public long getChecksum()
    {
        return checksum;
    }

    public void setChecksum(long checksum)
    {
        this.checksum = checksum;
    }
}
