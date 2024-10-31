// <auto-generated/>

#nullable disable

using System.ClientModel;
using System.ClientModel.Primitives;

namespace UnbrandedTypeSpec
{
    internal partial class ErrorResult<T> : global::System.ClientModel.ClientResult<T>
    {
        private readonly global::System.ClientModel.Primitives.PipelineResponse _response;
        private readonly global::System.ClientModel.ClientResultException _exception;

        public ErrorResult(global::System.ClientModel.Primitives.PipelineResponse response, global::System.ClientModel.ClientResultException exception) : base(default, response)
        {
            _response = response;
            _exception = exception;
        }

        public override T Value => throw _exception;
    }
}
