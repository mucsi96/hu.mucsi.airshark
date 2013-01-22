package hu.mucsi.airshark.samples.blueprint.server;

import hu.mucsi.airshark.samples.blueprint.interfacce.Info;

import java.util.Date;

import org.apache.aries.blueprint.annotation.Bean;
import org.apache.aries.blueprint.annotation.Destroy;
import org.apache.aries.blueprint.annotation.Init;
import org.apache.aries.blueprint.annotation.Service;

@Bean(id = "server")
@Service(autoExport = "all-classes")
public class InfoServer implements Info {

	@Init
	public void init() {
		System.out.println("======== Starting Info Server =========");
	}

	@Destroy
	public void destroy() {
		System.out.println("======== Stopping Info Server =========");
	}

	@Override
	public String getInfo() {
		return "Hi! I am the server. I will provide you some information using Info interface and the Blueprint specification.";
	}

	@Override
	public String getTime() {
		return "The time is " + new Date() + " [server]";
	}

}
