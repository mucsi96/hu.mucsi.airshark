package hu.mucsi.airshark.autoload.internal;

import hu.mucsi.airshark.autoload.ArtifactUrlTransformer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


public class BundleTransformer implements ArtifactUrlTransformer
{
    public boolean canHandle(File artifact)
    {
        JarFile jar = null;
        try
        {
            // Handle OSGi bundles with the default deployer
            String name = artifact.getName();
            if (!artifact.canRead()  
                || name.endsWith(".txt") || name.endsWith(".xml")
                || name.endsWith(".properties") || name.endsWith(".cfg"))
            {
                // that's file type which is not supported as bundle and avoid
                // exception in the log
                return false;
            }
            jar = new JarFile(artifact);
            Manifest m = jar.getManifest();
            if (m != null && m.getMainAttributes().getValue(new Attributes.Name("Bundle-SymbolicName")) != null)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            // Ignore
        }
        finally
        {
            if (jar != null)
            {
                try
                {
                    jar.close();
                }
                catch (IOException e)
                {
                    // Ignore
                }
            }
        }
        return false;
    }

    public URL transform(URL artifact)
    {
        return artifact;
    }

}
