package com.waffelmonster.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import net.zomis.minesweeper.analyze.factory.General2DAnalyze;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers","Content-Type");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,GET");
        
        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            final MinesweeperRequest request = gson.fromJson(input.getBody(), MinesweeperRequest.class);
            final General2DAnalyze analyze = new General2DAnalyze(request.map, request.bombs);
            final List<Probability> result = analyze.solve()
                    .analyzeDetailed(analyze)
                    .getProxies()
                    .stream()
                    .map(element -> new Probability(element.getField().getY(), element.getField().getX(), element.getMineProbability()))
                    .collect(Collectors.toList());
            return response
                    .withStatusCode(200)
                    .withBody(gson.toJson(result));
        } catch (Exception e) {
            return response
                    .withBody("[]")
                    .withStatusCode(500);
        }
    }

    static class MinesweeperRequest {
        int bombs;
        String[] map;
    }

    static class Probability {
        int x;
        int y;
        double chance;

        public Probability(int x, int y, double chance) {
            this.x = x;
            this.y = y;
            this.chance = chance;
        }
    }

}
