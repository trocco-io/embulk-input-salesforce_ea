package org.embulk.input.salesforce_ea;

import org.embulk.config.TaskReport;
import org.embulk.spi.Exec;
import org.embulk.spi.TransactionalFileInput;
import org.embulk.util.file.InputStreamFileInput;

@SuppressWarnings(
    "deprecation") // Exec.getBufferAllocator(), Exec.newTaskReport() for embulk v0.9 comp
public class SalesforceEaFileInput extends InputStreamFileInput implements TransactionalFileInput {

  public SalesforceEaFileInput(ForceClient client, PluginTask pluginTask, Dataset dataset) {
    super(Exec.getBufferAllocator(), new SingleFileProvider(client, pluginTask, dataset));
  }

  @Override
  public void abort() {}

  @Override
  public TaskReport commit() {
    return Exec.newTaskReport();
  }
}
