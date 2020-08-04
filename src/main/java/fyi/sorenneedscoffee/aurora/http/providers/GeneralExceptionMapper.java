package fyi.sorenneedscoffee.aurora.http.providers;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof JsonSyntaxException)
            return Response.status(Response.Status.BAD_REQUEST).entity("The request body could not be parsed").build();

        int status = getStatusCode(exception);

        if (status == 500) {
            Aurora.logger.warning("Jersey encountered an error. Please send the below information to the developer.");
            Aurora.logger.warning(exception.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(exception));
            return Response.status(status).entity(exception.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(exception)).build();
        }

        return Response.status(status).build();
    }

    private int getStatusCode(Throwable exception)
    {
        if (exception instanceof WebApplicationException)
        {
            return ((WebApplicationException)exception).getResponse().getStatus();
        }

        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
}
