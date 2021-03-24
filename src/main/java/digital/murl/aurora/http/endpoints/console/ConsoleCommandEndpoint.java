package digital.murl.aurora.http.endpoints.console;

import com.google.gson.JsonSyntaxException;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

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
      return getErrorResponse(e);
    }
  }
}
