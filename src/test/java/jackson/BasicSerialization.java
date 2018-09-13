package jackson;

import at.yeoman.photobackup.server.api.AssetReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

public class BasicSerialization {
    @Test
    public void deserializeAssetReport() throws IOException {
        String json = "{\"descriptions\":[{\"name\":\"hello\"}]}";
        System.out.println(json);
        AssetReport result = new ObjectMapper().readValue(json, AssetReport.class);
        System.out.println(result);
    }
}
