package org.embulk.input.salesforce_ea;

import org.embulk.util.config.Config;
import org.embulk.util.config.ConfigDefault;
import org.embulk.util.config.Task;

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

  @Config("pages")
  @ConfigDefault("1")
  String getPages();

  @Config("step")
  @ConfigDefault("10000")
  String getStep();

}