package com.waffelmonster.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppTest {
  @Test
  public void successfulResponse() {
    final App app = new App();
    final String map = Stream.of(
            "002?200000",
            "002?211100",
            "122????100",
            "?????31100",
            "?????20000",
            "??11110000",
            "??20001110",
            "??20001?10"
    ).map(line -> '"' + line + '"').collect(Collectors.joining(",", "[", "]"));
    final APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
    request.setBody(String.format("{\"bombs\":10,\"map\":%s}", map));
    final APIGatewayProxyResponseEvent result = app.handleRequest(request, null);
    assertEquals(result.getStatusCode().intValue(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"chance\""));
    assertTrue(content.contains("\"x\""));
    assertTrue(content.contains("\"y\""));
  }
}
