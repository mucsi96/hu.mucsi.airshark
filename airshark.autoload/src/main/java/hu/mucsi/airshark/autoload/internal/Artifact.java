package hu.mucsi.airshark.autoload.internal;

import hu.mucsi.airshark.autoload.ArtifactListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;


public class Artifact
{

    @Override
	public String toString() {
		return symbolicName + version;
	}

	private File path;
    private File jaredDirectory;
    private URL jaredUrl;
    private ArtifactListener listener;
    private URL transformedUrl;
    private File transformed;
    private long bundleId = -1;
    private long checksum;
    private String symbolicName;
    private Version version;

    public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

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
    
    public void loadProps() throws IOException, BundleException {
    	InputStream is = new FileInputStream(transformed != null ? transformed : path);
        try
        {
        	is.mark(256 * 1024);
            JarInputStream jar = new JarInputStream(is);
            Manifest m = jar.getManifest();
            if( m == null ) {
                throw new BundleException(
                    "The bundle " + path + " does not have a META-INF/MANIFEST.MF! "+
                        "Make sure, META-INF and MANIFEST.MF are the first 2 entries in your JAR!");
            }
            String sn = m.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
            String vStr = m.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
            Version v = vStr == null ? Version.emptyVersion : Version.parseVersion(vStr);
            this.setSymbolicName(sn);
            this.setVersion(v);
        }
        finally
        {
            is.close();
        }
    }
}
