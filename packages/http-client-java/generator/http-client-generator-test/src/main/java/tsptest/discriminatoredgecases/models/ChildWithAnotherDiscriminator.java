// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package tsptest.discriminatoredgecases.models;

import com.azure.core.annotation.Generated;
import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * The ChildWithAnotherDiscriminator model.
 */
@Immutable
public class ChildWithAnotherDiscriminator extends ParentWithRequiredProperty {
    /*
     * Discriminator property for ChildWithAnotherDiscriminator.
     */
    @Generated
    private String differentDiscriminator = "ChildWithAnotherDiscriminator";

    /*
     * The yetAnotherProperty property.
     */
    @Generated
    private final String yetAnotherProperty;

    /**
     * Creates an instance of ChildWithAnotherDiscriminator class.
     * 
     * @param discriminator the discriminator value to set.
     * @param aProperty the aProperty value to set.
     * @param yetAnotherProperty the yetAnotherProperty value to set.
     */
    @Generated
    protected ChildWithAnotherDiscriminator(String discriminator, String aProperty, String yetAnotherProperty) {
        super(discriminator, aProperty);
        this.yetAnotherProperty = yetAnotherProperty;
    }

    /**
     * Get the differentDiscriminator property: Discriminator property for ChildWithAnotherDiscriminator.
     * 
     * @return the differentDiscriminator value.
     */
    @Generated
    public String getDifferentDiscriminator() {
        return this.differentDiscriminator;
    }

    /**
     * Get the yetAnotherProperty property: The yetAnotherProperty property.
     * 
     * @return the yetAnotherProperty value.
     */
    @Generated
    public String getYetAnotherProperty() {
        return this.yetAnotherProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Generated
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("discriminator", getDiscriminator());
        jsonWriter.writeStringField("aProperty", getAProperty());
        jsonWriter.writeStringField("yetAnotherProperty", this.yetAnotherProperty);
        jsonWriter.writeStringField("differentDiscriminator", this.differentDiscriminator);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of ChildWithAnotherDiscriminator from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of ChildWithAnotherDiscriminator if the JsonReader was pointing to an instance of it, or null
     * if it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the ChildWithAnotherDiscriminator.
     */
    @Generated
    public static ChildWithAnotherDiscriminator fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            String discriminatorValue = null;
            try (JsonReader readerToUse = reader.bufferObject()) {
                readerToUse.nextToken(); // Prepare for reading
                while (readerToUse.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = readerToUse.getFieldName();
                    readerToUse.nextToken();
                    if ("differentDiscriminator".equals(fieldName)) {
                        discriminatorValue = readerToUse.getString();
                        break;
                    } else {
                        readerToUse.skipChildren();
                    }
                }
                // Use the discriminator value to determine which subtype should be deserialized.
                if ("anotherValue".equals(discriminatorValue)) {
                    return GrandChildWithAnotherDiscriminator.fromJson(readerToUse.reset());
                } else {
                    return fromJsonKnownDiscriminator(readerToUse.reset());
                }
            }
        });
    }

    @Generated
    static ChildWithAnotherDiscriminator fromJsonKnownDiscriminator(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            String discriminator = null;
            String aProperty = null;
            String yetAnotherProperty = null;
            String differentDiscriminator = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("discriminator".equals(fieldName)) {
                    discriminator = reader.getString();
                } else if ("aProperty".equals(fieldName)) {
                    aProperty = reader.getString();
                } else if ("yetAnotherProperty".equals(fieldName)) {
                    yetAnotherProperty = reader.getString();
                } else if ("differentDiscriminator".equals(fieldName)) {
                    differentDiscriminator = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            ChildWithAnotherDiscriminator deserializedChildWithAnotherDiscriminator
                = new ChildWithAnotherDiscriminator(discriminator, aProperty, yetAnotherProperty);
            deserializedChildWithAnotherDiscriminator.differentDiscriminator = differentDiscriminator;

            return deserializedChildWithAnotherDiscriminator;
        });
    }
}
