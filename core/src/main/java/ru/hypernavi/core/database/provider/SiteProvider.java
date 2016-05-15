package ru.hypernavi.core.database.provider;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.commons.Site;

/**
 * User: amosov-f
 * Date: 16.05.16
 * Time: 0:54
 */
public interface SiteProvider extends DatabaseProvider<Site> {
    @NotNull
    Site[] getAllSites();

    class Impl extends DatabaseProvider.Impl<Site> implements SiteProvider {
        @NotNull
        @Override
        public Site[] getAllSites() {
            return data.values().toArray(new Site[data.size()]);
        }
    }
}
