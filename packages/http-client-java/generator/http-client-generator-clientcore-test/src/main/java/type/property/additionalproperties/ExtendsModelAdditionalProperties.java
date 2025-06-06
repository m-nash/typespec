package type.property.additionalproperties;

import io.clientcore.core.annotations.Metadata;
import io.clientcore.core.annotations.MetadataProperties;
import io.clientcore.core.serialization.json.JsonReader;
import io.clientcore.core.serialization.json.JsonSerializable;
import io.clientcore.core.serialization.json.JsonToken;
import io.clientcore.core.serialization.json.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The model extends from Record&lt;ModelForRecord&gt; type.
 */
@Metadata(properties = { MetadataProperties.FLUENT })
public final class ExtendsModelAdditionalProperties implements JsonSerializable<ExtendsModelAdditionalProperties> {
    /*
     * The knownProp property.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private final ModelForRecord knownProp;

    /*
     * The model extends from Record<ModelForRecord> type.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    private Map<String, ModelForRecord> additionalProperties;

    /**
     * Creates an instance of ExtendsModelAdditionalProperties class.
     * 
     * @param knownProp the knownProp value to set.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public ExtendsModelAdditionalProperties(ModelForRecord knownProp) {
        this.knownProp = knownProp;
    }

    /**
     * Get the knownProp property: The knownProp property.
     * 
     * @return the knownProp value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public ModelForRecord getKnownProp() {
        return this.knownProp;
    }

    /**
     * Get the additionalProperties property: The model extends from Record&lt;ModelForRecord&gt; type.
     * 
     * @return the additionalProperties value.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public Map<String, ModelForRecord> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * Set the additionalProperties property: The model extends from Record&lt;ModelForRecord&gt; type.
     * 
     * @param additionalProperties the additionalProperties value to set.
     * @return the ExtendsModelAdditionalProperties object itself.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public ExtendsModelAdditionalProperties setAdditionalProperties(Map<String, ModelForRecord> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeJsonField("knownProp", this.knownProp);
        if (additionalProperties != null) {
            for (Map.Entry<String, ModelForRecord> additionalProperty : additionalProperties.entrySet()) {
                jsonWriter.writeUntypedField(additionalProperty.getKey(), additionalProperty.getValue());
            }
        }
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of ExtendsModelAdditionalProperties from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of ExtendsModelAdditionalProperties if the JsonReader was pointing to an instance of it, or
     * null if it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the ExtendsModelAdditionalProperties.
     */
    @Metadata(properties = { MetadataProperties.GENERATED })
    public static ExtendsModelAdditionalProperties fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            ModelForRecord knownProp = null;
            Map<String, ModelForRecord> additionalProperties = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("knownProp".equals(fieldName)) {
                    knownProp = ModelForRecord.fromJson(reader);
                } else {
                    if (additionalProperties == null) {
                        additionalProperties = new LinkedHashMap<>();
                    }

                    additionalProperties.put(fieldName, ModelForRecord.fromJson(reader));
                }
            }
            ExtendsModelAdditionalProperties deserializedExtendsModelAdditionalProperties
                = new ExtendsModelAdditionalProperties(knownProp);
            deserializedExtendsModelAdditionalProperties.additionalProperties = additionalProperties;

            return deserializedExtendsModelAdditionalProperties;
        });
    }
}
