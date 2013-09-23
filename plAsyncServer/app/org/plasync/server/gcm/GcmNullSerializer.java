package org.plasync.server.gcm;

// Configuration of ObjectMapper:

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

// serialization as done using regular ObjectMapper.writeValue()

// and NullSerializer can be something as simple as:
public class GcmNullSerializer extends JsonSerializer<Object>
{
    public void serialize(Object value, JsonGenerator jgen,
                          SerializerProvider provider)
            throws IOException, JsonProcessingException
    {
        // any JSON value you want...
        jgen.writeString("");
    }
}