package fyi.sorenneedscoffee.aurora.http;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

import javax.ws.rs.core.Response;

@OpenAPIDefinition(
        info = @Info(
                title = "Aurora",
                version = "1.0.1",
                description = "Ooo, pretty lights | Spigot visuals system",
                contact = @Contact(name = "Joe/Soren", email = "joseph.md.sorensen@icloud.com")
        )
)
public abstract class Endpoint {
    protected static final Response BAD_REQUEST;
    protected static final Response NOT_FOUND;
    protected static final Response POINT_DOESNT_EXIST;
    protected static final Response UNPROCESSABLE_ENTITY;
    protected static final Response.ResponseBuilder SERVER_ERROR;
    protected static final Response OK;

    static {
        BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST).build();
        NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();
        POINT_DOESNT_EXIST = Response.status(Response.Status.NOT_IMPLEMENTED)
                .entity("Point 0 is not present. Please add it ingame.")
                .build();
        UNPROCESSABLE_ENTITY = Response.status(422).build();
        SERVER_ERROR = Response.serverError();
        OK = Response.ok().build();
    }
}
