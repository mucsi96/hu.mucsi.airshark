package hu.mucsi.airshark.samples.blueprint.client;


import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greeting;
import hu.mucsi.airshark.samples.blueprint.multiple.interfacce.Greetings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.aries.blueprint.annotation.Bean;
import org.apache.aries.blueprint.annotation.Bind;
import org.apache.aries.blueprint.annotation.Init;
import org.apache.aries.blueprint.annotation.Inject;
import org.apache.aries.blueprint.annotation.ReferenceList;
import org.apache.aries.blueprint.annotation.ReferenceListener;
import org.apache.aries.blueprint.annotation.Service;
import org.apache.aries.blueprint.annotation.Unbind;

@Bean(id = "bindingListener")
@ReferenceListener
@Service(autoExport = "all-classes")
public class GreetingClient implements Greetings{

	@SuppressWarnings("unused")
	@Inject
	@ReferenceList(serviceInterface = Greeting.class,referenceListeners = @ReferenceListener(ref = "bindingListener"))
	private Greeting current;
	
	private List<Greeting> greetings = new ArrayList<Greeting>();

	@Init
	public void init() {
		System.out.println("Hello from client init. The greetings are: " + greetings);
	}

	@SuppressWarnings("rawtypes")
	@Bind
	public void bind(Greeting greeting,Map props) {
		this.greetings.add(greeting);
		System.out.println("Hi! I am the Client. Avaible greetings are:");
		for(Greeting g:greetings) {
			System.out.println(g.getGreeting());
		}
	}

	@SuppressWarnings("rawtypes")
	@Unbind
	public void unbind(Greeting greeting,Map props) {
		this.greetings.remove(greeting);
		System.out.println("Hi! I am the Client. Avaible greetings are:");
		for(Greeting g:greetings) {
			System.out.println(g.getGreeting());
		}
	}

	@Override
	public List<Greeting> getGreetings() {
		return greetings;
	}

}
