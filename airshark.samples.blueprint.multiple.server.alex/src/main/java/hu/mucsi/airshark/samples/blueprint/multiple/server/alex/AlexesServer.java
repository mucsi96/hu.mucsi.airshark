package hu.mucsi.airshark.samples.blueprint.multiple.server.alex;

import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greeting;

import org.apache.aries.blueprint.annotation.Bean;
import org.apache.aries.blueprint.annotation.Destroy;
import org.apache.aries.blueprint.annotation.Init;
import org.apache.aries.blueprint.annotation.Service;

@Bean(id = "AlexesServer")
@Service(autoExport = "all-classes")
public class AlexesServer implements Greeting{

	@Override
	public String getGreeting() {
		return "Hello! I am Alex.";
	}
	
	@Init
	public void init() {
		System.out.println("======== Starting Alexes Server =========");
	}

	@Destroy
	public void destroy() {
		System.out.println("======== Stopping Alexes Server =========");
	}

}
