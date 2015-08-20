HyperNavi
======
Hypermarket Navigator
------

###Build status
* [HyperNavi -- Release to Dev] (http://hypernavi.net:8111/viewLog.html?buildTypeId=Hypernavi_ReleaseToDev&buildId=lastFinished) ![report status](http://hypernavi.net:8111/app/rest/builds/buildType:Hypernavi_ReleaseToDev/statusIcon)

###About

about: TBD

###System requirements

1. Java 1.8
2. Android SDK 22
3. Maven 3.1.1 (optional)

###How to install project (for IntelliJ IDEA)

In `Project Structure`,

1. Add JDK 1.8 and Android SDK 22 to `SDKs` in `Platform Settings`
2. Set `Project SDK` to JDK 1.8 and `Project language level` to 8 in `Project` tab
3. Set `Module SDK` to Android SDK 22 for `client`, `app` and `marker` modules (see `Dependencies` in `Modules` tab)

After, run `Run Tests [All]` configuration

###How to run server on local machine

Run `Run Server` configuration. Send request to server in your browser,

[http://localhost:8080/category?text=носки](http://localhost:8080/category?text=носки)

###How to run android application in emulator/device

Create and run configuration for MarkerActivity class. Select emulator or USB device in configuration settings.