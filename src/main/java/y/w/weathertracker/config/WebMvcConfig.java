package y.w.weathertracker.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * JavaConfig
 */
@Configuration
@EnableSwagger2
public class WebMvcConfig extends WebMvcConfigurationSupport
{
    /**
     * The following disables Pathmatcher to match extension GLOBALLY. It's not necessary to do
     * so if endpont approach is preferred.
     *
     * I.e., endpoint approach: using regexp.
     *
     *     @GetMapping("/{timestamp:.+}")
     *     public ResponseEntity<Measurement> getMeasurement(@PathVariable ZonedDateTime timestamp)
     */

    /**
     * It's by default that JSON is used though.
     *
     * @param configurer
     */
    @Override public void configureContentNegotiation(final ContentNegotiationConfigurer configurer)
    {
        configurer.favorPathExtension(false).
                favorParameter(true).
                parameterName("mediaType").
                ignoreAcceptHeader(true).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("json", MediaType.APPLICATION_JSON);
    }

    /**
     * Path suffix matching is disabled. This will make sure the timestamp passed in the URL like below will be parsed correctly.
     * Otherwise, ".000Z" will be removed when the timestamp is finally passed to ZonedDateTime parser.
     *
     * Reference: org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#setUseRegisteredSuffixPatternMatch(boolean)
     *
     * http://localhost:8000/measurements/2015-09-01T16:00:00.000Z
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }

    /**
     * SwaggerConfig: enables http://localhost:8000/swagger-ui.html
     *
     * @return
     */
    @Bean
    public Docket measurementApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Measurements")
                .select()
                .apis(RequestHandlerSelectors.basePackage("y.w.weathertracker.measurements.controller"))
                .paths((Predicate<String>) regex("/measurements.*"))
                .build()
                .apiInfo(metaData());
    }

    @Bean
    public Docket statsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Statistics")
                .select()
                .apis(RequestHandlerSelectors.basePackage("y.w.weathertracker.statistics.controller"))
                .paths((Predicate<String>) regex("/stats.*"))
                .build()
                .apiInfo(metaData());
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    private ApiInfo metaData()
    {
        return new ApiInfoBuilder()
                .title("Weathertracker REST API")
                .description("\"Weathertracker REST API for Weather Information\"")
                .version("1.0.0").license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                .contact(new Contact("Yang Wang", "https://www.wy8162.com/about/", "wy8162@gmail.com"))
                .build();
    }
}