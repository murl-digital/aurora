package fyi.sorenneedscoffee.aurora.http.endpoints.console;

import com.google.gson.JsonSyntaxException;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

public class ConsoleCommandEndpoint extends Endpoint {

  public ConsoleCommandEndpoint() {
    this.path = Pattern.compile("/commands");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      String[] commands = Aurora.gson.fromJson(bodyStream, String[].class);
      Bukkit.getScheduler().runTask(Aurora.plugin, () -> {
        for (String command : commands) {
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
      });
      return OK;
    } catch (JsonSyntaxException e) {
      return BAD_REQUEST;
    } catch (Exception e) {
      if (e.getMessage() != null) {
        Aurora.logger.warning(e.getMessage());
      }
      Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
      String message =
          e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)
              : ExceptionUtils.getStackTrace(e);
      return SERVER_ERROR.clone().entity(message).build();
    }
  }
}
