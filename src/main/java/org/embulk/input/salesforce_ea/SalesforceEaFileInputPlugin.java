package org.embulk.input.salesforce_ea;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.sforce.async.AsyncApiException;
import com.sforce.ws.ConnectionException;

import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigException;
import org.embulk.config.ConfigSource;
import org.embulk.config.TaskReport;
import org.embulk.config.TaskSource;
import org.embulk.spi.Exec;
import org.embulk.spi.FileInputPlugin;
import org.embulk.spi.TransactionalFileInput;
import org.embulk.util.config.ConfigMapper;
import org.embulk.util.config.ConfigMapperFactory;
import org.embulk.util.config.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesforceEaFileInputPlugin implements FileInputPlugin {
    private final Logger logger = LoggerFactory.getLogger(SalesforceEaFileInputPlugin.class);

    private static final ConfigMapperFactory CONFIG_MAPPER_FACTORY =
        ConfigMapperFactory.builder().addDefaultModules().build();

    @Override
    public ConfigDiff transaction(ConfigSource config, FileInputPlugin.Control control) {
        final ConfigMapper configMapper = CONFIG_MAPPER_FACTORY.createConfigMapper();
        PluginTask task = configMapper.map(config, PluginTask.class);
        return resume(task.toTaskSource(), 1, control);
    }

    @Override
    public ConfigDiff resume(TaskSource taskSource, int taskCount, FileInputPlugin.Control control) {
        control.run(taskSource, taskCount);
        @SuppressWarnings("deprecation") // Exec.newConfigDiff() for embulk v0.9 comp
        final ConfigDiff configDiff = Exec.newConfigDiff();
        return configDiff;
    }

    @Override
    public void cleanup(TaskSource taskSource, int taskCount, List<TaskReport> successTaskReports) {
    }

    @Override
    public TransactionalFileInput open(TaskSource taskSource, int taskIndex)
    {
        try {
            final TaskMapper taskMapper = CONFIG_MAPPER_FACTORY.createTaskMapper();
            final PluginTask task = taskMapper.map(taskSource, PluginTask.class);
            ForceClient forceClient = new ForceClient(task);
            Dataset dataset = forceClient.getDataset();
            return new SalesforceEaFileInput(forceClient, task, dataset);
        }
        catch (AsyncApiException | ConnectionException | URISyntaxException | UnsupportedOperationException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new ConfigException(e);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
