package top.kwseeker.mcp.server;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.time.Duration;
import java.util.Map;

public class CalculatorClientTest {

    public static void main(String[] args) {
        ServerParameters serverParameters = ServerParameters
                .builder("java")
                .args("-jar",
                        "sdk-mcp/calculator/target/calculator-1.0-SNAPSHOT-jar-with-dependencies.jar")
                .build();
        var transport = new StdioClientTransport(serverParameters);
        try (var client = McpClient.sync(transport)
                .clientInfo(new McpSchema.Implementation("calculator-client", "1.0.0"))
                .initializationTimeout(Duration.ofSeconds(10))
                .requestTimeout(Duration.ofSeconds(10))
                .build()) {

            // 1 先初始化连接
            client.initialize();

            // 2 List and demonstrate tools
            McpSchema.ListToolsResult toolsList = client.listTools();
            System.out.println("Available Tools = " + toolsList);

            // 3 工具调用
            McpSchema.CallToolResult result = client.callTool(
                    new McpSchema.CallToolRequest("add",
                            Map.of("a", 1.2, "b", 2.3)));
            System.out.println("Result: " + result);
        }

        for (;;) {
            Thread.yield();
        }
    }
}
