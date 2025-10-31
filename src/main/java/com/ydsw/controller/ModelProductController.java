package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelProduct;
import com.ydsw.service.ModelProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Slf4j
public class ModelProductController {
    @Autowired
    private ModelProductService modelProductService;

    @PostMapping(value = "/api/modelFile/getAllProductByConditions")
    public ResultTemplate<Object> getAllProductByConditions(@RequestBody JSONObject jsonObject) {
        ModelProduct modelProduct = JSONUtil.toBean(jsonObject, ModelProduct.class);
        List<Map<String,Object>> res = modelProductService.getModelProductByCondition(modelProduct);
        return ResultTemplate.success(res);

    }

    @PostMapping(value = "/api/modelFile/getFilesByConditions")
    public ResponseEntity<Resource> getFilesByConditions(@RequestBody JSONObject jsonObject) {
        ModelProduct modelProduct = JSONUtil.toBean(jsonObject, ModelProduct.class);
        List<Map<String,Object>> res = modelProductService.getModelProductByCondition(modelProduct);
        if(res != null && !res.isEmpty()) {
            Map<String,Object> map = res.get(0);
            String filepath = map.get("filepath").toString();
            return getFileDownloadResponse(filepath,"");
        }else
        {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping(value = "/api/modelFile/deleteByConditions")
    public ResultTemplate<Object> deleteByConditions(@RequestBody JSONObject jsonObject) {
        ModelProduct modelProduct = JSONUtil.toBean(jsonObject, ModelProduct.class);
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        try {
            modelProductService.updateModelProduct(idList,modelProduct);
        }
        catch (Exception e) {
            return ResultTemplate.fail(e.getMessage());
        }
        return ResultTemplate.success();
    }

    @PostMapping(value = "/api/modelFile/insertModelProducts")
    public ResultTemplate<Object> insertModelProducts(@RequestBody JSONArray jsonArray) {
        List<ModelProduct> modelProductList = jsonArray.toList(ModelProduct.class);
        for (ModelProduct modelProduct : modelProductList) {
            modelProduct.setCreateTime(new Date());
        }
        try {
            modelProductService.saveBatch(modelProductList);
        } catch (Exception e) {
            return ResultTemplate.fail(e.getMessage());
        }
        return ResultTemplate.success();
    }
    // 通用方法：获取文件下载响应（自动检测Content-Type）
    public ResponseEntity<Resource> getFileDownloadResponse(String filePath, String fileName) {
        try {
            Path path = Paths.get(filePath).resolve(fileName).normalize();
            File file = path.toFile();

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            // 自动检测文件类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
