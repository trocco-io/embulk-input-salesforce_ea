package org.embulk.input.salesforce_ea;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.async.AsyncApiException;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.embulk.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceClient {

    private final Logger logger = LoggerFactory.getLogger(ForceClient.class);

    private static final String SERVICE_PATH = "/services/data/";
    private static final String PATH_WAVE_QUERY = "/wave/query";
    private static final String PATH_WAVE_DATASETS = "/wave/datasets/";
    private static final String STR_UTF_8 = "UTF-8";
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static final String SYS_PROPERTY_SOCKET_TIMEOUT = "com.springml.socket.timeout";
    private static final String HEADER_AUTH = "Authorization";
    private static final String HEADER_OAUTH = "OAuth ";
    private static final String STR_QUERY = "query";
    private static final String HEADER_ACCEPT = "Accept";

    private PluginTask pluginTask;
    private PartnerConnection partnerConnection;

    public ForceClient(PluginTask pluginTask) throws AsyncApiException, ConnectionException, URISyntaxException {
        this.pluginTask = pluginTask;
        this.partnerConnection = createConnectorConfig();
    }

    public InputStream query(Dataset dataset, int offset, String limit) throws UnsupportedOperationException, IOException, URISyntaxException {
        String waveQueryPath = getWaveQueryPath();
        URI queryURI = getRequestURI(partnerConnection, waveQueryPath, null);
        ObjectMapper objectMapper = createObjectMapper();
        String datasetId = dataset.getId() + "/" + dataset.getCurrentVersionId();
        String saql = createSaqlWithOffset(datasetId, dataset.getName(), Integer.toString(offset), limit);
        String request = createRequest(objectMapper, saql);
        return post(queryURI, getSessionId(), request);
    }

    public Dataset getDataset() throws URISyntaxException, UnsupportedOperationException, IOException
    {
        String datasetsQueryPath = getDatasetsQueryPath();
        URI queryURI = getRequestURI(partnerConnection, datasetsQueryPath, null);

        String response = get(queryURI, getSessionId());
        ObjectMapper objectMapper = createObjectMapper();
        Dataset dataset = objectMapper.readValue(response.getBytes(), Dataset.class);
        logger.info(String.format("[dataset] %s", dataset.getName()));
        return dataset;
    }

    private ObjectMapper createObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

        return objectMapper;
    }

    private String createRequest(ObjectMapper objectMapper, String saql) throws JsonProcessingException
    {
        Map<String, String> saqlMap = new HashMap<String, String>();
        saqlMap.put(STR_QUERY, saql);
        return objectMapper.writeValueAsString(saqlMap);
    }

    private String createSaqlWithOffset(String datasetId, String name, String offset, String limit) {
        String loadStatement = String.format("q = load \"%s\";", name);

        String saqlLoad = pluginTask.getSaql();
        if(!saqlLoad.contains(loadStatement)){
            throw new ConfigException("dataset name do not match");
        }

        String loadDatasetIdStatement = String.format("q = load \"%s\";", datasetId);

        saqlLoad = saqlLoad.replace(loadStatement, loadDatasetIdStatement);

        StringBuilder saqlBuilder = new StringBuilder();
        saqlBuilder.append(saqlLoad);
        saqlBuilder.append(System.lineSeparator());
        saqlBuilder.append(String.format("q = offset q %s;", offset));
        saqlBuilder.append(System.lineSeparator());
        saqlBuilder.append(String.format("q = limit q %s;", limit));

        logger.info(String.format("[SAQL] %s", saqlBuilder.toString()));
        return saqlBuilder.toString();
    }

    private String getSessionId()
    {
        return partnerConnection.getConfig().getSessionId();
    }

    private PartnerConnection createConnectorConfig() throws ConnectionException
    {
        ConnectorConfig partnerConfig = new ConnectorConfig();
        partnerConfig.setUsername(pluginTask.getUsername());
        partnerConfig.setPassword(pluginTask.getPassword() + pluginTask.getSecurityToken());
        partnerConfig.setAuthEndpoint(pluginTask.getAuthEndPoint() + pluginTask.getApiVersion());
        return new PartnerConnection(partnerConfig);
    }

    private String getWaveQueryPath()
    {
        StringBuilder waveQueryPath = new StringBuilder();
        waveQueryPath.append(SERVICE_PATH);
        waveQueryPath.append("v");
        waveQueryPath.append(pluginTask.getApiVersion());
        waveQueryPath.append(PATH_WAVE_QUERY);
        return waveQueryPath.toString();
    }

    private URI getRequestURI(PartnerConnection connection, String path, String query) throws URISyntaxException
    {
        URI seURI = new URI(connection.getConfig().getServiceEndpoint());
        return new URI(seURI.getScheme(), seURI.getUserInfo(), seURI.getHost(), seURI.getPort(), path, query, null);
    }

    public InputStream post(URI uri, String sessionId, String request) throws UnsupportedOperationException, IOException
    {
        StringEntity entity = new StringEntity(request, STR_UTF_8);
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        httpPost.setConfig(getRequestConfig());

        httpPost.addHeader(HEADER_AUTH, HEADER_OAUTH + sessionId);
        return execute(httpPost);
    }

    public String get(URI uri, String sessionId) throws UnsupportedOperationException, IOException
    {
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(getRequestConfig());
        httpGet.addHeader(HEADER_AUTH, HEADER_OAUTH + sessionId);
        httpGet.addHeader(HEADER_ACCEPT, CONTENT_TYPE_APPLICATION_JSON);

        InputStream is = execute(httpGet);
        String response = IOUtils.toString(is, STR_UTF_8);
        logger.info(response);
        return response;
}

    private RequestConfig getRequestConfig()
    {
        int timeout = Integer.parseInt(System.getProperty(SYS_PROPERTY_SOCKET_TIMEOUT, pluginTask.getConnectionTimeout()));
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
            .setConnectionRequestTimeout(timeout).build();
        return requestConfig;
    }

    private InputStream execute(HttpUriRequest httpReq) throws UnsupportedOperationException, IOException
    {
        logger.info(String.format("[request] %s", httpReq.getURI().getPath()));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpReq);

        int statusCode = response.getStatusLine().getStatusCode();
        if (!(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED)) {
            String reasonPhrase = response.getStatusLine().getReasonPhrase();
            String errResponse = IOUtils.toString(response.getEntity().getContent(), STR_UTF_8);
            throw new ConfigException(String.format("%s:%s", reasonPhrase,errResponse));
        }

        HttpEntity responseEntity = response.getEntity();
        return responseEntity.getContent();
    }

    private String getDatasetsQueryPath() {
        StringBuilder waveQueryPath = new StringBuilder();
        waveQueryPath.append(SERVICE_PATH);
        waveQueryPath.append("v");
        waveQueryPath.append(pluginTask.getApiVersion());
        waveQueryPath.append(PATH_WAVE_DATASETS);
        waveQueryPath.append(pluginTask.getDatasetId());
        return waveQueryPath.toString();
    }
}