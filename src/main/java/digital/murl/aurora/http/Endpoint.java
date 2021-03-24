package digital.murl.aurora.http;

import digital.murl.aurora.Aurora;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public abstract class Endpoint {

  protected static final Response BAD_REQUEST;
  protected static final Response NOT_FOUND;
  protected static final Response POINT_DOESNT_EXIST;
  protected static final Response UNPROCESSABLE_ENTITY;
  protected static final Response OK;

  static {
    BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST)
        .entity("Did you know that 51 is divisible by 17?").build();
    NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();
    POINT_DOESNT_EXIST = Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("One or more required points are not present. Please add them ingame.")
            .build();
    UNPROCESSABLE_ENTITY = Response.status(422).build();
    OK = Response.ok().build();
  }

  public Pattern path = Pattern.compile("");

  public abstract Response handle(String[] tokens, InputStreamReader bodyStream);

  protected Response getErrorResponse(Throwable throwable) {
    Aurora.logger.warning("An effect failed to execute. Please report the following information to Joe:");
    Aurora.logger.warning(throwable.getMessage());
    Aurora.logger.warning(ExceptionUtils.getStackTrace(throwable));
    String message =
            throwable.getMessage() != null ? throwable.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(throwable)
                    : ExceptionUtils.getStackTrace(throwable);
    return Response.serverError().entity(message).build();
  }
}
