package io.μserv;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

public abstract class μServ {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private UndertowJaxrsServer server;
	private Class<? extends Application> application;
	private Reflections reflections;
	private String pakcage;

	public void start() {
		server = new UndertowJaxrsServer();

		/*DeploymentInfo di = new DeploymentInfo()
		.setClassLoader(this.getClass().getClassLoader())
		.addServletContainerInitalizer(new ServletContainerInitializerInfo(ResteasyServletInitializer.class, getClasses()))*/

		final DeploymentInfo di = server.undertowDeployment(application)
				.setDeploymentName(Configuration.getAppName())
				.setContextPath(Configuration.getContextPath())
				.addListener(new ListenerInfo(GuiceResteasyBootstrapServletContextListener.class))
				.addInitParameter("resteasy.guice.modules", getModules());

		server.deploy(di);
		int port = Configuration.getServerPort();
		String host = Configuration.getServerHost();
		server.start(Undertow.builder().addHttpListener(port, host));
		log.info("Listening on {}:{}", host, port);
	}

	public void stop() {
		server.stop();
	}

	private String getModules() {
		ConfigModule.setClasses(getClasses());
		return reflections.getSubTypesOf(Module.class)
				.stream()
				.map(Class::getName)
				.filter(s -> s.startsWith(pakcage))
				.collect(Collectors.joining(","))
				.concat(",")
				.concat(ConfigModule.class.getName());
	}

	private Set<Class<?>> getClasses() {
		//final Set<Class<? extends Application>> applications = reflections.getSubTypesOf(Application.class);
		final Set<Class<?>> providers = reflections.getTypesAnnotatedWith(Provider.class);
		final Set<Class<?>> resources = reflections.getTypesAnnotatedWith(Path.class);
		return new ImmutableSet.Builder()
				.addAll(providers)
				.addAll(resources)
				.build();
	}

	protected void application(Class<? extends Application> application) {
		this.application = application;
	}

	protected void pakcage(String pakcage) {
		this.pakcage = pakcage;
		this.reflections = new Reflections(pakcage);
	}
}
