package y.w.weathertracker.bdd;

import y.w.weathertracker.measurements.model.Measurement;
import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.model.AggregateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * These are the test definitions for the following features
 * - 01-add-measurement.feature
 * - 01-get-stats.feature
 * - 02-get-measurement.feature
 *
 * We store all the step definitions in one Java class to avoid the duplicate steps, so
 * that I don't need to modifu the feature files.
 *
 */
@Slf4j
public class StepDefsHelper
{
    private static final String URI_SCHEME           = "http";
    private static final String URI_HOST             = "localhost";
    private static final int    URI_PORT             = 8000; // this is what HackerRank expects
    private static final String BASE_URL             = URI_SCHEME + "://" + URI_HOST + ":" + URI_PORT;
    private static final String STATS_PATH           = "/stats";
    private static final String BASE_MEASUREMENT_URL = BASE_URL + "/measurements";
    private static final String BASE_STATISTICS_URL  = BASE_URL + STATS_PATH;

    private TestRestTemplate restTemplate;

    private ResponseEntity<?> responseEntity = null;

    private Measurement measurement;
    private ResultSet   resultSet;

    public TestRestTemplate getRestTemplate() {

        return restTemplate !=null ? restTemplate : new TestRestTemplate();
    }

    public void setRestTemplate(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> invokeRESTCall(String url, HttpMethod method, HttpEntity<?> requestEntity){

        return getRestTemplate().exchange(url,method,requestEntity, String.class);
    }

    public static String getBaseMeasurementUrl()
    {
        return BASE_MEASUREMENT_URL;
    }

    public static String getBaseStatisticsUrl()
    {
        return BASE_STATISTICS_URL;
    }

    public HttpHeaders getDefaultHttpHeaders(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }


    public String buildUrl(String host, String port, String path, Map<String,String> uriVariables, MultiValueMap<String,String> queryParams){
        UriComponentsBuilder builder=UriComponentsBuilder.fromPath(path)
                .host(host)
                .port(port)
                .scheme("http");
        if(queryParams !=null && !queryParams.isEmpty()) builder.queryParams(queryParams);
        UriComponents uriComponent= uriVariables !=null && !uriVariables.isEmpty() ?  builder.buildAndExpand(uriVariables) : builder.build();

        return uriComponent.toUri().toString();
    }

    public String buildUrl(String host,String port,String path){

        return buildUrl(host, port, path,null,null);
    }

    public String buildUrl(String host,String port,String path,Map<String,String> uriVariables){

        return buildUrl(host, port, path,uriVariables,null);
    }

    public ResponseEntity<?> getResponseEntity()
    {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<?> responseEntity)
    {
        this.responseEntity = responseEntity;
    }

    public Measurement getMeasurement()
    {
        return measurement;
    }

    public void setMeasurement(Measurement measurement)
    {
        this.measurement = measurement;
    }

    public ResultSet getResultSet()
    {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet)
    {
        this.resultSet = resultSet;
    }

    public void setResultSet(int responseCode, ResponseEntity<?> responseEntity)
    {
        this.resultSet = new ResultSet(responseCode, responseEntity);
    }

    /**
     * Some data in the features has leading and trailing quotes. We got to remove it.
     *
     * @param s
     * @return
     */
    public static String stripLeadingTrailingQuote(String s)
    {
        if (s.startsWith("\"") || s.startsWith("'"))
            s = s.substring(1);

        if (s.endsWith("\"") || s.endsWith("'"))
            s = s.substring(0, s.length() - 1);
        return s;
    }

    public ResultSet postMeasurement(TestRestTemplate restTemplate, URI uri, String json)
    {
        int responseCode;
        ResponseEntity<String> re = null;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<String>(json, httpHeaders);

        try
        {
            re = restTemplate.postForEntity(uri, httpEntity, String.class);
            responseCode = re.getStatusCodeValue();
        }
        catch (HttpClientErrorException | HttpServerErrorException e)
        {
            responseCode = e.getStatusCode().value();
        }
        return new ResultSet(responseCode, re);
    }

    /**
     * The parameters come in as List<List<String>>, [n, 2]. The first row is header which is not useful.
     * Like
     *
     *       | param        | value                    |
     *       | stat         | min                      |
     *       | stat         | max                      |
     *       | stat         | average                  |
     *       | metric       | temperature              |
     *       | fromDateTime | 2015-09-01T16:00:00.000Z |
     *       | toDateTime   | 2015-09-01T17:00:00.000Z |
     *
     * @return a URI of pattern http://localhost:8000/stats?metric=temperature&stat=MIN,MAX&fromDateTime=2015-09-01T16:00:00.000Z&toDateTime=2015-09-01T16:00:00.000Z
     */
    public URI buildStatsRequestUri(List<List<String>> params)
    {
        List<String> statParams = params.stream().filter(l -> l.get(0).equals("stat")).map(l -> l.get(1).toUpperCase()).collect(
                Collectors.toList());
        List<String> metricParams = params.stream().filter(l -> l.get(0).equals("metric")).map(l -> l.get(1)).collect(Collectors.toList());
        String fromParam = params.stream().filter(l -> l.get(0).equals("fromDateTime")).map(l -> l.get(1)).collect(Collectors.toList()).get(0);
        String toParam = params.stream().filter(l -> l.get(0).equals("toDateTime")).map(l -> l.get(1)).collect(Collectors.toList()).get(0);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(URI_SCHEME)
                .host(URI_HOST)
                .port(URI_PORT)
                .path(STATS_PATH)
                .queryParam("metric", StringUtils.join(metricParams, ","))
                .queryParam("stat", StringUtils.join(statParams, ","))
                .queryParam("fromDateTime", fromParam)
                .queryParam("toDateTime", toParam)
                .build();

        return uriComponents.toUri();
    }

    public URI buildStatsRequestUri(MultiValueMap<String, String> params)
    {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(URI_SCHEME)
                .host(URI_HOST)
                .port(URI_PORT)
                .path(STATS_PATH)
                .queryParam("metric", StringUtils.join(params.get("metric"), ","))
                .queryParam("stat", StringUtils.join(params.get("stat"), ","))
                .queryParam("fromDateTime", params.get("fromDateTime").get(0))
                .queryParam("toDateTime", params.get("toDateTime").get(0))
                .build();

        return uriComponents.toUri();
    }

    /**
     * Convert table like below to a list of AggregateResult.
     *       | metric        | stat      | value |
     *       | "temperature" | "min"     | 27.1  |
     *       | "temperature" | "max"     | 27.5  |
     *       | "temperature" | "average" | 27.3  |
     *
     * @param data
     * @return
     */
    public List<AggregateResult> convertToListOfAggregate(List<List<String>> data)
    {
        List<AggregateResult> results = new ArrayList<>();

        for (int i=1; i<data.size(); i++)
        {
            AggregateResult a = new AggregateResult();
            List<String> row = data.get(i);
            a.setMetric(stripLeadingTrailingQuote(row.get(0)));
            a.setStatistic(Statistic.valueOf(stripLeadingTrailingQuote(row.get(1).toUpperCase())));
            a.setValue(Double.parseDouble(stripLeadingTrailingQuote(row.get(2))));

            results.add(a);
        }

        return results;
    }

    public static class ResultSet
    {
        public int responseCode;
        public ResponseEntity<?> re;

        public ResultSet(int responseCode, ResponseEntity<?> re)
        {
            this.responseCode = responseCode;
            this.re = re;
        }
    }
}