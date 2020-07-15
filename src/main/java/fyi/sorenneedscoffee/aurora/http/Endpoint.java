package fyi.sorenneedscoffee.aurora.http;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;

import javax.ws.rs.core.Response;
import java.io.Reader;

public abstract class Endpoint {
    protected static final Response BAD_REQUEST;
    protected static final Response NOT_FOUND;
    protected static final Response POINT_DOESNT_EXIST;
    protected static final Response UNPROCESSABLE_ENTITY;
    protected static final Response SERVER_ERROR;
    protected static final Response OK;

    static {
        BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST).build();
        NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();
        POINT_DOESNT_EXIST = Response.status(Response.Status.NOT_IMPLEMENTED)
                .entity("Point 0 is not present. Please add it ingame.")
                .build();
        UNPROCESSABLE_ENTITY = Response.status(422).build();
        SERVER_ERROR = Response.serverError().build();
        OK = Response.ok().build();
    }
}
