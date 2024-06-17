// <auto-generated/>

#nullable disable

using System;
using System.ClientModel.Primitives;
using System.Collections.Generic;
using System.Text.Json;

namespace UnbrandedTypeSpec.Models
{
    /// <summary></summary>
    public partial class RoundTripModel : IJsonModel<RoundTripModel>
    {
        private IDictionary<string, BinaryData> _serializedAdditionalRawData;

        internal RoundTripModel(string requiredString, int requiredInt, IList<StringFixedEnum?> requiredCollection, IDictionary<string, StringExtensibleEnum?> requiredDictionary, Thing requiredModel, IntExtensibleEnum intExtensibleEnum, IList<IntExtensibleEnum> intExtensibleEnumCollection, FloatExtensibleEnum floatExtensibleEnum, FloatExtensibleEnumWithIntValue floatExtensibleEnumWithIntValue, IList<FloatExtensibleEnum> floatExtensibleEnumCollection, FloatFixedEnum floatFixedEnum, FloatFixedEnumWithIntValue floatFixedEnumWithIntValue, IList<FloatFixedEnum> floatFixedEnumCollection, IntFixedEnum intFixedEnum, IList<IntFixedEnum> intFixedEnumCollection, StringFixedEnum? stringFixedEnum, BinaryData requiredUnknown, BinaryData optionalUnknown, IDictionary<string, BinaryData> requiredRecordUnknown, IDictionary<string, BinaryData> optionalRecordUnknown, IDictionary<string, BinaryData> readOnlyRequiredRecordUnknown, IDictionary<string, BinaryData> readOnlyOptionalRecordUnknown, ModelWithRequiredNullableProperties modelWithRequiredNullable, BinaryData requiredBytes, IDictionary<string, BinaryData> serializedAdditionalRawData)
        {
            RequiredString = requiredString;
            RequiredInt = requiredInt;
            RequiredCollection = requiredCollection;
            RequiredDictionary = requiredDictionary;
            RequiredModel = requiredModel;
            IntExtensibleEnum = intExtensibleEnum;
            IntExtensibleEnumCollection = intExtensibleEnumCollection;
            FloatExtensibleEnum = floatExtensibleEnum;
            FloatExtensibleEnumWithIntValue = floatExtensibleEnumWithIntValue;
            FloatExtensibleEnumCollection = floatExtensibleEnumCollection;
            FloatFixedEnum = floatFixedEnum;
            FloatFixedEnumWithIntValue = floatFixedEnumWithIntValue;
            FloatFixedEnumCollection = floatFixedEnumCollection;
            IntFixedEnum = intFixedEnum;
            IntFixedEnumCollection = intFixedEnumCollection;
            StringFixedEnum = stringFixedEnum;
            RequiredUnknown = requiredUnknown;
            OptionalUnknown = optionalUnknown;
            RequiredRecordUnknown = requiredRecordUnknown;
            OptionalRecordUnknown = optionalRecordUnknown;
            ReadOnlyRequiredRecordUnknown = readOnlyRequiredRecordUnknown;
            ReadOnlyOptionalRecordUnknown = readOnlyOptionalRecordUnknown;
            ModelWithRequiredNullable = modelWithRequiredNullable;
            RequiredBytes = requiredBytes;
            _serializedAdditionalRawData = serializedAdditionalRawData;
        }

        internal RoundTripModel()
        {
        }

        void IJsonModel<RoundTripModel>.Write(Utf8JsonWriter writer, ModelReaderWriterOptions options)
        {
        }

        RoundTripModel IJsonModel<RoundTripModel>.Create(ref Utf8JsonReader reader, ModelReaderWriterOptions options)
        {
            return new RoundTripModel();
        }

        BinaryData IPersistableModel<RoundTripModel>.Write(ModelReaderWriterOptions options)
        {
            return new BinaryData("IPersistableModel");
        }

        RoundTripModel IPersistableModel<RoundTripModel>.Create(BinaryData data, ModelReaderWriterOptions options)
        {
            return new RoundTripModel();
        }

        string IPersistableModel<RoundTripModel>.GetFormatFromOptions(ModelReaderWriterOptions options) => "J";
    }
}
