package org.embulk.input.salesforce_ea;

import java.util.Optional;
import org.embulk.util.config.Config;
import org.embulk.util.config.ConfigDefault;
import org.embulk.util.config.Task;

public interface PluginTask extends Task {
  @Config("username")
  @ConfigDefault("null")
  Optional<String> getUsername();

  @Config("password")
  @ConfigDefault("null")
  Optional<String> getPassword();

  @Config("api_version")
  @ConfigDefault("\"46.0\"")
  String getApiVersion();

  @Config("connection_timeout")
  @ConfigDefault("\"600000\"")
  String getConnectionTimeout();

  @Config("security_token")
  @ConfigDefault("null")
  Optional<String> getSecurityToken();

  @Config("auth_end_point")
  @ConfigDefault("\"https://login.salesforce.com/services/Soap/u/\"")
  Optional<String> getAuthEndPoint();

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

  @Config("auth_method")
  @ConfigDefault("\"user_password\"")
  AuthMethod getAuthMethod();

  @Config("instance_url")
  @ConfigDefault("null")
  Optional<String> getInstanceUrl();

  @Config("access_token")
  @ConfigDefault("null")
  Optional<String> getAccessToken();
}
