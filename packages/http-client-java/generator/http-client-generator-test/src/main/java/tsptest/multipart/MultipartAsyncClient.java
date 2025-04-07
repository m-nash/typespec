// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package tsptest.multipart;

import com.azure.core.annotation.Generated;
import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceClient;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.exception.ClientAuthenticationException;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.exception.ResourceModifiedException;
import com.azure.core.exception.ResourceNotFoundException;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.core.util.FluxUtil;
import java.util.Objects;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;
import tsptest.multipart.implementation.MultipartClientImpl;
import tsptest.multipart.implementation.MultipartFormDataHelper;
import tsptest.multipart.models.FileDataFileDetails;
import tsptest.multipart.models.FormData;
import tsptest.multipart.models.UploadHttpPartRequest;

/**
 * Initializes a new instance of the asynchronous MultipartClient type.
 */
@ServiceClient(builder = MultipartClientBuilder.class, isAsync = true)
public final class MultipartAsyncClient {
    @Generated
    private final MultipartClientImpl serviceClient;

    /**
     * Initializes an instance of MultipartAsyncClient class.
     * 
     * @param serviceClient the service client implementation.
     */
    @Generated
    MultipartAsyncClient(MultipartClientImpl serviceClient) {
        this.serviceClient = serviceClient;
    }

    /**
     * The upload operation.
     * <p><strong>Query Parameters</strong></p>
     * <table border="1">
     * <caption>Query Parameters</caption>
     * <tr><th>Name</th><th>Type</th><th>Required</th><th>Description</th></tr>
     * <tr><td>compress</td><td>Boolean</td><td>No</td><td>The compress parameter</td></tr>
     * </table>
     * You can add these to a request with {@link RequestOptions#addQueryParam}
     * 
     * @param name The name parameter.
     * @param data The data parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the {@link Response} on successful completion of {@link Mono}.
     */
    @Generated
    @ServiceMethod(returns = ReturnType.SINGLE)
    Mono<Response<Void>> uploadWithResponse(String name, BinaryData data, RequestOptions requestOptions) {
        // Operation 'upload' is of content-type 'multipart/form-data'. Protocol API is not usable and hence not
        // generated.
        return this.serviceClient.uploadWithResponseAsync(name, data, requestOptions);
    }

    /**
     * The uploadHttpPart operation.
     * <p><strong>Query Parameters</strong></p>
     * <table border="1">
     * <caption>Query Parameters</caption>
     * <tr><th>Name</th><th>Type</th><th>Required</th><th>Description</th></tr>
     * <tr><td>compress</td><td>Boolean</td><td>No</td><td>The compress parameter</td></tr>
     * </table>
     * You can add these to a request with {@link RequestOptions#addQueryParam}
     * 
     * @param name The name parameter.
     * @param body The body parameter.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @return the {@link Response} on successful completion of {@link Mono}.
     */
    @Generated
    @ServiceMethod(returns = ReturnType.SINGLE)
    Mono<Response<Void>> uploadHttpPartWithResponse(String name, BinaryData body, RequestOptions requestOptions) {
        // Operation 'uploadHttpPart' is of content-type 'multipart/form-data'. Protocol API is not usable and hence not
        // generated.
        return this.serviceClient.uploadHttpPartWithResponseAsync(name, body, requestOptions);
    }

    /**
     * The upload operation.
     * 
     * @param name The name parameter.
     * @param data The data parameter.
     * @param compress The compress parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return A {@link Mono} that completes when a successful response is received.
     */
    @Generated
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Void> upload(String name, FormData data, Boolean compress) {
        // Generated convenience method for uploadWithResponse
        RequestOptions requestOptions = new RequestOptions();
        if (compress != null) {
            requestOptions.addQueryParam("compress", String.valueOf(compress), false);
        }
        return uploadWithResponse(name, new MultipartFormDataHelper(requestOptions)
            .serializeTextField("name", data.getName())
            .serializeTextField("resolution", String.valueOf(data.getResolution()))
            .serializeTextField("type", Objects.toString(data.getType()))
            .serializeJsonField("size", data.getSize())
            .serializeFileField("image", data.getImage().getContent(), data.getImage().getContentType(),
                data.getImage().getFilename())
            .serializeFileFields("fileData",
                data.getFileData() == null
                    ? null
                    : data.getFileData().stream().map(FileDataFileDetails::getContent).collect(Collectors.toList()),
                data.getFileData() == null
                    ? null
                    : data.getFileData().stream().map(FileDataFileDetails::getContentType).collect(Collectors.toList()),
                data.getFileData() == null
                    ? null
                    : data.getFileData().stream().map(FileDataFileDetails::getFilename).collect(Collectors.toList()))
            .end()
            .getRequestBody(), requestOptions).flatMap(FluxUtil::toMono);
    }

    /**
     * The upload operation.
     * 
     * @param name The name parameter.
     * @param data The data parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return A {@link Mono} that completes when a successful response is received.
     */
    @Generated
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Void> upload(String name, FormData data) {
        // Generated convenience method for uploadWithResponse
        RequestOptions requestOptions = new RequestOptions();
        return uploadWithResponse(name, new MultipartFormDataHelper(requestOptions)
            .serializeTextField("name", data.getName())
            .serializeTextField("resolution", String.valueOf(data.getResolution()))
            .serializeTextField("type", Objects.toString(data.getType()))
            .serializeJsonField("size", data.getSize())
            .serializeFileField("image", data.getImage().getContent(), data.getImage().getContentType(),
                data.getImage().getFilename())
            .serializeFileFields("fileData",
                data.getFileData() == null
                    ? null
                    : data.getFileData().stream().map(FileDataFileDetails::getContent).collect(Collectors.toList()),
                data.getFileData() == null
                    ? null
                    : data.getFileData().stream().map(FileDataFileDetails::getContentType).collect(Collectors.toList()),
                data.getFileData() == null
                    ? null
                    : data.getFileData().stream().map(FileDataFileDetails::getFilename).collect(Collectors.toList()))
            .end()
            .getRequestBody(), requestOptions).flatMap(FluxUtil::toMono);
    }

    /**
     * The uploadHttpPart operation.
     * 
     * @param name The name parameter.
     * @param body The body parameter.
     * @param compress The compress parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return A {@link Mono} that completes when a successful response is received.
     */
    @Generated
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Void> uploadHttpPart(String name, UploadHttpPartRequest body, Boolean compress) {
        // Generated convenience method for uploadHttpPartWithResponse
        RequestOptions requestOptions = new RequestOptions();
        if (compress != null) {
            requestOptions.addQueryParam("compress", String.valueOf(compress), false);
        }
        return uploadHttpPartWithResponse(name,
            new MultipartFormDataHelper(requestOptions)
                .serializeFileField("fileData1", body.getFileData1().getContent(), body.getFileData1().getContentType(),
                    body.getFileData1().getFilename())
                .serializeFileField("file_data2", body.getFileData2().getContent(),
                    body.getFileData2().getContentType(), body.getFileData2().getFilename())
                .serializeJsonField("size", body.getSize())
                .end()
                .getRequestBody(),
            requestOptions).flatMap(FluxUtil::toMono);
    }

    /**
     * The uploadHttpPart operation.
     * 
     * @param name The name parameter.
     * @param body The body parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws HttpResponseException thrown if the request is rejected by server.
     * @throws ClientAuthenticationException thrown if the request is rejected by server on status code 401.
     * @throws ResourceNotFoundException thrown if the request is rejected by server on status code 404.
     * @throws ResourceModifiedException thrown if the request is rejected by server on status code 409.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return A {@link Mono} that completes when a successful response is received.
     */
    @Generated
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<Void> uploadHttpPart(String name, UploadHttpPartRequest body) {
        // Generated convenience method for uploadHttpPartWithResponse
        RequestOptions requestOptions = new RequestOptions();
        return uploadHttpPartWithResponse(name,
            new MultipartFormDataHelper(requestOptions)
                .serializeFileField("fileData1", body.getFileData1().getContent(), body.getFileData1().getContentType(),
                    body.getFileData1().getFilename())
                .serializeFileField("file_data2", body.getFileData2().getContent(),
                    body.getFileData2().getContentType(), body.getFileData2().getFilename())
                .serializeJsonField("size", body.getSize())
                .end()
                .getRequestBody(),
            requestOptions).flatMap(FluxUtil::toMono);
    }
}
