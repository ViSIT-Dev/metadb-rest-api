package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public class ObjectRepository {
    @Autowired
    private Anno4j anno4j;

    public JsonObject getRepresentationOfObject (@NonNull String id){
        JsonObject jsonObject = new JsonObject();
        return jsonObject;
    }
}
