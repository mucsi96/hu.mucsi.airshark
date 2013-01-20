package hu.mucsi.airshark.autoload;

import java.io.File;

public interface ArtifactListener
{
    boolean canHandle(File artifact);
}
