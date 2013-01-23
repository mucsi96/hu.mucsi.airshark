package hu.mucsi.airshark.samples.blueprint.multiple.server.kate;

import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greeting;

import org.apache.aries.blueprint.annotation.Bean;
import org.apache.aries.blueprint.annotation.Destroy;
import org.apache.aries.blueprint.annotation.Init;
import org.apache.aries.blueprint.annotation.Service;

@Bean(id = "KatesServer")
@Service(autoExport = "all-classes")
public class KatesServer implements Greeting{

	@Override
	public String getGreeting() {
		return "Hello! I am Kate.";
	}
	
	@Init
	public void init() {
		System.out.println("======== Starting Kates Server =========");
	}

	@Destroy
	public void destroy() {
		System.out.println("======== Stopping Kates Server =========");
	}

}
