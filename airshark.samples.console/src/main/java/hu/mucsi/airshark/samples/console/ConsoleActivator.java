package hu.mucsi.airshark.samples.console;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ConsoleActivator implements BundleActivator {
	public ConsoleActivator() {
		System.out.println("Welcome to airshark!");
		System.out.println("This is activators constructor.");
	}

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("This is activators start method"); 

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("This is activators stop method");

	}
}
