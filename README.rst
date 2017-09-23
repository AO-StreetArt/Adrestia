Adrestia
========

.. figure:: https://travis-ci.org/AO-StreetArt/Adrestia.svg?branch=master
   :alt:

 Overview
 --------

 Adrestia is the Gateway Service for the Aesel Distributed Visualization platoform.

 This service is intended to fill a small role within a larger
 architecture designed to synchronize 3D objects across different client
 programs. It is highly scalable, and many instances can run in parallel
 to support increasing load.

 Features
 --------

 - Expose HTTP API to devices and translate between underlying programs
 - Connect to other services over Zero MQ using JSON or Google Protocol Buffers.
 - Configurable Logic
 - Scalable microservice design

 Adrestia is a part of the AO Aesel Project, along with
 `Crazy Ivan <https://github.com/AO-StreetArt/CrazyIvan>`__
& `CLyman <https://github.com/AO-StreetArt/CrazyIvan>`__.  It therefore
 utilizes the `DVS Interface
 library <https://github.com/AO-StreetArt/DvsInterface>`__, also
 available on github. It utilizes the Obj3.proto file for inbound
 communications when configured to read protocol buffers.