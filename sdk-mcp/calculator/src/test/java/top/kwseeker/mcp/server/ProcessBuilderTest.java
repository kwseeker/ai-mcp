package top.kwseeker.mcp.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;

// 单元测试中启动进程会失败
public class ProcessBuilderTest {

    // 启动进程正常
    public static void main(String[] args) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("java", "-jar", "sdk-mcp/calculator/target/calculator-1.0-SNAPSHOT-jar-with-dependencies.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process", e);
        }
        System.out.printf("Process info: %s\n, pid: %d", process.info(), process.pid());

        for(;;) {
            Thread.yield();
        }
    }

    // 单元测试中启动进程失败
    @Test
    public void testStartProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("java", "-jar", "sdk-mcp/calculator/target/calculator-1.0-SNAPSHOT-jar-with-dependencies.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start process", e);
        }
        System.out.printf("Process info: %s\n, pid: %d", process.info(), process.pid());

        for(;;) {
            Thread.yield();
        }
    }
}
