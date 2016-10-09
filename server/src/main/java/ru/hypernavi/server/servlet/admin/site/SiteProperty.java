package ru.hypernavi.server.servlet.admin.site;

import com.google.gson.reflect.TypeToken;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 13.06.16.
 */
public enum SiteProperty {
    ;

    public static final Property<String> SITE_ID = new Property<>("site_id");
    public static final Property<Site> SITE = new Property<>("site");
    public static final Property<Index<Site>> SITE_INDEX = new Property<>("site_index");

    public static final Param<String> SITE_ID_PARAM = new QueryParam.StringParam("site_id");
    public static final Param<Site> SITE_BODY = new BodyParam.ObjectParam<>(Site.class, GsonUtils::gson);
    public static final Param<Index<Site>> SITE_INDEX_BODY = new BodyParam.ObjectParam<>(new TypeToken<Index<Site>>() {}.getType(), GsonUtils::gson);
}
