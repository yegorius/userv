package io.μserv;

import com.google.inject.AbstractModule;

import java.util.Set;

public class ConfigModule extends AbstractModule {
	private static Set<Class<?>> classes;

	static void setClasses(Set<Class<?>> classes) {
		ConfigModule.classes = classes;
	}

	@Override
	protected void configure() {
		classes.forEach(this::bind);
	}
}
