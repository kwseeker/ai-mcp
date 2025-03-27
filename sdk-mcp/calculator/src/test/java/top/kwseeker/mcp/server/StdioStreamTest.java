package top.kwseeker.mcp.server;

import jdk.internal.access.JavaIOFileDescriptorAccess;
import jdk.internal.access.SharedSecrets;
import org.junit.jupiter.api.Test;

import java.io.*;

public class StdioStreamTest {

    // 模拟 StdioServerTransportProvider 中向标准输入（System.in）读数据以及向标准输出（System.out）写数据测试
    @Test
    public void testStdioStreamRW() throws IOException {
        //--add-opens java.base/jdk.internal.access=ALL-UNNAMED
        // 1. MCP Client 向标准输入写入数据
        //ProcessPipeOutputStream clientOs = new ProcessPipeOutputStream(0);
        JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        FileDescriptor fileDescriptor = new FileDescriptor();
        fdAccess.set(fileDescriptor, 60);
        try (FileOutputStream stdin = new FileOutputStream(fileDescriptor)) {
            stdin.write("request message from client!".getBytes());
        }

        // 2. MCP Server 从标准输入读取数据
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        System.out.println("read from System.in: " + line);

        // 3. MCP Server 将处理结果写入标准输出
        //System.out.write("response message after handling!".getBytes());

        // 4. MCP Client 从标准输出读取响应数据

        //System.out.println("read from System.out: " + line);
    }
}
