package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.function.Supplier;


import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.util.TextUtils;
import ru.hypernavi.util.function.IOFunction;
import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 14:40
 */
public abstract class BodyParam<T> implements Param<T> {
    private static final Log LOG = LogFactory.getLog(BodyParam.class);

    @Nullable
    @Override
    public T getValue(@NotNull final HttpServletRequest req) {
        try {
            return parse(req.getReader());
        } catch (IOException e) {
            LOG.warn(e);
        } catch (RuntimeException e) {
            LOG.error(e);
        }
        return null;
    }

    @Nullable
    protected abstract T parse(@NotNull final Reader bodyReader) throws IOException;

    public static class LambdaParam<T> extends BodyParam<T> {
        @NotNull
        private final IOFunction<Reader, T> parser;

        public LambdaParam(@NotNull final IOFunction<Reader, T> parser) {
            this.parser = parser;
        }

        @Nullable
        @Override
        protected T parse(@NotNull final Reader bodyReader) throws IOException {
            return parser.apply(bodyReader);
        }
    }

    public static class ObjectParam<T> extends LambdaParam<T> {
        public ObjectParam(@NotNull final Type type) {
            this(type, GsonUtils::gson);
        }

        public ObjectParam(@NotNull final Type type, @NotNull final Supplier<Gson> gson) {
            this(type, gson, false);
        }

        public ObjectParam(@NotNull final Type type, @NotNull final Supplier<Gson> gson, final boolean debug) {
            super(reader -> parse(reader, type, gson.get(), debug));
        }

        @NotNull
        private static <T> T parse(@NotNull final Reader reader,
                                   @NotNull final Type type,
                                   @NotNull final Gson gson,
                                   final boolean debug) throws IOException {
            if (!debug) {
                return gson.fromJson(reader, type);
            }
            final String body = IOUtils.toString(reader);
            LOG.debug("Recieved post body: '" + TextUtils.limit(body, 1000) + "'");
            return gson.fromJson(body, type);
        }
    }
}
