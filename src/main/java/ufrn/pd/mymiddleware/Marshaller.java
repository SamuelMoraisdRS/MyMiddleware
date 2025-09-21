package ufrn.pd.mymiddleware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Marshaller {
    private final ObjectMapper mapper = new ObjectMapper();
    public String marshal(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public <T> T unmarshal(String payload, Class<T> clazz) {
        return mapper.convertValue(payload, clazz);
    }

}
