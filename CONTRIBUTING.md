# Contributing to Androscope

The project consists of 2 parts:
- Android library (lib-androscope module) and sample app.
- Angular template (template folder).

Compiled template used by Android library is NOT pushed into version control system. You need to compile it before running Androscope (and after modifications):

```
./gradlew buildTemplate
```

You need to have [Angular](https://angular.io/guide/setup-local) installed with its prerequisites to be able to compile the template.

It is convenient to work on the web template on desktop using the API provided by Android app. To do this update the constant RestService.ROOT in the template with the IP address of Androscope (don't commit this change!). Then you can run the template in your desktop browser. Keep in mind that some requests (PUT, POST, DELETE) might not work with this approach because of different hosts.

We use official formatting for Kotlin code and automatic default formatting in WebStorm for TypeScript/HTML/CSS.
