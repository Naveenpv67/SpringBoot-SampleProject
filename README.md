import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.lang.reflect.Field;

public class IgnoreValueSerializer<T> extends JsonSerializer<T> {
    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        // Use reflection to iterate through the fields
        for (Field field : value.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object fieldValue = field.get(value);

                // Only include the field if its value is not "ignoreValue"
                if (fieldValue == null || !fieldValue.equals("ignoreValue")) {
                    gen.writeObjectField(field.getName(), fieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        gen.writeEndObject();
    }


@JsonSerialize(using = IgnoreValueSerializer.class)
public class BankDetailsDTO {
    private String bankCode;
    private String channel;
    private String serviceCode;

    // Getters and setters
}


    
