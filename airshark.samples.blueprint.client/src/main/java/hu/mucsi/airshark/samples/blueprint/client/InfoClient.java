package hu.mucsi.airshark.samples.blueprint.client;

import hu.mucsi.airshark.samples.blueprint.interfacce.Info;

import java.util.Map;

import org.apache.aries.blueprint.annotation.Bean;
import org.apache.aries.blueprint.annotation.Bind;
import org.apache.aries.blueprint.annotation.Init;
import org.apache.aries.blueprint.annotation.Inject;
import org.apache.aries.blueprint.annotation.Reference;
import org.apache.aries.blueprint.annotation.ReferenceListener;
import org.apache.aries.blueprint.annotation.Unbind;

@Bean(id = "bindingListener")
@ReferenceListener
public class InfoClient {

	@Inject
	@Reference(id = "ref1", serviceInterface = Info.class,referenceListeners = @ReferenceListener(ref = "bindingListener"))
	private Info infoService;

	@Init
	public void init() {
		System.out.println("Hello from client init. The info is: " + infoService);
	}

	@Bind
	public void bind(Info info, Map props) {
		this.infoService = info;
		System.out.println("Hi! I am the Client. The information service is now avaible!");
		System.out.println(this.infoService.getInfo());
		System.out.println(this.infoService.getTime());
	}

	@Unbind
	public void unbind(Info info, Map props) {
		this.infoService = info;
		System.out.println("Hi! I am the Client. The information service is now not avaible!");
	}

}
