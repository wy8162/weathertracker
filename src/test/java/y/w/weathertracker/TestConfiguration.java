package y.w.weathertracker;

import y.w.weathertracker.bdd.StepDefsHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TestConfiguration
 *
 * @author ywang
 * @date 8/9/2019
 */
@Configuration
public class TestConfiguration
{
    @Bean
    public StepDefsHelper helper()
    {
        return new StepDefsHelper();
    }
}
