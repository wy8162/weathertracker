package y.w.weathertracker.typeregistry;

import y.w.weathertracker.bdd.StepDefsHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Matrix
 */
@Getter
@Setter
public class JsonDTO
{
    private static ObjectMapper objectMapper = new ObjectMapper();

    private List<String> listOfJson = new ArrayList<>();

    public JsonDTO(List<Map<String, String>> rows) throws IOException
    {
        for (Map<String, String> m : rows)
        {
            Map<String, String> map = new HashMap<>();
            for (String k : m.keySet())
            {
                map.put(k, StepDefsHelper.stripLeadingTrailingQuote(m.get(k)));
            }

            StringWriter stringWriter = new StringWriter();
            objectMapper.writeValue(stringWriter, map);
            listOfJson.add(stringWriter.toString());
        }
    }
}
