SELECT geo_object.name, geo_object.description, geo_point.latitude, geo_point.longitude, [plan].link, [plan].azimuth
FROM site WHERE id = %d
INNER JOIN geo_object ON geo_object.id = site.geo_object_id
INNER JOIN geo_point ON geo_point.id = geo_object.geo_point_id
OUTER JOIN [site_plan] ON [site].id = [site_plan].site_id
INNER JOIN [plan] ON [plan].id = [site_plan].plan_id