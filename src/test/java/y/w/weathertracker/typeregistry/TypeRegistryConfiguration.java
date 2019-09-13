package y.w.weathertracker.typeregistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;
import io.cucumber.datatable.TableTransformer;
import io.cucumber.datatable.TypeReference;
import org.springframework.util.MultiValueMap;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.ENGLISH;

/**
 * Cucumber TypeRegistryConfiguration
 */
public class TypeRegistryConfiguration implements TypeRegistryConfigurer
{
    ObjectMapper objectMapper = new ObjectMapper();

    @Override public Locale locale()
    {
        return ENGLISH;
    }

    /**
     * Map a matrix to List<List<String>>
     *
     *   Like
     *  | param        | value                    |
     *  | stat         | min                      |
     *  | stat         | max                      |
     *  | stat         | average                  |
     *  | metric       | temperature              |
     *  | metric       | dewPoint                 |
     *  | metric       | precipitation            |
     *  | fromDateTime | 2015-09-01T16:00:00.000Z |
     *  | toDateTime   | 2015-09-01T17:00:00.000Z |
     *
     * @param typeRegistry
     */
    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry)
    {
        typeRegistry.defineDataTableType(
                new DataTableType(
                        JsonDTO.class,
                        (TableTransformer<JsonDTO>) table ->
                                new JsonDTO(table.asMaps())));

        typeRegistry.defineDataTableType(
                new DataTableType(
                        MultiValueMapDTO.class,
                        (TableTransformer<MultiValueMapDTO>) table ->
                                new MultiValueMapDTO(table.asLists())));

        JacksonTableTransformer transformer = new JacksonTableTransformer();

        typeRegistry.setDefaultDataTableCellTransformer(transformer);
        typeRegistry.setDefaultDataTableEntryTransformer(transformer);
        typeRegistry.setDefaultParameterTransformer(transformer);
    }

    private static final class JacksonTableTransformer implements
            ParameterByTypeTransformer,
            TableEntryByTypeTransformer,
            TableCellByTypeTransformer
    {
        private final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Map DataTable to list of Java objects. This has to use @Given annotation instead of Java 8 lambda.
         *
         * I.e.
         *
         * | timestamp                  | temperature | dewPoint | precipitation |
         * | "2015-09-01T16:00:00.000Z" | 27.1        | 16.7     | 0             |
         *
         * Can be mapped to List<Sales>
         *
         * @param entry
         * @param type
         * @param cellTransformer
         * @param <T>
         * @return
         */
        @Override
        public <T> T transform(Map<String, String> entry, Class<T> type, TableCellByTypeTransformer cellTransformer)
        {
            return objectMapper.convertValue(entry, type);
        }

        @Override
        public <T> T transform(String value, Class<T> cellType) throws Throwable
        {
            return objectMapper.readValue(new StringReader(value), cellType);
        }

        /**
         * Maps a string of JSON data to a Java object.
         *
         * @param s a string or an instance of type.
         * @param type the class type of the target object. It can be String or any class. A string will be returned if it is String.
         * @return
         * @throws Throwable
         */
        @Override public Object transform(String s, Type type) throws Throwable
        {
            if (type.equals(String.class))
            {
                return s;
            }
            @SuppressWarnings("unchecked")
            Class<?> clazz = (Class<?>) type;
            Object o = objectMapper.readValue(new StringReader(s), clazz);
            return o;
        }
    }
}

