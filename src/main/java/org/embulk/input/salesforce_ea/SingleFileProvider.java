package org.embulk.input.salesforce_ea;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.stream.IntStream;

import org.embulk.config.ConfigException;
import org.embulk.util.file.InputStreamFileInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileProvider implements InputStreamFileInput.Provider {

  private final Logger logger = LoggerFactory.getLogger(SingleFileProvider.class);
  private static int FITST_STEP_NUM = 0;

  private ForceClient client;
  private PluginTask pluginTask;
  private Dataset dataset;
  private Iterator<Integer> iterator;

  public SingleFileProvider(ForceClient client, PluginTask pluginTask, Dataset dataset) {
    this.client = client;
    this.pluginTask = pluginTask;
    this.dataset = dataset;

    logger.info(String.format("page size: %s", pluginTask.getPages()));
    this.iterator = IntStream.range(FITST_STEP_NUM, Integer.valueOf(pluginTask.getPages())).boxed().iterator();
  }

  @Override
  public InputStream openNext() {
    if (!iterator.hasNext()) {
      return null;
    }
    int offset = iterator.next() * Integer.parseInt(pluginTask.getStep());
    try {
      logger.info(String.format("query call: offset %d", offset));
      return client.query(dataset, offset, pluginTask.getStep());
    } catch (UnsupportedOperationException | IOException | URISyntaxException e) {
      logger.error(e.getMessage(), e);
      throw new ConfigException(e);
    }
  }

  @Override
  public void close() throws IOException {
  }
}
