package y.w.weathertracker.bdd;

import y.w.weathertracker.statistics.Statistic;
import y.w.weathertracker.statistics.model.AggregateResult;
import y.w.weathertracker.typeregistry.MultiValueMapDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These are the test definitions for the following features
 * - 01-get-stats.feature
 */
@Slf4j
public class GetStatStepDefs
{
    @Autowired
    private StepDefsHelper helper;

    /////////////////////////////////////////////////////////
    // Step Definitions for 01-get-state.feature
    /////////////////////////////////////////////////////////

    List<AggregateResult> statsList = null;
    String queryResult;
    ObjectMapper mapper = new ObjectMapper();

    @When("I get stats with parameters:")
    public void i_get_stats_with_parameters(MultiValueMapDTO params) throws IOException
    {
        try
        {
              // Get it as JSON string
//            URI uri = helper.buildStatsRequestUri(dataTable.asLists());
//            ResponseEntity<String> statRes = helper.getRestTemplate().getForEntity(uri, String.class);
//            queryResult = statRes.getBody();
//            int responseCode = statRes.getStatusCodeValue();
//            statsList = mapper.readValue(queryResult, new TypeReference<List<AggregateResult>>(){});
//
//            helper.setResultSet(responseCode, null);

            // Try another way
            ResponseEntity<List<AggregateResult>> statRes = helper.getRestTemplate()
                    .exchange(helper.buildStatsRequestUri(params.getMap()),
                            HttpMethod.GET,
                            HttpEntity.EMPTY,
                            new ParameterizedTypeReference<List<AggregateResult>>() {}
                    );
            statsList = statRes.getBody();
            helper.setResultSet(statRes.getStatusCodeValue(), null);
        }
        catch (HttpClientErrorException e)
        {
            log.error(e.getMessage());
        }
    }


    @Then("the response body is an array of:")
    public void the_response_body_is_an_array_of(DataTable dataTable) {
        List<AggregateResult> expected = helper.convertToListOfAggregate(dataTable.asLists());

        for (AggregateResult a:statsList)
        {
            boolean found = false;
            for (AggregateResult e : expected)
            {
                if (e.equals(a))
                {
                    found = true;
                    break;
                }
                // float data comparison of calculated value...give it a chance.
                else if (a.getStatistic() == Statistic.AVERAGE)
                {
                    if (Math.abs(a.getValue() - e.getValue()) < 0.1)
                    {
                        found = true;
                        break;
                    }
                }

            }
            assertThat(found).isTrue();
        }
    }

    @Then("the response body is an empty array")
    public void the_response_body_is_an_empty_array() {
        assertThat(statsList).isEmpty();
    }
}