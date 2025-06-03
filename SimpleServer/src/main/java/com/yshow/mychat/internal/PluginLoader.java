package com.yshow.mychat.internal;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.SimpleServer;
import com.yshow.mychat.plugin.Plugin;
import com.yshow.mychat.server.Server;

public final class PluginLoader {

    private static final Logger log = LoggerFactory.getLogger(PluginLoader.class);

    private final Server server;

    private final List<Plugin> plugins;

    public PluginLoader(Server server) {
	this.server = server;
	this.plugins = new ArrayList<Plugin>();
    }

    public final void loadPlugins() {
	final var pluginDir = Path.of("plugins"); // Directory containing plugin JAR files

	if (log.isDebugEnabled()) {
	    try {

		log.debug("Files Contained in path:{} ", pluginDir.toAbsolutePath());

		if (log.isTraceEnabled()) {
		try (Stream<Path> paths = Files.walk(pluginDir)) {
		    paths.forEach(path -> log.trace("{}", path));
		}
	    }

	    } catch (IOException e) {
		log.warn("Error walking files", e);
	    }
	}

	loadAllPlugins(pluginDir);
    }

    private final void loadAllPlugins(Path pluginDir) {

	log.atDebug().setMessage("Loading plugins").log();

	log.atTrace().setMessage("Initializing ModuleFinder on path {} ").addArgument(pluginDir.toAbsolutePath()).log();

	ModuleFinder pluginsFinder = ModuleFinder.of(pluginDir);

	log.atTrace().setMessage("Creating pluginConfiguration ").log();

	Configuration pluginsConfiguration = ModuleLayer.boot().configuration().resolveAndBind(pluginsFinder,
		ModuleFinder.of(), Set.of());

	log.atTrace().setMessage("PluginConfiguration created {} ").addArgument(pluginsConfiguration).log();

	log.atTrace().setMessage("Creating ModuleLayer").log();

	ModuleLayer layer = ModuleLayer.boot().defineModulesWithOneLoader(pluginsConfiguration,
		SimpleServer.class.getClassLoader());

	log.atTrace().setMessage("ModuleLayer created {}").addArgument(layer).log();

	log.atTrace().setMessage("Finding plugins with ServiceLoader").log();


	List<Plugin> services = ServiceLoader.load(layer, Plugin.class).stream().map(Provider::get).toList();

	for (Plugin plugin : services) {

	    log.atTrace().setMessage("Initializing plugins, current plugin {}")
		    .addArgument(() -> plugin.getClass().getSimpleName()).log();

	    plugins.add(plugin);

	    plugin.initialize(server);

	}

    }

    /*
     * return immutable view of the plugins list
     */
    public List<Plugin> getPlugins() {

	return List.copyOf(plugins);
    }

    public boolean removePlugin(Plugin plugin) {
	
	plugin.disable();

	return plugins.remove(plugin);
    }

}
