package com.ydsw.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class ImageController {

    private static final String PYTHON_UPLOAD_FOLDER =  "/usr/soft/heigangkoumodel/code/uploads";
    private static final String PYTHON_OUTPUT_FOLDER = "./outputs";

    @PreAuthorize("hasAnyAuthority('api_water_process')")
    @PostMapping("/api/water/process")
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
            String resultFilePath = (String) response.getBody().get("result_tif_path");
            String previewFilePath = (String) response.getBody().get("preview_png_path");

            if (resultFilePath == null || resultFilePath.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Missing result_tif_path in Python response.");
            }

            // 6. 返回结果（包括预览图URL）
            Map<String, String> responseData = new HashMap<>();
            responseData.put("task_id", taskId);
            responseData.put("result_url", "/api/results/" + taskId + ".tif");

            if (previewFilePath != null && !previewFilePath.isEmpty()) {
                responseData.put("preview_url", "/api/previews/" + taskId + ".png");
            }

            return ResponseEntity.ok(responseData);

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

    @GetMapping("/api/results/{taskId}.tif")
    public ResponseEntity<Resource> getResultFile(@PathVariable String taskId) {
        try {
            Path resultPath = Paths.get(PYTHON_OUTPUT_FOLDER, "result_" + taskId + ".tif");
            File resultFile = resultPath.toFile();

            if (!resultFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("image/tiff"));
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename(resultFile.getName())
                    .build());
            headers.setContentLength(resultFile.length());

            return new ResponseEntity<>(
                    new FileSystemResource(resultFile),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/previews/{taskId}.png")
    public ResponseEntity<Resource> getPreviewImage(@PathVariable String taskId) {
        try {
            Path previewPath = Paths.get(PYTHON_OUTPUT_FOLDER, "preview_" + taskId + ".png");
            File previewFile = previewPath.toFile();

            if (!previewFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDisposition(ContentDisposition
                    .inline()
                    .filename(previewFile.getName())
                    .build());
            headers.setContentLength(previewFile.length());

            return new ResponseEntity<>(
                    new FileSystemResource(previewFile),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}