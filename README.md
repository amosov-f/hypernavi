HyperNavi
======
Hypermarket Navigator
------

about: TBD

###System requirements

1. Java 1.8
2. Android SDK 22
3. Maven 3.1.1

###How to install project (for IntelliJ IDEA)

In `Project Structure`,

1. Add JDK 1.8 and Android SDK 22 to `SDKs` in `Platform Settings`
2. Set `Project SDK` to JDK 1.8 and `Project language level` to 8 in `Project` tab
3. Set Android SDK 22 to `Module SDK` for `app` and `marker` modules (see `Dependencies` in `Modules` tab)

###How to run server on local machine

Create and run configuration for HyperNaviServer class with next command line arguments,

`-port 8080 -cfg /common.properties /debug.properties`

###How to run android application in emulator/device

Create and run configuration for MarkerActivity class. Select emulator or USB device in configuration settings.

###Other HOWTOs
  - [How to deploy project to remote server](docs/howto/HowToDeploy.md)