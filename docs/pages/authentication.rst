.. _auth:

Authenticating with Adrestia
============================

:ref:`Go Home <index>`

Adrestia provides JWT authentication with Spring Security, and can authenticate
from a browser with cookies, or from other applications using a Bearer token.
To test out authentication, you can visit http://adrestia-address:8080/login,
making sure to replace 'adrestia-address' with the address of your instance.

The Authentication options in application.properties expose the ability to turn
on/off authentication, as well as provide an initial admin user and password.
Currently, only administrator users can create other users, via the v1/users/sign-up
endpoint.
