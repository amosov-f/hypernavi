SET @orig_lat = %f;
SET @orig_lon = %f;
SELECT
  *,
  3956 * 2 * ASIN(SQRT(
                      POWER(SIN((@orig_lat - abs(dest.lat)) * pi() / 180 / 2), 2)
                      + COS(@orig_lat * pi() / 180) * COS(abs(dest.lat) * pi() / 180)
                        * POWER(SIN((@orig_lon – dest.lon) * pi() / 180 / 2), 2)
                  ))
    AS distance
FROM hotels dest
HAVING distance < @dist
ORDER BY distance
LIMIT 10 OFFSET %d;