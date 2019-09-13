package y.w.weathertracker.typeregistry;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * Convert a List<List<String>> to a MultiValueMap
 *
 * @author ywang
 * @date 8/14/2019
 */
@Getter
@Setter
public class MultiValueMapDTO
{
    private MultiValueMap<String, String> map;

    public MultiValueMapDTO(List<List<String>> list)
    {
        this.map = new LinkedMultiValueMap<>();
        for (List<String> l : list)
            this.map.add(l.get(0), l.get(1));
    }
}
