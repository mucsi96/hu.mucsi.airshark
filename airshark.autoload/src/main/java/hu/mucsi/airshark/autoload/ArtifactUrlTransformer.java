package hu.mucsi.airshark.autoload;

import java.net.URL;

public interface ArtifactUrlTransformer extends ArtifactListener
{

     URL transform(URL artifact) throws Exception;
}