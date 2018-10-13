.. _auth:

Authenticating with Adrestia
============================

:ref:`Go Home <index>`

Adrestia utilizes `Auth0 <https://auth0.com>`__ for it's authentication
flows.  This provides a number of integrations, and I suggest referring to their
latest documentation to obtain a better understanding of how this all functions.

Adrestia provides JWT authentication with Spring Security, so is capable of
authenticating from User Databases, social log-ins, etc.  To test out authentication,
you can visit `the login portal <http://aesel-address:8080/portal/home>`__.

You can follow `this guide <https://auth0.com/docs/quickstart/webapp/java-spring-security-mvc/01-login>`__
to setup your Auth0 account to integrate with Adrestia correctly.  The configuration
properties required can be set in application.properties or a separate file called 'auth0.properties'.
