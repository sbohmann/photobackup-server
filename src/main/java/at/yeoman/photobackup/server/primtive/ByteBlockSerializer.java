package at.yeoman.photobackup.server.primtive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

class ByteBlockSerializer extends JsonSerializer<ByteBlock> {
    @Override
    public void serialize(ByteBlock value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toJson());
    }
}
