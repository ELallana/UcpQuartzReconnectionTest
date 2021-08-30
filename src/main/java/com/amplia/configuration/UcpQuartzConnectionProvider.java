package com.amplia.configuration;
import lombok.extern.slf4j.Slf4j;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.quartz.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class UcpQuartzConnectionProvider  implements ConnectionProvider {
	PoolDataSourceImpl dataSource;
	
	public UcpQuartzConnectionProvider(){
		log.info("Creating PoolDataSource");
		dataSource = new PoolDataSourceImpl();
		try {
			dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		} catch (SQLException e) {
			log.warn("Unable to set ConnectionFactoryClassName to oracle.jdbc.pool.OracleDataSource", e);
		}
		try {
			Properties connectionFactoryProperties = dataSource.getConnectionFactoryProperties();
			String processName = connectionFactoryProperties.getProperty("v$session.program");
			if (processName == null || processName.isEmpty()) {
				processName = System.getProperty("process.name");
				if (processName == null || processName.isEmpty()) {
					processName = System.getProperty("app.name");
				}
			}
			connectionFactoryProperties.put("v$session.program", processName + "_QRTZ");
			dataSource.setConnectionFactoryProperties(connectionFactoryProperties);
		} catch (Exception e) {
			log.warn("Unable to set v$session.program", e);
		}
		log.info("Created PoolDataSource: " + dataSource + ": " + dataSource.getDescription());
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		try {
			return dataSource.getConnection();	
		} catch (SQLException e){
			log.warn("Fails to get connection with data source properties: {}.", dataSource.getConnectionProperties(), e);
			throw e;
		}
	}

	@Override
	public void shutdown() throws SQLException {

	}

	@Override
	public void initialize() throws SQLException {
//		log.info("Creating PoolDataSource");
//		dataSource = new PoolDataSourceImpl();
//		dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
//		log.info("Created PoolDataSource:" + dataSource);
	}
	
	public void setDataSourceName(String dataSourceName) throws SQLException {
		if (dataSourceName != null && !dataSourceName.isEmpty()) {
			dataSource.setDataSourceName(dataSourceName);
		}
	}
        
	public void setDriver(String connectionFactoryName) throws SQLException {
		dataSource.setConnectionFactoryClassName(connectionFactoryName);	
	}
	
	public void setURL(String url) throws SQLException {
		dataSource.setURL(url);	
	}
	public void setUser(String user) throws SQLException {
		if (user != null && !user.isEmpty()) {
			dataSource.setUser(user);
		}
	}
	public void setPassword(String password) throws SQLException {
		if (password != null && !password.isEmpty()) {
			dataSource.setPassword(password);
		}
	}
	public void setConnectionPoolName(String poolName) throws SQLException {
		if (poolName != null && !poolName.isEmpty()) {
			dataSource.setConnectionPoolName(poolName);
		}
	}
	public void setInitialPoolSize(int initialPoolSize) throws SQLException {
		dataSource.setInitialPoolSize(initialPoolSize);
	}
	public void setMinPoolSize(int minPoolSize) throws SQLException {
		dataSource.setMinPoolSize(minPoolSize);
	}
	public void setMaxPoolSize(int maxConnections) throws SQLException {
//		log.info("Setting max connections {} for PoolDataSource: {}",maxConnections, dataSource);
		//Default Integer.MAX_VALUE
		dataSource.setMaxPoolSize(maxConnections);
	}
	public void setConnectionWaitTimeout(int connectionWaitTimeout) throws SQLException {
		dataSource.setConnectionWaitTimeout(connectionWaitTimeout);
	}
	public void setTimeoutCheckInterval(int timeoutCheckInterval) throws SQLException {
		dataSource.setTimeoutCheckInterval(timeoutCheckInterval);
	}
	public void setAbandonedConnectionTimeout(int abandonedConnectionTimeout) throws SQLException {
		dataSource.setAbandonedConnectionTimeout(abandonedConnectionTimeout);
	}
	public void setTimeToLiveConnectionTimeout(int timeToLiveConnectionTimeout) throws SQLException {
		dataSource.setTimeToLiveConnectionTimeout(timeToLiveConnectionTimeout);
	}
	public void setInactiveConnectionTimeout(int discardIdleConnectionsSeconds) throws SQLException {
		dataSource.setInactiveConnectionTimeout(discardIdleConnectionsSeconds);
	}
	public void setMaxConnectionReuseTime(int maxConnectionReuseTime) throws SQLException {
		dataSource.setMaxConnectionReuseTime(maxConnectionReuseTime);
	}
	public void setConnectionHarvestTriggerCount(int connectionHarvestTriggerCount) throws SQLException {
		dataSource.setConnectionHarvestTriggerCount(connectionHarvestTriggerCount);
	}
	public void setConnectionHarvestMaxCount(int connectionHarvestMaxCount) throws SQLException {
		dataSource.setConnectionHarvestMaxCount(connectionHarvestMaxCount);
	}
	public void setFastConnectionFailoverEnabled(boolean fastConnectionFailoverEnabled) throws SQLException {
		//Default false
		dataSource.setFastConnectionFailoverEnabled(fastConnectionFailoverEnabled);
	}
	public void setOnsConfiguration(String onsConfiguration) throws SQLException {
		if (onsConfiguration != null && !onsConfiguration.isEmpty()) {
			dataSource.setONSConfiguration(onsConfiguration);
		}
	}
	public void setValidateConnectionOnBorrow(boolean validateOnCheckout) throws SQLException {
		//Default false
		dataSource.setValidateConnectionOnBorrow(validateOnCheckout);
	}
	public void setSqlForValidateConnection(String validationQuery) throws SQLException {
		if (validationQuery != null && !validationQuery.isEmpty()) {
			dataSource.setSQLForValidateConnection(validationQuery);
		}
	}
	public void setMaxStatements(int maxStatementsPerConnection) throws SQLException {
		dataSource.setMaxStatements(maxStatementsPerConnection);
	}

	public void setProcessName(String processName) throws SQLException {
		Properties connectionFactoryProperties = dataSource.getConnectionFactoryProperties();
		connectionFactoryProperties.put("v$session.program", processName);
		dataSource.setConnectionFactoryProperties(connectionFactoryProperties);
	}
	public void setWalletLocation(String walletLocation) throws SQLException {
		if (walletLocation != null && !walletLocation.isEmpty()) {
			Properties connectionFactoryProperties = dataSource.getConnectionFactoryProperties();
			connectionFactoryProperties.put("oracle.net.wallet_location", walletLocation);
			dataSource.setConnectionFactoryProperties(connectionFactoryProperties);
		}
	}
	public void setTnsLocation(String tnsLocation) throws SQLException {
		if (tnsLocation != null && !tnsLocation.isEmpty()) {
			Properties connectionFactoryProperties = dataSource.getConnectionFactoryProperties();
			connectionFactoryProperties.put("oracle.net.tns_admin", tnsLocation);
			dataSource.setConnectionFactoryProperties(connectionFactoryProperties);
		}
	}
	public void setReadTimeout(int readTimeout) throws SQLException {
		Properties connectionFactoryProperties = dataSource.getConnectionFactoryProperties();
		connectionFactoryProperties.put("oracle.jdbc.ReadTimeout", readTimeout);
		dataSource.setConnectionFactoryProperties(connectionFactoryProperties);
	}
}
