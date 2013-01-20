package hu.mucsi.airshark.autoload;

import java.io.File;


public interface ArtifactTransformer extends ArtifactListener
{
     File transform(File artifact, File tmpDir) throws Exception;
}
