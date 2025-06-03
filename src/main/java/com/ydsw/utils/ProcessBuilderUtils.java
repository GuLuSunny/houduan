package com.ydsw.utils;

import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Data
public class ProcessBuilderUtils {
    // 可配置的Python路径
    private static String pythonPath = "D:\\Ananconda3\\envs\\heigangkouenv\\python.exe";

    // 默认编码（可根据系统调整）
    private static String defaultEncoding = "GBK";

    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(10);

    /**
     * 专为日志监控设计的执行方法（解决中文乱码问题）
     */
    public static void executeWithLogMonitoring(String scriptPath, List<String> args,
                                               Map<String, String> envVars,
                                               String infoencoding) {
        if (infoencoding != null) {
            defaultEncoding=infoencoding;
        }

        try {
            // 构建命令
            List<String> command = new ArrayList<>();
            command.add(pythonPath);
            command.add(scriptPath);
            if (args != null) {
                command.addAll(args);
            }

            ProcessBuilder pb = new ProcessBuilder(command);

            // 设置工作目录
            File workingDir = new File(scriptPath).getParentFile();
            if (workingDir != null) {
                pb.directory(workingDir);
            }

            // 添加环境变量
            if (envVars != null) {
                pb.environment().putAll(envVars);
            }
            // 强制无缓冲输出
            pb.environment().put("PYTHONUNBUFFERED", "1");

            // 关键：将错误流重定向到标准输出
            pb.redirectErrorStream(true);

            // 启动进程
            Process process = pb.start();

            // 使用线程池并行处理输出
            ExecutorService executor = Executors.newFixedThreadPool(1);

            // 启动输出处理线程（指定编码）
            executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), defaultEncoding))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 输出时使用正确的编码
                        System.out.println("[PYTHON LOG] " + line);
                    }
                } catch (IOException e) {
                    System.err.println("日志读取错误: " + e.getMessage());
                }
            });

            // 等待进程结束
            int exitCode = process.waitFor();

            // 关闭线程池
            executor.shutdown();

            if (exitCode != 0) {
                throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("执行失败: " + e.getMessage(), e);
        }
    }

    // 重载方法（使用默认编码）
    public static void executeWithLogMonitoring(String scriptPath, List<String> args,
                                                Map<String, String> envVars) {
        executeWithLogMonitoring(scriptPath, args, envVars, null);
    }

    // 重载方法（无环境变量）
    public static void executeWithLogMonitoring(String scriptPath, List<String> args) {
        executeWithLogMonitoring(scriptPath, args, null, null);
    }

    // 重载方法（仅执行）
    public static void executeWithLogMonitoring(String scriptPath) {
        executeWithLogMonitoring(scriptPath, null, null, null);
    }
    /**
     * 通用Python脚本执行方法（解决中文乱码）
     */
    public static void executePythonScript(String scriptPath, List<String> args,
                                             Map<String, String> envVars, String encoding) {
        if (encoding == null) {
            encoding = defaultEncoding;
        }

        try {
            // 构建命令列表
            List<String> command = new ArrayList<>();
            command.add(pythonPath);
            command.add(scriptPath);
            if (args != null) {
                command.addAll(args);
            }

            ProcessBuilder pb = new ProcessBuilder(command);

            // 设置工作目录
            File workingDir = new File(scriptPath).getParentFile();
            if (workingDir != null) {
                pb.directory(workingDir);
            }

            // 添加环境变量
            if (envVars != null) {
                Map<String, String> env = pb.environment();
                env.putAll(envVars);
            }

            // 强制无缓冲输出
            pb.environment().put("PYTHONUNBUFFERED", "1");

            // 关键：将错误流重定向到标准输出
            pb.redirectErrorStream(true);

            // 启动进程
            Process process = pb.start();

            // 使用线程池并行处理输出
            ExecutorService executor = Executors.newFixedThreadPool(1);

            // 读取标准输出（指定编码）

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), encoding))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 输出时使用正确的编码
                    System.out.println("[PYTHON LOG] " + line);
                }
            }

            // 读取错误流（指定编码）
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), encoding))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println("日志读取错误: " + errorLine);
                }
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException(
                        "Python脚本执行失败，退出码: " + exitCode +
                                "\n错误信息: " + errorOutput
                );
            }


        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("调用Python脚本时发生错误: " + e.getMessage(), e);
        }
    }

    // 重载方法（使用默认编码）
    public static void executePythonScript(String scriptPath, List<String> args,
                                             Map<String, String> envVars) {
        executePythonScript(scriptPath, args, envVars, defaultEncoding);
    }

    // 重载方法（无环境变量）
    public static void executePythonScript(String scriptPath, List<String> args) {
        executePythonScript(scriptPath, args, null, defaultEncoding);
    }

    // 重载方法（无命令及环境变量）
    public static void executePythonScript(String scriptPath) {
        executePythonScript(scriptPath, null, null, defaultEncoding);
    }

    /**
     * 带实时输出的执行方法（解决中文乱码）
     */
    public static void executeWithRealTimeOutput(String scriptPath, List<String> args,
                                                 String encoding) {
        if (encoding == null) {
            encoding = defaultEncoding;
        }

        try {
            List<String> command = new ArrayList<>();
            command.add(pythonPath);
            command.add(scriptPath);
            if (args != null) {
                command.addAll(args);
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            File workingDir = new File(scriptPath).getParentFile();
            if (workingDir != null) {
                pb.directory(workingDir);
            }

            pb.redirectErrorStream(true);  // 合并错误流到标准输出
            Process process = pb.start();

            // 实时读取输出（指定编码）
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), encoding))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python Output] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("执行失败: " + e.getMessage(), e);
        }
    }

    // 重载方法（使用默认编码）
    public static void executeWithRealTimeOutput(String scriptPath, List<String> args) {
        executeWithRealTimeOutput(scriptPath, args, defaultEncoding);
    }

    // 兼容旧方法
    public static void executeWithRealTimeOutput(String pythonScriptPath) {
        executeWithRealTimeOutput(pythonScriptPath, null, defaultEncoding);
    }
    /**
     * 后台执行Python脚本（完全异步，不关心结果）
     *
     * @param scriptPath Python脚本路径
     * @param args       传递给Python脚本的参数
     * @param envVars    环境变量（可选）
     */
    public static void executeInBackground(String scriptPath, List<String> args,
                                           Map<String, String> envVars) {
        executorService.submit(() -> {
            try {
                // 构建命令
                List<String> command = new ArrayList<>();
                command.add(pythonPath);
                command.add(scriptPath);
                if (args != null) {
                    command.addAll(args);
                }

                ProcessBuilder pb = new ProcessBuilder(command);

                // 设置工作目录
                File workingDir = new File(scriptPath).getParentFile();
                if (workingDir != null) {
                    pb.directory(workingDir);
                }

                // 添加环境变量
                if (envVars != null) {
                    pb.environment().putAll(envVars);
                }

                // 关键环境变量设置
                pb.environment().put("PYTHONIOENCODING", "UTF-8");
                pb.environment().put("PYTHONUNBUFFERED", "1");
                pb.environment().put("PYTHONUTF8", "1");  // Python 3.7+ 额外保险

                // 重定向输出到日志文件（可选）
                File logFile = new File(workingDir, "python_background.log");
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
                pb.redirectError(ProcessBuilder.Redirect.appendTo(logFile));

                // 启动进程（不等待结束）
                Process process = pb.start();

                // 仅记录进程启动信息
                System.out.println("Python脚本已在后台启动，PID: " + getProcessId(process));

                // 不调用process.waitFor()，让进程自主运行

            } catch (IOException e) {
                System.err.println("后台执行Python脚本失败: " + e.getMessage());
            }
        });
    }

    /**
     * 获取进程ID（跨平台实现）
     */
    private static long getProcessId(Process process) {
        try {
            // Java 9+ 直接支持
            return process.pid();

            // 对于旧版Java，使用反射或平台特定方法
        } catch (Exception e) {
            // 忽略错误
        }
        return -1; // 未知PID
    }

    /**
     * 关闭线程池（在应用结束时调用）
     */
    public static void shutdown() {
        executorService.shutdown();
    }

    // 重载方法（简化调用）
    public static void executeInBackground(String scriptPath) {
        executeInBackground(scriptPath, null, null);
    }

    public static void executeInBackground(String scriptPath, List<String> args) {
        executeInBackground(scriptPath, args, null);
    }

}