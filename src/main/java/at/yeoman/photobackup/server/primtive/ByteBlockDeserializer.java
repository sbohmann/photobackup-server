package at.yeoman.photobackup.server.primtive;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

class ByteBlockDeserializer extends JsonDeserializer<ByteBlock> {
    @Override
    public ByteBlock deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new ByteBlock(p.getText());
    }
}
