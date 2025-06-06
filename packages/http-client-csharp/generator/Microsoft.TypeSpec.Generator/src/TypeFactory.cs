// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

using System;
using System.Collections.Generic;
using System.IO;
using Microsoft.TypeSpec.Generator.Input;
using Microsoft.TypeSpec.Generator.Primitives;
using Microsoft.TypeSpec.Generator.Providers;

namespace Microsoft.TypeSpec.Generator
{
    public class TypeFactory
    {
        private ChangeTrackingListDefinition ChangeTrackingListProvider { get; } = new();

        private ChangeTrackingDictionaryDefinition ChangeTrackingDictionaryProvider { get; } = new();

        private Dictionary<InputModelType, ModelProvider?> CSharpToModelProvider { get; } = [];

        private Dictionary<EnumCacheKey, EnumProvider?> EnumCache { get; } = [];

        private Dictionary<InputType, CSharpType?> TypeCache { get; } = [];

        private Dictionary<InputModelProperty, PropertyProvider?> PropertyCache { get; } = [];

        private IReadOnlyList<LibraryVisitor> Visitors => CodeModelGenerator.Instance.Visitors;
        private Dictionary<InputType, IReadOnlyList<TypeProvider>> SerializationsCache { get; } = [];

        private Dictionary<InputLiteralType, InputType> LiteralValueTypeCache { get; } = [];

        internal HashSet<string> UnionVariantTypesToKeep { get; } = [];

        protected internal TypeFactory()
        {
        }

        public CSharpType? CreateCSharpType(InputType inputType)
        {
            if (TypeCache.TryGetValue(inputType, out var type))
            {
                return type;
            }

            type = CreateCSharpTypeCore(inputType);
            TypeCache.Add(inputType, type);
            return type;
        }

        protected virtual CSharpType? CreateCSharpTypeCore(InputType inputType)
        {
            CSharpType? type;
            switch (inputType)
            {
                case InputLiteralType literalType:
                    var input = CreateCSharpType(literalType.ValueType);
                    type = input != null ? CSharpType.FromLiteral(input, literalType.Value) : null;
                    break;
                case InputEnumTypeValue enumValueType:
                    // for enum value, we redirect to its corresponding enum type as a literal
                    var enumValue = CreateCSharpType(enumValueType.EnumType);
                    type = enumValue != null ? CSharpType.FromLiteral(enumValue, enumValueType.Value) : null;
                    break;
                case InputUnionType unionType:
                    var unionInputs = new List<CSharpType>();
                    foreach (var variant in unionType.VariantTypes)
                    {
                        var unionInput = CreateCSharpType(variant);
                        if (unionInput != null)
                        {
                            unionInputs.Add(unionInput);
                            // we only keep the type if it is not framework type and not literal
                            if (!unionInput.IsFrameworkType && !unionInput.IsLiteral)
                            {
                                UnionVariantTypesToKeep.Add(unionInput.Name);
                            }
                        }
                    }
                    type = CSharpType.FromUnion(unionInputs);
                    break;
                case InputArrayType listType:
                    var arrayInput = CreateCSharpType(listType.ValueType);
                    type = arrayInput != null ? new CSharpType(typeof(IList<>), arrayInput) : null;
                    break;
                case InputDictionaryType dictionaryType:
                    var inputValueType = CreateCSharpType(dictionaryType.ValueType);
                    type = inputValueType != null ? new CSharpType(typeof(IDictionary<,>), typeof(string), inputValueType) : null;
                    break;
                case InputEnumType enumType:
                    type = CreateEnum(enumType)?.Type;
                    break;
                case InputModelType modelType:
                    type = CreateModel(modelType)?.Type;
                    break;
                case InputNullableType nullableType:
                    type = CreateCSharpType(nullableType.Type)?.WithNullable(true);
                    break;
                default:
                    type = CreatePrimitiveCSharpTypeCore(inputType);
                    break;
            }

            return type;
        }

        /// <summary>
        /// Factory method for creating a <see cref="CSharpType"/> based on an input type <paramref name="inputType"/>.
        /// </summary>
        /// <param name="inputType">The <see cref="InputType"/> to convert.</param>
        /// <returns>An instance of <see cref="CSharpType"/>.</returns>
        internal static Type CreatePrimitiveCSharpTypeCore(InputType inputType) => inputType switch
        {
            InputPrimitiveType primitiveType => primitiveType.Kind switch
            {
                InputPrimitiveTypeKind.Boolean => typeof(bool),
                InputPrimitiveTypeKind.Bytes => typeof(BinaryData),
                InputPrimitiveTypeKind.PlainDate => typeof(DateTimeOffset),
                InputPrimitiveTypeKind.Decimal => typeof(decimal),
                InputPrimitiveTypeKind.Decimal128 => typeof(decimal),
                InputPrimitiveTypeKind.PlainTime => typeof(TimeSpan),
                InputPrimitiveTypeKind.Float32 => typeof(float),
                InputPrimitiveTypeKind.Float64 => typeof(double),
                InputPrimitiveTypeKind.Int8 => typeof(sbyte),
                InputPrimitiveTypeKind.UInt8 => typeof(byte),
                InputPrimitiveTypeKind.Int32 => typeof(int),
                InputPrimitiveTypeKind.Int64 => typeof(long),
                InputPrimitiveTypeKind.SafeInt => typeof(long),
                InputPrimitiveTypeKind.Integer => typeof(long), // in typespec, integer is the base type of int related types, see type relation: https://typespec.io/docs/language-basics/type-relations
                InputPrimitiveTypeKind.Float => typeof(double), // in typespec, float is the base type of float32 and float64, see type relation: https://typespec.io/docs/language-basics/type-relations
                InputPrimitiveTypeKind.Numeric => typeof(double), // in typespec, numeric is the base type of number types, see type relation: https://typespec.io/docs/language-basics/type-relations
                InputPrimitiveTypeKind.Stream => typeof(Stream),
                InputPrimitiveTypeKind.String => typeof(string),
                InputPrimitiveTypeKind.Url => typeof(Uri),
                InputPrimitiveTypeKind.Unknown => typeof(BinaryData),
                _ => typeof(object),
            },
            InputDateTimeType dateTimeType => typeof(DateTimeOffset),
            InputDurationType durationType => typeof(TimeSpan),
            _ => throw new InvalidOperationException($"Unknown type: {inputType}")
        };

        /// <summary>
        /// Factory method for creating a <see cref="TypeProvider"/> based on an <see cref="InputModelType"> <paramref name="model"/>.
        /// </summary>
        /// <param name="model">The <see cref="InputModelType"/> to convert.</param>
        /// <returns>An instance of <see cref="TypeProvider"/>.</returns>
        public ModelProvider? CreateModel(InputModelType model)
        {
            if (CSharpToModelProvider.TryGetValue(model, out var modelProvider))
                return modelProvider;

            modelProvider = CreateModelCore(model);

            foreach (var visitor in Visitors)
            {
                modelProvider = visitor.PreVisitModel(model, modelProvider);
            }

            CSharpToModelProvider.Add(model, modelProvider);
            return modelProvider;
        }

        protected virtual ModelProvider? CreateModelCore(InputModelType model) => new ModelProvider(model);

        /// <summary>
        /// Factory method for creating a <see cref="TypeProvider"/> based on an <see cref="InputEnumType"> <paramref name="enumType"/>.
        /// </summary>
        /// <param name="enumType">The <see cref="InputEnumType"/> to convert.</param>
        /// <param name="declaringType"/> The declaring <see cref="TypeProvider".</param>
        /// <returns>An instance of <see cref="EnumProvider"/>.</returns>
        public EnumProvider? CreateEnum(InputEnumType enumType, TypeProvider? declaringType = null)
        {
            var enumCacheKey = new EnumCacheKey(enumType, declaringType);
            if (EnumCache.TryGetValue(enumCacheKey, out var enumProvider))
                return enumProvider;

            enumProvider = CreateEnumCore(enumType, declaringType);

            foreach (var visitor in Visitors)
            {
                enumProvider = visitor.PreVisitEnum(enumType, enumProvider);
            }

            EnumCache.Add(enumCacheKey, enumProvider);
            return enumProvider;
        }

        protected virtual EnumProvider? CreateEnumCore(InputEnumType enumType, TypeProvider? declaringType)
            => EnumProvider.Create(enumType, declaringType);

        /// <summary>
        /// Factory method for creating a <see cref="ParameterProvider"/> based on an input parameter <paramref name="parameter"/>.
        /// </summary>
        /// <param name="parameter">The <see cref="InputParameter"/> to convert.</param>
        /// <returns>An instance of <see cref="ParameterProvider"/>.</returns>
        public ParameterProvider CreateParameter(InputParameter parameter)
            => CreateParameterCore(parameter);

        protected virtual ParameterProvider CreateParameterCore(InputParameter parameter)
            => new ParameterProvider(parameter);

        /// <summary>
        /// Creates a <see cref="PropertyProvider"/> based on an input property <paramref name="property"/>.
        /// </summary>
        /// <param name="property">The input property.</param>
        /// <returns>The property provider.</returns>
        public PropertyProvider? CreateProperty(InputModelProperty property, TypeProvider enclosingType)
        {
            if (PropertyCache.TryGetValue(property, out var propertyProvider))
                return propertyProvider;

            propertyProvider = CreatePropertyCore(property, enclosingType);
            PropertyCache.Add(property, propertyProvider);
            return propertyProvider;
        }

        /// <summary>
        /// Factory method for creating a <see cref="PropertyProvider"/> based on an input property <paramref name="property"/>.
        /// </summary>
        /// <param name="property">The input model property.</param>
        /// <param name="enclosingType">The enclosing type.</param>
        /// <returns>An instance of <see cref="PropertyProvider"/>.</returns>
        protected virtual PropertyProvider? CreatePropertyCore(InputModelProperty property, TypeProvider enclosingType)
        {
            PropertyProvider.TryCreate(property, enclosingType, out var propertyProvider);
            if (Visitors.Count == 0)
            {
                return propertyProvider;
            }
            foreach (var visitor in Visitors)
            {
                propertyProvider = visitor.PreVisitProperty(property, propertyProvider);
            }
            return propertyProvider;
        }

        /// <summary>
        /// Factory method for retrieving the serialization format for a given input type.
        /// </summary>
        /// <param name="input">The <see cref="InputType"/> to retrieve the serialization format for.</param>
        /// <returns>The <see cref="SerializationFormat"/> for the input type.</returns>
        public SerializationFormat GetSerializationFormat(InputType input) => input switch
        {
            InputLiteralType literalType => GetSerializationFormat(literalType.ValueType),
            InputArrayType listType => GetSerializationFormat(listType.ValueType),
            InputDictionaryType dictionaryType => GetSerializationFormat(dictionaryType.ValueType),
            InputNullableType nullableType => GetSerializationFormat(nullableType.Type),
            InputDateTimeType dateTimeType => dateTimeType.Encode switch
            {
                DateTimeKnownEncoding.Rfc3339 => SerializationFormat.DateTime_RFC3339,
                DateTimeKnownEncoding.Rfc7231 => SerializationFormat.DateTime_RFC7231,
                DateTimeKnownEncoding.UnixTimestamp => SerializationFormat.DateTime_Unix,
                _ => throw new IndexOutOfRangeException($"unknown encode {dateTimeType.Encode}"),
            },
            InputDurationType durationType => durationType.Encode switch
            {
                // there is no such thing as `DurationConstant`
                DurationKnownEncoding.Iso8601 => SerializationFormat.Duration_ISO8601,
                DurationKnownEncoding.Seconds => durationType.WireType.Kind switch
                {
                    InputPrimitiveTypeKind.Int32 => SerializationFormat.Duration_Seconds,
                    InputPrimitiveTypeKind.Float or InputPrimitiveTypeKind.Float32 => SerializationFormat.Duration_Seconds_Float,
                    _ => SerializationFormat.Duration_Seconds_Double
                },
                DurationKnownEncoding.Constant => SerializationFormat.Duration_Constant,
                _ => throw new IndexOutOfRangeException($"unknown encode {durationType.Encode}")
            },
            InputPrimitiveType primitiveType => primitiveType.Kind switch
            {
                InputPrimitiveTypeKind.PlainDate => SerializationFormat.Date_ISO8601,
                InputPrimitiveTypeKind.PlainTime => SerializationFormat.Time_ISO8601,
                InputPrimitiveTypeKind.Bytes => primitiveType.Encode switch
                {
                    BytesKnownEncoding.Base64 => SerializationFormat.Bytes_Base64,
                    BytesKnownEncoding.Base64Url => SerializationFormat.Bytes_Base64Url,
                    null => SerializationFormat.Default,
                    _ => throw new IndexOutOfRangeException($"unknown encode {primitiveType.Encode}")
                },
                InputPrimitiveTypeKind.Integer or InputPrimitiveTypeKind.Int8 or InputPrimitiveTypeKind.Int16 or InputPrimitiveTypeKind.Int32
                    or InputPrimitiveTypeKind.Int64 or InputPrimitiveTypeKind.UInt8 or InputPrimitiveTypeKind.UInt16 or InputPrimitiveTypeKind.UInt32
                    or InputPrimitiveTypeKind.UInt64 or InputPrimitiveTypeKind.SafeInt when primitiveType.Encode is "string" => SerializationFormat.Int_String,
                _ => SerializationFormat.Default
            },
            _ => SerializationFormat.Default
        };

        /// <summary>
        /// The initialization type of list properties. This type should implement both <see cref="IList{T}"/> and <see cref="IReadOnlyList{T}"/>.
        /// </summary>
        public virtual CSharpType ListInitializationType => ChangeTrackingListProvider.Type;

        /// <summary>
        /// The initialization type of dictionary properties. This type should implement both <see cref="IDictionary{TKey, TValue}"/> and <see cref="IReadOnlyDictionary{TKey, TValue}"/>.
        /// </summary>
        public virtual CSharpType DictionaryInitializationType => ChangeTrackingDictionaryProvider.Type;

        /// <summary>
        /// Returns the serialization type providers for the given model type provider.
        /// </summary>
        /// <param name="inputType">The input model.</param>
        /// <param name="typeProvider">The type provider.</param>
        public IReadOnlyList<TypeProvider> CreateSerializations(InputType inputType, TypeProvider typeProvider)
        {
            if (SerializationsCache.TryGetValue(inputType, out var serializations))
                return serializations;

            serializations = CreateSerializationsCore(inputType, typeProvider);
            SerializationsCache.Add(inputType, serializations);
            return serializations;
        }

        protected virtual IReadOnlyList<TypeProvider> CreateSerializationsCore(InputType inputType, TypeProvider typeProvider)
        {
            return [];
        }

        public virtual NewProjectScaffolding CreateNewProjectScaffolding()
        {
            return new NewProjectScaffolding();
        }

        private readonly struct EnumCacheKey
        {
            public InputEnumType EnumType { get; }
            public TypeProvider? DeclaringType { get; }
            public EnumCacheKey(InputEnumType enumType, TypeProvider? declaringType)
            {
                EnumType = enumType;
                DeclaringType = declaringType;
            }
        }

        private string? _primaryNamespace;
        public string PrimaryNamespace => _primaryNamespace ??= GetCleanNameSpace(CodeModelGenerator.Instance.InputLibrary.InputNamespace.Name);

        public string GetCleanNameSpace(string clientNamespace)
        {
            Span<char> dest = stackalloc char[clientNamespace.Length + GetSegmentCount(clientNamespace)];
            var source = clientNamespace.AsSpan();
            var destIndex = 0;
            var nextDot = source.IndexOf('.');
            while (nextDot != -1)
            {
                var segment = source.Slice(0, nextDot);
                if (IsSpecialSegment(segment))
                {
                    dest[destIndex] = '_';
                    destIndex++;
                }
                segment.CopyTo(dest.Slice(destIndex));
                destIndex += segment.Length;
                dest[destIndex] = '.';
                destIndex++;
                source = source.Slice(nextDot + 1);
                nextDot = source.IndexOf('.');
            }
            if (IsSpecialSegment(source))
            {
                dest[destIndex] = '_';
                destIndex++;
            }
            source.CopyTo(dest.Slice(destIndex));
            destIndex += source.Length;
            return dest.Slice(0, destIndex).ToString();
        }

        private bool IsSpecialSegment(ReadOnlySpan<char> readOnlySpan)
        {
            var badNamespaceSegments = CodeModelGenerator.Instance.InputLibrary.InputNamespace.InvalidNamespaceSegments;
            for (int i = 0; i < badNamespaceSegments.Count; i++)
            {
                if (readOnlySpan.Equals(badNamespaceSegments[i], StringComparison.Ordinal))
                {
                    return true;
                }
            }
            return false;
        }

        private static int GetSegmentCount(string clientNamespace)
        {
            int count = 0;
            for (int i = 0; i < clientNamespace.Length; i++)
            {
                if (clientNamespace[i] == '.')
                {
                    count++;
                }
            }
            return ++count;
        }
    }
}
