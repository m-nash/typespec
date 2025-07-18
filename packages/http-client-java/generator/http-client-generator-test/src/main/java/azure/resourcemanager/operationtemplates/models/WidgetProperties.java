// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package azure.resourcemanager.operationtemplates.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * The WidgetProperties model.
 */
@Fluent
public final class WidgetProperties implements JsonSerializable<WidgetProperties> {
    /*
     * The name of the widget.
     */
    private String name;

    /*
     * The description of the widget.
     */
    private String description;

    /*
     * The provisioning state of the widget.
     */
    private String provisioningState;

    /**
     * Creates an instance of WidgetProperties class.
     */
    public WidgetProperties() {
    }

    /**
     * Get the name property: The name of the widget.
     * 
     * @return the name value.
     */
    public String name() {
        return this.name;
    }

    /**
     * Set the name property: The name of the widget.
     * 
     * @param name the name value to set.
     * @return the WidgetProperties object itself.
     */
    public WidgetProperties withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the description property: The description of the widget.
     * 
     * @return the description value.
     */
    public String description() {
        return this.description;
    }

    /**
     * Set the description property: The description of the widget.
     * 
     * @param description the description value to set.
     * @return the WidgetProperties object itself.
     */
    public WidgetProperties withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get the provisioningState property: The provisioning state of the widget.
     * 
     * @return the provisioningState value.
     */
    public String provisioningState() {
        return this.provisioningState;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("name", this.name);
        jsonWriter.writeStringField("description", this.description);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of WidgetProperties from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of WidgetProperties if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IOException If an error occurs while reading the WidgetProperties.
     */
    public static WidgetProperties fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            WidgetProperties deserializedWidgetProperties = new WidgetProperties();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("name".equals(fieldName)) {
                    deserializedWidgetProperties.name = reader.getString();
                } else if ("description".equals(fieldName)) {
                    deserializedWidgetProperties.description = reader.getString();
                } else if ("provisioningState".equals(fieldName)) {
                    deserializedWidgetProperties.provisioningState = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedWidgetProperties;
        });
    }
}
