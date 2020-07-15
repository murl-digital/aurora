package fyi.sorenneedscoffee.aurora.http.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

    private static final String PRETTY_PRINT = "pretty-print";

    private final Gson gson;
    private final Gson prettyGson;

    @Context
    private UriInfo ui;

    public GsonProvider() {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()
                .enableComplexMapKeySerialization();

        this.gson = builder.create();
        this.prettyGson = builder.setPrettyPrinting().create();
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                      MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {

        try (InputStreamReader reader = new InputStreamReader(entityStream, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, type);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws WebApplicationException {

        try (PrintWriter printWriter = new PrintWriter(entityStream)) {
            String json;
            if (ui.getQueryParameters().containsKey(PRETTY_PRINT)) {
                json = prettyGson.toJson(t);
            } else {
                json = gson.toJson(t);
            }
            printWriter.write(json);
            printWriter.flush();
        }
    }
}
