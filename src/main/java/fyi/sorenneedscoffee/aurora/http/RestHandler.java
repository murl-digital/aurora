package fyi.sorenneedscoffee.aurora.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.endpoints.StopEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.bossbar.BossBarEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.dragon.DragonEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.laser.EndLaserEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.laser.LaserEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.laser.TargetedLaserEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.particle.ParticleEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.potion.GlobalPotionEndpoint;
import fyi.sorenneedscoffee.aurora.http.endpoints.time.TimeShiftEndpoint;
import fyi.sorenneedscoffee.aurora.http.models.bossbar.BossBarModel;
import fyi.sorenneedscoffee.aurora.http.models.dragon.DragonModel;
import fyi.sorenneedscoffee.aurora.http.models.laser.LaserModel;
import fyi.sorenneedscoffee.aurora.http.models.laser.TargetedLaserModel;
import fyi.sorenneedscoffee.aurora.http.models.particle.ParticleModel;
import fyi.sorenneedscoffee.aurora.http.models.potion.GlobalPotionModel;
import fyi.sorenneedscoffee.aurora.http.models.time.TimeShiftModel;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        List<String> tokens = Arrays.asList(path.split("/"));
        if ("bar".equals(tokens.get(1))) {
            if ("clear".equals(tokens.get(2))) {
                Response response = BossBarEndpoint.clear();
                writeResponse(exchange, response);
                return;
            }

            if ("set".equals(tokens.get(2))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                BossBarModel[] models = Aurora.gson.fromJson(reader, BossBarModel[].class);
                Response response = BossBarEndpoint.start(models);
                writeResponse(exchange, response);
                return;
            }
        } else if ("effects".equals(tokens.get(1))) {
            if ("stop".equals(tokens.get(3))) {
                if ("all".equals(tokens.get(2))) {
                    Response response = StopEndpoint.stopAll();
                    writeResponse(exchange, response);
                    return;
                }

                Response response = StopEndpoint.stop(UUID.fromString(tokens.get(2)));
                writeResponse(exchange, response);
                return;
            }

            if ("time".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                TimeShiftModel[] models = Aurora.gson.fromJson(reader, TimeShiftModel[].class);
                Response response = TimeShiftEndpoint.startEffect(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("potion".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                GlobalPotionModel[] models = Aurora.gson.fromJson(reader, GlobalPotionModel[].class);
                Response response = GlobalPotionEndpoint.start(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("particle".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                ParticleModel[] models = Aurora.gson.fromJson(reader, ParticleModel[].class);
                Response response = ParticleEndpoint.start(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("particle".equals(tokens.get(2)) && "trigger".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                ParticleModel[] models = Aurora.gson.fromJson(reader, ParticleModel[].class);
                Response response = ParticleEndpoint.trigger(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("targetedlaser".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                TargetedLaserModel[] models = Aurora.gson.fromJson(reader, TargetedLaserModel[].class);
                Response response = TargetedLaserEndpoint.start(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("targetedlaser".equals(tokens.get(2)) && "restart".equals(tokens.get(4))) {
                Response response = TargetedLaserEndpoint.restart(UUID.fromString(tokens.get(3)));
                writeResponse(exchange, response);
                return;
            }

            if ("laser".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                LaserModel[] models = Aurora.gson.fromJson(reader, LaserModel[].class);
                Response response = LaserEndpoint.start(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("laser".equals(tokens.get(2)) && "trigger".equals(tokens.get(4))) {
                Response response = LaserEndpoint.trigger(UUID.fromString(tokens.get(3)));
                writeResponse(exchange, response);
                return;
            }

            if ("endlaser".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                LaserModel[] models = Aurora.gson.fromJson(reader, LaserModel[].class);
                Response response = EndLaserEndpoint.start(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("dragon".equals(tokens.get(2)) && "start".equals(tokens.get(4))) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                DragonModel[] models = Aurora.gson.fromJson(reader, DragonModel[].class);
                Response response = DragonEndpoint.start(UUID.fromString(tokens.get(3)), models);
                writeResponse(exchange, response);
                return;
            }

            if ("dragon".equals(tokens.get(2)) && "restart".equals(tokens.get(4))) {
                Response response = DragonEndpoint.restart(UUID.fromString(tokens.get(3)));
                writeResponse(exchange, response);
                return;
            }
        }

        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().close();
    }

    private void writeResponse(HttpExchange exchange, Response response) throws IOException {
        exchange.sendResponseHeaders(response.getStatus(), response.getLength());
        if (response.hasEntity()) {
            String entity = (String) response.getEntity();
            exchange.getResponseBody().write(entity.getBytes());
        }
        exchange.getResponseBody().close();
    }
}
