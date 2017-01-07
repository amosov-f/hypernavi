package ru.hypernavi.server.servlet.admin;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Site;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.ml.regression.map.MapFactors;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.ml.regression.map.ValidationResult;
import ru.hypernavi.server.servlet.AbstractHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by amosov-f on 04.01.17
 */
@WebServlet(value = "/test")
public class TestService extends AbstractHttpService {
  @Inject
  private SiteProvider provider;

  @Override
  public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
//    final Site site = Arrays.stream(provider.getAllSites()).filter(s -> "Central Park".equals(s.getPlace().getName()))
//        .findFirst()
//        .get();
//
//    final Plan plan = (Plan) site.getHints()[0];
//
//    final ValidationResult r1 = MapProjectionImpl.validate(MapFactors.RAW_LON_LAT, plan.getPoints());
//    final ValidationResult r2 = MapProjectionImpl.validate(MapFactors.linearLonLat(plan.getPoints()), plan.getPoints());

    final Site[] sites = provider.getAllSites();
    double ddx = 0;
    double ddy = 0;
    for (Site site : sites) {
      if (site.getHints().length > 0) {
        if (site.getHints()[0] instanceof Plan) {
          final Plan plan = (Plan) site.getHints()[0];
          if (plan.getPoints().length > 10) {
            final ValidationResult r1 = MapProjectionImpl.validate(MapFactors.linearLonLat(plan.getPoints()), plan.getPoints());
            final ValidationResult r2 = MapProjectionImpl.validate(MapFactors.linearLonLat(plan.getPoints()), plan.getPoints());
            try {
              final double dx = r2.getEvalX().correlationCoefficient() - r1.getEvalX().correlationCoefficient();
              final double dy = r2.getEvalY().correlationCoefficient() - r1.getEvalY().correlationCoefficient();
              System.out.println(site.getPlace().getName() + " " + dx + " " + dy);
              ddx += dx;
              ddy += dy;
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }
      }
    }
    try {
      resp.getWriter().write(ddx + ", " + ddy);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
