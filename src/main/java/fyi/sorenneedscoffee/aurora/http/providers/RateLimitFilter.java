package fyi.sorenneedscoffee.aurora.http.providers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Provider
public class RateLimitFilter implements ContainerRequestFilter {
    boolean allow = true;

    private final Timer timer = new Timer();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (allow) {
            allow = false;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    allow = true;
                }
            }, 50);
        } else {
            requestContext.abortWith(Response.ok().build());
        }
    }
}
