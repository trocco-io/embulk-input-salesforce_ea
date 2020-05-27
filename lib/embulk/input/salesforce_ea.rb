Embulk::JavaPlugin.register_input(
  "salesforce_ea", "org.embulk.input.salesforce_ea.SalesforceEaFileInputPlugin",
  File.expand_path('../../../../classpath', __FILE__))
