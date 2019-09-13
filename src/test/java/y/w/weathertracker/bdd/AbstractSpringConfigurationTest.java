package y.w.weathertracker.bdd;

import y.w.weathertracker.WeatherTrackerApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * AbstractSpringConfigurationTest
 *
 * @author ywang
 * @date 8/9/2019
 */
@SpringBootTest(classes = WeatherTrackerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration
@DirtiesContext
public class AbstractSpringConfigurationTest
{
}
