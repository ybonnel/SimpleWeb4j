SimpleWeb4j - A very Simple stack web (inspired by Spark).
==============================================

## Why SimpleWeb4j

My current way to create web application is angular.js on client size, with twitter bootstrap for css, and have json resources on server side.
So I want a very simple framework to help create this sort of web app.

It's why I created SimpleWeb4j, on server side, you can handle http request, in order to create quickly json services, and on client side create an angular.js app.

## Dependencies

- Jetty : used for http server.
- Guava : Util classes (if you don't know it, look at it right now!).
- slf4j : For the logs.
- Hibernate : use for entities managment.
- Reflections : use to find entities.
- H2 : use by default for database (you can exclude it if you use another database or if you don't use databases).

## Licence

All SimpleWeb4j is under the Apache License, Version 2.0


## Usage

### Maven dependency

Soon.

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
            public void create(String resource) throws HttpErrorException {
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

Soon.

## Samples

I've created three samples which can be found in the test resources under the package "fr.ybonnel.simpleweb4j.samples".

All samples are directly inspired by play framework, which is a great web framework.

## Build status

[![Build Status](https://simpleweb4j.ci.cloudbees.com/job/SimpleWeb4j-build/badge/icon)](https://simpleweb4j.ci.cloudbees.com/job/SimpleWeb4j-build/)

## Other documentation

You can find all the generated maven site in [jenkins](https://simpleweb4j.ci.cloudbees.com/job/SimpleWeb4j-build/site/)

## TODO List

- [ ] Deploy SimpleWeb4j in central repository.
- [x] Create a very simple sample usage in readme.
- [ ] Create an archetype.
- [ ] Create a minified process for javascript and css.
- [ ] Add some useful util js for client side.
- [ ] Add sample explanation in wiki.
- [ ] Add a wiki page for each functionnality.
- [ ] Add unit test sample in wiki.
