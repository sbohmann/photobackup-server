package at.yeoman.photobackup.server.assets;

import at.yeoman.photobackup.server.primtive.ByteBlock;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

class ChecksumDeserializer extends JsonDeserializer<Checksum> {
    @Override
    public Checksum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        return new Checksum(new ByteBlock(node.asText()));
    }
}
