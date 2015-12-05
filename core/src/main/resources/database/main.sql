CREATE DATABASE hypernavi;
USE hypernavi;

CREATE TABLE geo_point (
  id int NOT NULL AUTO_INCREMENT,
  latitude double NOT NULL,
  longitude double NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY ix_geo_point (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE geo_object (
  id int NOT NULL AUTO_INCREMENT,
  name text NOT NULL,
  description text NOT NULL,
  geo_point_id int NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY ix_geo_object (name(255), description(255), geo_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE plan (
  id int NOT NULL AUTO_INCREMENT,
  link text NOT NULL,
  azimuth double DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY ix_plan (link(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE site (
  id int NOT NULL AUTO_INCREMENT,
  geo_object_id int(11) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY ix_site (geo_object_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE site_plan (
  site_id int NOT NULL,
  plan_id int NOT NULL,
  UNIQUE KEY unique_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;