.. _retryDesign:

Messaging Retry Design
======================

:ref:`Go Home <index>`

Terms
-----

Greylist - Short-term store for services marked as potentially failed
Redlist - Very short-term store to prevent the same failed service from being used immediately after
Blacklist - Long-term store for failed services.  Entries expire slowly

Phase 1 - Find a Service Instance
---------------------------------

  1. Check the socket pool for open, unused sockets.  If we have one, return it
  2. Get a List of Service Instances from the Service Manager
  3. Get a random number (seed value = time of program startup)
  4. For each Service in the list (random number from step 2 as start index):
    a. If the service is not in use, not in the blacklist, and not in the redlist return it
    b. Else, return null

Phase 2 - Sending Message
-------------------------

  1. Send message
  2. On success we continue, on failure we move to phase 3

Phase 3 - After Failure
-----------------------

  1. Re-try on same socket (timeout and # of retries configurable)
  2. If all attempts are unsuccessful, then report failure
    a. If instance not in greylist or blacklist, add to greylist and redlist
    b. If instance in greylist and not blacklist, add to blacklist
    c. Remove failed socket from socket pool
  3. Return to Phase 1
