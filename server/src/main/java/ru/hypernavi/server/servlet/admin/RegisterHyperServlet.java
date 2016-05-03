package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.Building;
import ru.hypernavi.core.database.RegisterHypermarket;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionValidationException;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.server.servlet.search.SearchRequest;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Константин on 21.08.2015.
 */
@Deprecated
@WebServlet(name = "register hypermarket", value = "/register/hypermarket")
public class RegisterHyperServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(RegisterHyperServlet.class);

    private static final Param<String> PARAM_ADDRESS = new QueryParam.StringParam("address");
    private static final Param<String> PARAM_TYPE = new QueryParam.StringParam("type");
    private static final Param<String> PARAM_URL = new QueryParam.StringParam("url");
    private static final Param<String> PARAM_PAGE = new QueryParam.StringParam("page");

    private static final Property<String> ADDRESS = new Property<>("address");
    private static final Property<String> TYPE = new Property<>("type");
    private static final Property<String> URL = new Property<>("url");
    private static final Property<String> PAGE = new Property<>("page");

    public RegisterHyperServlet() {
        super(new RequestReader.Factory<SearchRequest>() {
            @NotNull
            @Override
            public SearchRequest create(@NotNull final HttpServletRequest req) {
                return new SearchRequest(req) {
                    @Override
                    public void initialize(@NotNull final Session session) {
                        super.initialize(session);
                        setPropertyIfPresent(session, ADDRESS, PARAM_ADDRESS);
                        setPropertyIfPresent(session, TYPE, PARAM_TYPE);
                        setPropertyIfPresent(session, URL, PARAM_URL);
                        setPropertyIfPresent(session, RegisterHyperServlet.PAGE, PARAM_PAGE);
                    }

                    @Override
                    public void validate(@NotNull final Session session) throws SessionValidationException {
                        super.validate(session);
                        if (session.has(ADDRESS) && session.has(TYPE) && session.has(URL) && session.has(PAGE)) {
                            return;
                        }
                        throw new SessionValidationException("Request hasn't required params!");
                    }
                };
            }
        });
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {

        final GeoPoint location = session.demand(Property.GEO_LOCATION);

        final String address = session.demand(ADDRESS);
        final String type = session.demand(TYPE);
        final String url = session.demand(URL);
        final String page = session.demand(PAGE);

        RegisterHypermarket.register(new Building(new GeoPointImpl(location.getLongitude(), location.getLatitude()), address, null, null, null), type, url, page);

        LOG.info("OK!");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().print("OK");
    }
}
