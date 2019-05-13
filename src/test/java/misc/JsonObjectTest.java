package misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

public class JsonObjectTest {

    @Test
    public void testMultipleAddsToJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("test", "test");

        if(jsonObject.has("test")) {
            JsonPrimitive intermediate = jsonObject.getAsJsonPrimitive("test");

            jsonObject.addProperty("test", intermediate.getAsString() + ", test2");
        }

        System.out.println(jsonObject.toString());
    }
}
