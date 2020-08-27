package fyi.sorenneedscoffee.aurora.http;

import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;

public class Response {
    private final int statusCode;
    private final String entity;

    private Response(int statusCode, String entity) {
        this.statusCode = statusCode;
        this.entity = entity;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getEntity() {
        return entity;
    }

    public static ResponseBuilder status(Status status) {
        return status(status.getStatusCode());
    }

    public static ResponseBuilder status(int statusCode) {
        return new ResponseBuilder().status(statusCode);
    }

    public static ResponseBuilder ok() {
        return new ResponseBuilder().status(200);
    }

    public static ResponseBuilder serverError() {
        return new ResponseBuilder().status(500);
    }

    public boolean hasEntity() {
        return entity != null;
    }

    public long getLength() {
        return entity == null ? 0 : entity.length();
    }

    public static class ResponseBuilder implements Serializable{
        private int statusCode;
        private String entity;

        public ResponseBuilder status(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public ResponseBuilder entity(String entity) {
            this.entity = entity;
            return this;
        }

        public Response build() {
            return new Response(statusCode, entity);
        }

        public ResponseBuilder clone() {
            return (ResponseBuilder) SerializationUtils.clone(this);
        }
    }

    public enum Status {
        
        OK(200),

        CREATED(201),

        ACCEPTED(202),

        NO_CONTENT(204),

        RESET_CONTENT(205),

        PARTIAL_CONTENT(206),

        MOVED_PERMANENTLY(301),

        FOUND(302),

        SEE_OTHER(303),

        NOT_MODIFIED(304),

        USE_PROXY(305),

        TEMPORARY_REDIRECT(307),

        BAD_REQUEST(400),

        UNAUTHORIZED(401),

        PAYMENT_REQUIRED(402),

        FORBIDDEN(403),

        NOT_FOUND(404),

        METHOD_NOT_ALLOWED(405),

        NOT_ACCEPTABLE(406),

        PROXY_AUTHENTICATION_REQUIRED(407),

        REQUEST_TIMEOUT(408),

        CONFLICT(409),

        GONE(410),

        LENGTH_REQUIRED(411),

        PRECONDITION_FAILED(412),

        REQUEST_ENTITY_TOO_LARGE(413),

        REQUEST_URI_TOO_LONG(414),

        UNSUPPORTED_MEDIA_TYPE(415),

        REQUESTED_RANGE_NOT_SATISFIABLE(416),

        EXPECTATION_FAILED(417),

        INTERNAL_SERVER_ERROR(500),

        NOT_IMPLEMENTED(501),

        BAD_GATEWAY(502),

        SERVICE_UNAVAILABLE(503),

        GATEWAY_TIMEOUT(504),

        HTTP_VERSION_NOT_SUPPORTED(505);
        
        private final int code;


        Status(final int statusCode) {
            this.code = statusCode;
        }
        
        public int getStatusCode() {
            return code;
        }
    }
}
