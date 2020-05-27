package org.embulk.input.salesforce_ea;

import java.io.InputStream;

import org.embulk.config.TaskReport;
import org.embulk.spi.Exec;
import org.embulk.spi.TransactionalFileInput;
import org.embulk.spi.util.InputStreamFileInput;

public class SalesforceEaFileInput extends InputStreamFileInput implements TransactionalFileInput
{

  public SalesforceEaFileInput(PluginTask pluginTask, InputStream openedStream)
  {
    super(pluginTask.getBufferAllocator(), openedStream);
  }

  @Override
  public void abort()
  {
  }

  @Override
  public TaskReport commit()
  {
    return Exec.newTaskReport();
  }
}