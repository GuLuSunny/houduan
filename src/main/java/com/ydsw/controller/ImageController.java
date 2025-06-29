package com.ydsw.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
public class ImageController {

    private static final String PYTHON_UPLOAD_FOLDER = "D:\\heigankoumodel\\基于光学和SAR图像融合的水网提取\\code\\Unet部分\\code\\uploads";
    private static final String PYTHON_OUTPUT_FOLDER = "./outputs";

    @PostMapping("/api/image/process")
    public ResponseEntity<?> processImage(@RequestBody Map<String, Object> request) {
        try {
            // 1. 验证请求参数
            String fileUrl = request.get("fileUrl").toString();
            if (fileUrl == null || fileUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("Missing fileUrl");
            }

            // 2. 准备文件和目录
            String taskId = UUID.randomUUID().toString();
            File originalFile = new File(fileUrl);
            String filename = originalFile.getName();

            // 3. 将文件复制到Python的上传目录
            Path uploadPath = Paths.get(PYTHON_UPLOAD_FOLDER, taskId);
            Files.createDirectories(uploadPath);
            Path destination = uploadPath.resolve(filename);
            Files.copy(originalFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            // 4. 调用Python服务
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> body = Map.of(
                    "task_id", taskId,
                    "filename", filename
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:5000/api/process",
                    body,
                    Map.class
            );

            if (response.getBody() == null || !"success".equals(response.getBody().get("status"))) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getBody());
            }

            // 5. 获取Python服务返回的结果文件路径
            String resultFilePath = (String) response.getBody().get("result_file_path");
            if (resultFilePath == null || resultFilePath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Missing result_file_path in Python response.");
            }

            Path resultPath = Paths.get(resultFilePath);
            if (!Files.exists(resultPath)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Result file not found.");
            }

            File resultFile = resultPath.toFile();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("image/tiff"));
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(resultPath.getFileName().toString())
                    .build());
            headers.setContentLength(resultFile.length());

            return new ResponseEntity<>(
                    new FileSystemResource(resultFile),
                    headers,
                    HttpStatus.OK
            );

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File operation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}
