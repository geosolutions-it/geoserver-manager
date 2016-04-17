# geoserver-manager
## Status
 * Master on travis [![Build Status](https://travis-ci.org/geosolutions-it/geoserver-manager.svg?branch=master)](https://travis-ci.org/geosolutions-it/geoserver-manager)
 * Master on Linux + OracleJDK7 [![Build Status](http://build.geo-solutions.it/jenkins/view/GeoServer-manager/job/GeoServer-Manager-Master/badge/icon)](http://build.geo-solutions.it/jenkins/view/GeoServer-manager/job/GeoServer-Manager-Master/)
 * Master on Windows + OracleJDK7 [![Build Status](http://winbuild.geo-solutions.it/jenkins/buildStatus/icon?job=GeoServer-Manager-Master)](http://winbuild.geo-solutions.it/jenkins/view/GeoServer-Manager/job/GeoServer-Manager-Master/)
 * Stable on Linux + OracleJDK7 [![Build Status](http://build.geo-solutions.it/jenkins/view/GeoServer-manager/job/GeoServer-Manager-Stable/badge/icon)](http://build.geo-solutions.it/jenkins/view/GeoServer-manager/job/GeoServer-Manager-Stable/)
 * Stable on Windows + OracleJDK7 [![Build Status](http://winbuild.geo-solutions.it/jenkins/buildStatus/icon?job=GeoServer-Manager-Stable)](http://winbuild.geo-solutions.it/jenkins/view/GeoServer-Manager/job/GeoServer-Manager-Stable/)
 * Coveralls [![Coverage Status](https://coveralls.io/repos/github/geosolutions-it/geoserver-manager/badge.svg?branch=master)](https://coveralls.io/github/geosolutions-it/geoserver-manager?branch=master)


## Intro
Client library written in Java to interact with [GeoServer](http://www.geoserver.org) through its [ReST administration interface](http://docs.geoserver.org/stable/en/user/rest/api/index.html).

The purpose of this project is to hold a ReST client library to interact with GeoServer; a requirement for this library is to depend as less as possible on external libraries. This library aims at being lean and mean.

For general questions about this project feel free to use the mailing lists.

## Using the library 

### Working with Maven 
In order to include the lib and its dependencies in a Maven project, the repository to point at is this one:

```xml
   <repository>
      <id>GeoSolutions</id>
      <url>http://maven.geo-solutions.it/</url>
   </repository>
```

and the dependency tag for your pom is as follows:

```xml
  <dependency>
    <groupId>it.geosolutions</groupId>
    <artifactId>geoserver-manager</artifactId>
    <version>1.7.0</version>
  </dependency>
```
If you are simply looking for the JAR to include in your project, you can find it in our Maven repository [here](http://maven.geo-solutions.it/it/geosolutions/geoserver-manager/1.7.0/geoserver-manager-1.7.0.jar).

## Documentation 
You can find some examples in the wiki.
## License

geoserver-manager is released under a permissinve [MIT](https://opensource.org/licenses/MIT) license. See [wikipedia](https://en.wikipedia.org/wiki/MIT_License) for more information.


## Mailing Lists

 * [USERS](https://groups.google.com/forum/?fromgroups#!forum/geoserver-manager-users]https://groups.google.com/forum/?fromgroups#!forum/geoserver-manager-users)
 * [DEVELOPERS](https://groups.google.com/forum/?fromgroups#!forum/geoserver-manager-devs)

For more information see [this](https://github.com/geosolutions-it/geoserver-manager/wiki) page.

## Version 
Current stable version is 1.7.0 ([[Changelog]]).

## Credits
The work on this library has been initiated by GeoSolutions. Over the years it has been funder by various organizations like UN FAO, German Space Agency (DLR) and others.
