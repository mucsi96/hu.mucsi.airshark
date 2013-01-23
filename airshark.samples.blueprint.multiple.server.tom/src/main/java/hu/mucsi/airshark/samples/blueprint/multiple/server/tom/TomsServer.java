package hu.mucsi.airshark.samples.blueprint.multiple.server.tom;

import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greeting;

import org.apache.aries.blueprint.annotation.Bean;
import org.apache.aries.blueprint.annotation.Destroy;
import org.apache.aries.blueprint.annotation.Init;
import org.apache.aries.blueprint.annotation.Service;

@Bean(id = "TomsServer")
@Service(autoExport = "all-classes")
public class TomsServer implements Greeting{

	@Override
	public String getGreeting() {
		return "Hello! I am Tom.";
	}
	
	@Init
	public void init() {
		System.out.println("======== Starting Toms Server =========");
	}

	@Destroy
	public void destroy() {
		System.out.println("======== Stopping Toms Server =========");
	}

}
