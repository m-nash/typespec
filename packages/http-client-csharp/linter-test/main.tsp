import "@typespec/http";

using TypeSpec.Http;

namespace Azure.Storage.Tables;

// Single-word model names — should trigger linter warnings

@doc("Represents a document about eating carrots")
model Document {
  @doc("The document ID")
  id: string;

  @doc("The document content")
  content: string;

  @doc("Last modified timestamp")
  lastModified: utcDateTime;
}

// Multi-word model names — should NOT trigger linter warnings

@doc("Represents a table entity")
model TableEntity {
  partitionKey: string;
  rowKey: string;
}

@doc("Storage configuration options")
model StorageConfiguration {
  maxRetries: int32;
  timeout: duration;
}

@route("/tables")
interface Tables {
  @get list(): TableEntity[];
  @get read(@path id: string): Document;
}
