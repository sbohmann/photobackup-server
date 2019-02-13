package at.yeoman.photobackup.server.assets;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

class ChecksumSerializer extends JsonSerializer<Checksum> {
    @Override
    public void serialize(Checksum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toJson());
    }
}
