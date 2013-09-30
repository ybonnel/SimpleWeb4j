![logo](https://raw.github.com/ybonnel/SimpleWeb4j/master/images/logo-simpleweb4j-140.png)

SimpleWeb4j - A very Simple web stack.
==============================================

## Why SimpleWeb4j

My current way to create web application is angular.js on client side, with twitter bootstrap for css, and have json resources on server side.
So I want a very simple framework to help create this kind of web app.

It's why I created SimpleWeb4j, on server side, you can handle http request in order to create quickly json services,
and on client side create an angular.js app (you can also use others javascript framework, but I prefer angular).

## Dependencies

- Jetty : used for http server.
- slf4j : For the logs.
- gson : use to serialize/deserialize json objects.
- Hibernate : Use for entities management.
- H2 : use by default for database (you can exclude it if you use another database or if you don't use databases).

## Licence

All SimpleWeb4j is under the Apache License, Version 2.0


## Usage

### Maven dependency

```xml
<dependency>
    <groupId>fr.ybonnel</groupId>
    <artifactId>simpleweb4j</artifactId>
    <version>0.0.5</version>
</dependency>
```

### Cloudbees clickstart

The fastest way to create a SimpleWeb4j application is to use clickstart.

All clickstart contain a sample of Beers CRUD with :

    - Git repository
    - Jenkins instance.
    - Database support.
    - Test of REST services
    - Test of GUI with fluentlenium.

Clickstart with mysql : [github.com/CloudBees-community/simpleweb4j-clickstart](https://github.com/CloudBees-community/simpleweb4j-clickstart)

<a href="https://grandcentral.cloudbees.com/?CB_clickstart=https://raw.github.com/CloudBees-community/simpleweb4j-clickstart/master/clickstart.json"><img src="https://d3ko533tu1ozfq.cloudfront.net/clickstart/deployInstantly.png"/></a>

Clickstart with mongo : [github.com/CloudBees-community/simpleweb4j-mongo-clickstart](https://github.com/CloudBees-community/simpleweb4j-mongo-clickstart)

<a href="https://grandcentral.cloudbees.com/?CB_clickstart=https://raw.github.com/CloudBees-community/simpleweb4j-mongo-clickstart/master/clickstart.json"><img src="https://d3ko533tu1ozfq.cloudfront.net/clickstart/deployInstantly.png"/></a>


### Your first application

Create you main class :
```java
package fr.mygroup;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;

public class MyApplication {

    public static void startServer(int port) {
        setPort(port);
        // Put all your static resource under a package named "fr.mygroup.public"
        setPublicResourcesPath("/fr/mygroup/public");
        // Rest resource sample
        resource(new RestResource<String>("resource", String.class) {
            @Override
            public String getById(String id) throws HttpErrorException {
                return "myResource";
            }

            @Override
            public List<String> getAll() throws HttpErrorException {
                return new ArrayList<String>();
            }

            @Override
            public void update(String id, String resource) throws HttpErrorException {
            }

            @Override
            public String create(String resource) throws HttpErrorException {
                return resource;
            }

            @Override
            public void delete(String id) throws HttpErrorException {
            }
        });
        // Get sample
        get(new Route<Void, String>("/hello", Void.class) {
            @Override
            public Response<String> handle(Void param, RouteParameters routeParams) {
                return new Response<>("Hello World");
            }
        });

        start();
    }

    public static void main(String[] args) {
        // Start the server on port 9999.
        startServer(9999);
    }
}
```

After started server, you will see "Hello World" is you access to "http://localhost:9999"

Now, just add you static files under the package set with "setPublicResourcesPath", and start using angular.js or what ever you want.

### Static files embedded with SimpleWeb4j

SimpleWeb4j comes with angular, jquery and twitter bootstrap. All the resources of SimpleWeb4j are under the package [fr.ybonnel.simpleweb4j.public](https://github.com/ybonnel/SimpleWeb4j/tree/master/src/main/resources/fr/ybonnel/simpleweb4j/public)

### Use maven archetype

In order to create a project very quickly, you can use the archetype fr.ybonnel:simpleweb4j-archetype:0.0.4.
```
mvn archetype:generate -DarchetypeGroupId=fr.ybonnel -DarchetypeArtifactId=simpleweb4j-archetype -DarchetypeVersion=0.0.4 -DgroupId=com.exemple -DartifactId=myapp -Dpackage=com.exemple.myapp -DclassName=MyApp -DwithHibernate=true -Dversion=0.0.1-SNAPSHOT
```

Archetype parameters :

- groupId : your groupId.
- artifactId : your artifactId.
- package (default = groupId) : the main package of your application.
- className (default = "HelloWorld") : the name of your main class.
- withHibernate (default = "true") : true if you want the entity managment, false otherwize.
- version : your version.


## Samples

I've created three samples which can be found in the test resources under the package "fr.ybonnel.simpleweb4j.samples".

All samples are directly inspired by play framework, which is a great web framework.

## Build status

[![Build Status](https://simpleweb4j.ci.cloudbees.com/job/SimpleWeb4j-build/badge/icon)](https://simpleweb4j.ci.cloudbees.com/job/SimpleWeb4j-build/)

## Other documentation

You can find all the generated maven site in [jenkins](https://simpleweb4j.ci.cloudbees.com/job/SimpleWeb4j-build/site/)

## Contributors

 - [Yan Bonnel](https://twitter.com/ybonnel) (principal maintainer)
 - [Nicolas De Loof](https://twitter.com/ndeloof) (first creation of clickstart)
 - [Pierre Leresteux](https://twitter.com/pierreLeresteux) (creation of logo)
 - [Damien Gouyette](https://twitter.com/cestpasdur) (creation of ContentType logic)

