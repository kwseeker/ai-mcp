package top.kwseeker.mcp.server;

import java.util.ArrayList;
import java.util.List;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

// MCP Server Tool 案例
// 加法计算器
public class Calculator {

    public static void main(String[] args) {
        System.out.println("Calculator MCP Server Start ...");
        // 1 传输层实现
        // 创建传输层提供者，现在支持两种协议 Stdio 和 Http SSE, 这里暂时先使用 Stdio
        StdioServerTransportProvider stdioTransportProvider = new StdioServerTransportProvider();
        // 2 MCP Server 信息
        McpSchema.Implementation serverInfo = new McpSchema.Implementation("calculator-server", "1.0.0");
        // 3 MCP Server 建造者
        // SyncSpecification 其实是一个建造者类型
        McpServer.SyncSpecification serverBuilder = McpServer.sync(stdioTransportProvider).serverInfo(serverInfo);
        // 向建造者类型中装配组件：Tool Resource Prompt
        // 3.1 装配 Tools
        List<McpServerFeatures.SyncToolSpecification> tools = getSyncToolSpecifications();
        serverBuilder.tools(tools);
        // 3.2 还可以装配 Resource Prompt
        // 4 配置 MCP Server 能力
        McpSchema.ServerCapabilities.Builder capabilitiesBuilder = new McpSchema.ServerCapabilities.Builder();
        capabilitiesBuilder.tools(true);    // tools 变更会通知客户端
        serverBuilder.capabilities(capabilitiesBuilder.build());
        // 5 构建并启动 MCP Server
        McpSyncServer mcpSyncServer = serverBuilder.build();

        for(;;) {
            Thread.yield();
        }
    }

    private static List<McpServerFeatures.SyncToolSpecification> getSyncToolSpecifications() {
        List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();
        // 定义工具调用参数格式，用于调用前校验
        // 关于 JSON Schema, 参考：https://json-schema.apifox.cn/
        // Spring AI 将 JSON Schema 这部分细节完全封装了，实现了自动根据 Method 生成对应的 JSON Schema 字符串可以参考：
        // https://github.com/spring-projects/spring-ai/tree/main/spring-ai-core/src/main/java/org/springframework/ai/util/json
        // 比如：JsonSchemaGenerator.java
        String jsonSchema = """
                            {
                                "$schema": "https://json-schema.org/draft/2020-12/schema",
                                "type": "object",
                                "properties": {
                                    "a": {
                                        "type": "number",
                                        "format": "double"
                                    },
                                    "b": {
                                        "type": "number",
                                        "format": "double"
                                    }
                                },
                                "required": [
                                    "a",
                                    "b"
                                ],
                                "additionalProperties": false
                            }
                            """;
        var tool = new McpSchema.Tool("add", "执行加法运算", jsonSchema);
        var addTool = new McpServerFeatures.SyncToolSpecification(tool,
                // call
                (exchange, summands) -> {
                    double a = ((Number) summands.get("a")).doubleValue();
                    double b = ((Number) summands.get("b")).doubleValue();
                    double sum = a + b;
                    String result = String.format("%.2f + %.2f = %.2f", a, b, sum);
                    return new McpSchema.CallToolResult(List.of(new TextContent(result)), false);
                });
        tools.add(addTool);
        return tools;
    }
}
