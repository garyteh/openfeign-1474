# OpenFeign Issue #1474

## Problem Statement

When obtaining the `feign.Response`, Feign delegates control to the caller, shifting the responsibility for handling the response, including error management, proper body closure, and other related tasks.

## Reproducing the Issue

To replicate the issue, follow these steps:

1. Start the application using the command: `./gradlew bootRun`
2. Open a terminal window and execute the following command three times: `curl -i localhost:8080/`. On the third attempt, the response should be an Internal Server Error (500).

## Application Functionality

The purpose of this application is to facilitate easy bug replication by implementing the following measures:

- Limit the maximum connections per route.
- Reduce the connection request timeout.
- Expose a root endpoint `http://localhost:8080/` that:
  - Initiates a POST request to `https://pie.dev/response-headers?Transfer-Encoding=chunked` to simulate a `Transfer-Encoding: chunked` response.
  - Sets the return type as `feign.Response` but disregards it.
  - Returns an OK response (200).

## Bug Explanation

The bug occurs due to the omission of the `Content-Length` header when the server responds with `Transfer-Encoding: chunked`. Consequently, Feign does not adequately close the body within the `feign.ResponseHandler.disconnectResponseBodyIfNeeded` function.

This issue leads to connections not being closed correctly, as observed through the `lsof` command: `lsof -Pnl -i | grep $(jcmd | grep com.atlassian.openfeign1474.Openfeign1474Application | cut -d' ' -f1)`. In this scenario, two connections are likely to get stuck at `CLOSE_WAIT` state due to the improper release of resources (connections). For further context, refer to [this ticket](https://issues.apache.org/jira/browse/HTTPCLIENT-1918), where a contributor to the Apache HTTP client emphasized the importance of correctly releasing resources associated with response objects.
