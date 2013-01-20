package hu.mucsi.airshark.autoload;

import java.io.File;

public interface ArtifactInstaller extends ArtifactListener
{

    void install(File artifact) throws Exception;

    void update(File artifact) throws Exception;

    void uninstall(File artifact) throws Exception;

}
