package com.lordofthejars.nosqlunit.neo4j;

import static com.lordofthejars.nosqlunit.core.IOUtils.deleteDir;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lordofthejars.nosqlunit.core.AbstractLifecycleManager;

public class ManagedWrappingNeoServer extends AbstractLifecycleManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManagedWrappingNeoServer.class); 
	
	protected static final String LOCALHOST = "127.0.0.1";

	protected static final String DEFAULT_NEO4J_TARGET_PATH = "target" + File.separatorChar + "neo4j-temp";

	private String targetPath = DEFAULT_NEO4J_TARGET_PATH;
	private int port = Configurator.DEFAULT_WEBSERVER_PORT;
	
	private WrappingNeoServerBootstrapper graphDb;

	private ManagedWrappingNeoServer() {
		super();
	}
	
	
	public static class ManagedWrappingNeoServerRuleBuilder {

		private ManagedWrappingNeoServer managedWrappingNeoServer;

		private ManagedWrappingNeoServerRuleBuilder() {
			this.managedWrappingNeoServer = new ManagedWrappingNeoServer();
		}

		public static ManagedWrappingNeoServerRuleBuilder newWrappingNeoServerNeo4jRule() {
			return new ManagedWrappingNeoServerRuleBuilder();
		}

		public ManagedWrappingNeoServerRuleBuilder port(int port) {
			this.managedWrappingNeoServer.setPort(port);
			return this;
		}
		
		public ManagedWrappingNeoServerRuleBuilder targetPath(String targetPath) {
			this.managedWrappingNeoServer.setTargetPath(targetPath);
			return this;
		}

		public ManagedWrappingNeoServer build() {
			if (this.managedWrappingNeoServer.getTargetPath() == null) {
				throw new IllegalArgumentException("No Path to Embedded Neo4j is provided.");
			}
			return this.managedWrappingNeoServer;
		}

	}
	
	@Override
	protected String getHost() {
		return LOCALHOST;
	}


	@Override
	protected int getPort() {
		return port;
	}


	@Override
	protected void doStart() throws Throwable {
		
		LOGGER.info("Starting {} wrapped Neo4j instance.", getHost()+getPort());
		
		cleanDb();
		createWrappingEmbeddedGraphDatabaseService();
		graphDb.start();
		
		LOGGER.info("Starting {} wrapped Neo4j instance.", getHost()+getPort());
	}


	@Override
	protected void doStop() {
		
		LOGGER.info("Stopping {} wrapped Neo4j instance.", getHost()+getPort());

		stopGraphDb();
		
		LOGGER.info("Stopped {} wrapped Neo4j instance.", getHost()+getPort());
	}


	private void stopGraphDb() {
		try {
			this.graphDb.stop();
		} finally {
			cleanDb();
		}
	}


	private GraphDatabaseService createWrappingEmbeddedGraphDatabaseService() {
		
		GraphDatabaseService newEmbeddedDatabase = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(this.targetPath).newGraphDatabase();
		
		Configurator embeddedServerConfigurator = new EmbeddedServerConfigurator((GraphDatabaseAPI) newEmbeddedDatabase);
		embeddedServerConfigurator.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, getPort());
		
		graphDb = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) newEmbeddedDatabase, embeddedServerConfigurator);
		
		return newEmbeddedDatabase;
	}


	private void cleanDb() {
		File dbPath = new File(targetPath);
		if (dbPath.exists()) {
			deleteDir(dbPath);
		}
	}


	private void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	
	private String getTargetPath() {
		return targetPath;
	}
	
	private void setPort(int port) {
		this.port = port;
	}
	
}
