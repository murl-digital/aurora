package fyi.sorenneedscoffee.aurora.http.providers;

import com.google.gson.JsonSyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GsonExceptionMapper implements ExceptionMapper<JsonSyntaxException> {

    @Override
    public Response toResponse(JsonSyntaxException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
