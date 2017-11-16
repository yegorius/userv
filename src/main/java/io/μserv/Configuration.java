package io.μserv;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Configuration {
	private static final String[] unixos = {"linux", "macosx", "osx", "solaris", "sunos", "freebsd", "openbsd", "netbsd", "aix", "hpux"};
	private static final Config config = loadConfig();

	private static Config loadConfig() {
		Config defaultConfig = ConfigFactory.load();
		final String appName = defaultConfig.getString("app.name");

		final String workDir = System.getProperty("user.dir");
		final Path localConfigPath = Paths.get(workDir, "etc").resolve(appName);

		final String homeDir = System.getProperty("user.home");
		final Path userConfigPath = Paths.get(homeDir, ".config").resolve(appName);

		final ConfigParseOptions options = ConfigParseOptions.defaults().setAllowMissing(true);
		final Config localConfig = ConfigFactory.parseFileAnySyntax(localConfigPath.toFile(), options);
		final Config userConfig = ConfigFactory.parseFileAnySyntax(userConfigPath.toFile(), options);

		if (Arrays.asList(unixos).contains(System.getProperty("os.name"))) {
			final Path etcConfigPath = Paths.get("/etc").resolve(appName);
			final Config etcConfig = ConfigFactory.parseFileAnySyntax(etcConfigPath.toFile(), options);
			defaultConfig = etcConfig.withFallback(defaultConfig);
		}

		return localConfig.withFallback(userConfig.withFallback(defaultConfig));
	}

	public static Config get() {
		return config;
	}

	static int getServerPort() {
		return config.getInt("app.port");
	}

	static String getServerHost() {
		return config.getString("app.host");
	}

	static String getAppName() {
		return config.getString("app.name");
	}

	static String getContextPath() {
		return config.getString("app.contextPath");
	}
}
