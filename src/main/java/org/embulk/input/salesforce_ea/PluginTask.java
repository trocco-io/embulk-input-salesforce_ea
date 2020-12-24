package org.embulk.input.salesforce_ea;

import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigInject;
import org.embulk.config.Task;
import org.embulk.spi.BufferAllocator;
import org.embulk.spi.SchemaConfig;

public interface PluginTask extends Task
{
  @Config("username")
  String getUsername();

  @Config("password")
  String getPassword();

  @Config("api_version")
  @ConfigDefault("\"46.0\"")
  String getApiVersion();

  @Config("connection_timeout")
  @ConfigDefault("\"600000\"")
  String getConnectionTimeout();

  @Config("security_token")
  String getSecurityToken();

  @Config("auth_end_point")
  @ConfigDefault("\"https://login.salesforce.com/services/Soap/u/\"")
  String getAuthEndPoint();

  @Config("dataset_id")
  String getDatasetId();

  @Config("saql")
  String getSaql();

  @Config("columns")
  @ConfigDefault("[]")
  SchemaConfig getColumns();

  @Config("pages")
  @ConfigDefault("1")
  String getPages();

  @Config("step")
  @ConfigDefault("10000")
  String getStep();

  @ConfigInject
  BufferAllocator getBufferAllocator();

}