import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.JsonWriteContext;

import java.util.ArrayList;


/*
 * JSON SERIALIZER AND DESERIALIZER
 */
public class WriteJsonObject {
    private final ObjectMapper objMap = new ObjectMapper();

    /**
     * Serializer, (object -> json)
     */
    public String serialize(Object Object) {
        try {
            return this.objMap.writeValueAsString(Object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deserializer, (json -> object)
     */
    public <T> T deserialize(String json, Class<T> obj) {
        try {
            return this.objMap.readValue(json, obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deserializer using TypeReference rather than class, (json -> object)
     */
    public  <T> T deserialize(String json, TypeReference<T> obj){
        try {
            return this.objMap.readValue(json, obj);
        } catch (Exception e) {
            return null;
        }
    }
}
