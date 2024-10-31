// <auto-generated/>

#nullable disable

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace UnbrandedTypeSpec
{
    internal partial class ClientUriBuilder
    {
        private global::System.UriBuilder _uriBuilder;
        private global::System.Text.StringBuilder _pathBuilder;
        private global::System.Text.StringBuilder _queryBuilder;

        public ClientUriBuilder()
        {
        }

        private global::System.UriBuilder UriBuilder => (_uriBuilder  ??=  new global::System.UriBuilder());

        private global::System.Text.StringBuilder PathBuilder => (_pathBuilder  ??=  new global::System.Text.StringBuilder(UriBuilder.Path));

        private global::System.Text.StringBuilder QueryBuilder => (_queryBuilder  ??=  new global::System.Text.StringBuilder(UriBuilder.Query));

        public void Reset(global::System.Uri uri)
        {
            _uriBuilder = new global::System.UriBuilder(uri);
            _pathBuilder = new global::System.Text.StringBuilder(UriBuilder.Path);
            _queryBuilder = new global::System.Text.StringBuilder(UriBuilder.Query);
        }

        public void AppendPath(string value, bool escape)
        {
            if (escape)
            {
                value = global::System.Uri.EscapeDataString(value);
            }
            if ((((PathBuilder.Length > 0) && (PathBuilder[(PathBuilder.Length - 1)] == '/')) && (value[0] == '/')))
            {
                PathBuilder.Remove((PathBuilder.Length - 1), 1);
            }
            PathBuilder.Append(value);
            UriBuilder.Path = PathBuilder.ToString();
        }

        public void AppendPath(bool value, bool escape = false) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendPath(float value, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendPath(double value, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendPath(int value, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendPath(global::System.Byte[] value, string format, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value, format), escape);

        public void AppendPath(global::System.Collections.Generic.IEnumerable<string> value, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendPath(global::System.DateTimeOffset value, string format, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value, format), escape);

        public void AppendPath(global::System.TimeSpan value, string format, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value, format), escape);

        public void AppendPath(global::System.Guid value, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendPath(long value, bool escape = true) => AppendPath(global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, string value, bool escape)
        {
            if ((QueryBuilder.Length > 0))
            {
                QueryBuilder.Append('&');
            }
            if (escape)
            {
                value = global::System.Uri.EscapeDataString(value);
            }
            QueryBuilder.Append(name);
            QueryBuilder.Append('=');
            QueryBuilder.Append(value);
        }

        public void AppendQuery(string name, bool value, bool escape = false) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, float value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, global::System.DateTimeOffset value, string format, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value, format), escape);

        public void AppendQuery(string name, global::System.TimeSpan value, string format, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value, format), escape);

        public void AppendQuery(string name, double value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, decimal value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, int value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, long value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, global::System.TimeSpan value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public void AppendQuery(string name, global::System.Byte[] value, string format, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value, format), escape);

        public void AppendQuery(string name, global::System.Guid value, bool escape = true) => AppendQuery(name, global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(value), escape);

        public global::System.Uri ToUri()
        {
            if ((_pathBuilder != null))
            {
                UriBuilder.Path = _pathBuilder.ToString();
            }
            if ((_queryBuilder != null))
            {
                UriBuilder.Query = _queryBuilder.ToString();
            }
            return UriBuilder.Uri;
        }

        public void AppendQueryDelimited<T>(string name, global::System.Collections.Generic.IEnumerable<T> value, string delimiter, bool escape = true)
        {
            global::System.Collections.Generic.IEnumerable<string> stringValues = value.Select(v => global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(v));
            AppendQuery(name, string.Join(delimiter, stringValues), escape);
        }

        public void AppendQueryDelimited<T>(string name, global::System.Collections.Generic.IEnumerable<T> value, string delimiter, string format, bool escape = true)
        {
            global::System.Collections.Generic.IEnumerable<string> stringValues = value.Select(v => global::UnbrandedTypeSpec.TypeFormatters.ConvertToString(v, format));
            AppendQuery(name, string.Join(delimiter, stringValues), escape);
        }
    }
}
