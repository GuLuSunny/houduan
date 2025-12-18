package com.ydsw.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fengwenyi.api.result.ResultTemplate;
import com.ydsw.domain.ModelProduct;
import com.ydsw.service.ModelProductService;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.M;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class ModelProductController {
    @Autowired
    private ModelProductService modelProductService;

    @PostMapping(value = "/api/modelFile/getAllProductByConditions")
    public ResultTemplate<Object> getAllProductByConditions(@RequestBody JSONObject jsonObject) {
        ModelProduct modelProduct = JSONUtil.toBean(jsonObject, ModelProduct.class);
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        List<Map<String,Object>> res = modelProductService.getModelProductByCondition(idList,modelProduct);
        return ResultTemplate.success(res);

    }

    @PostMapping(value = "/api/modelFile/getFilesByConditions")
    public ResponseEntity<Resource> getFilesByConditions(@RequestBody JSONObject jsonObject) {
        ModelProduct modelProduct = JSONUtil.toBean(jsonObject, ModelProduct.class);
        List<String> idArray = jsonObject.getBeanList("ids", String.class);
        List<Integer> idList = new ArrayList<>();
        ModelFileStatusController.ArrayStrToInt(idArray,idList);
        List<Map<String,Object>> res = modelProductService.getModelProductByCondition(idList,modelProduct);
        if(res != null && !res.isEmpty()) {
            Map<String,Object> map = res.get(0);
            String filepath = map.get("filepath").toString();
            return getFileDownloadResponse(filepath,"");
        }else
        {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping(value = "/api/modelFile/getProductPageByConditions")
    public ResultTemplate<Object> getProductPageByConditions(@RequestBody JSONObject jsonObject) {
        ModelProduct modelProduct = JSONUtil.toBean(jsonObject, ModelProduct.class);
        int currentPage = jsonObject.get("currentPage") == null ? 1 : jsonObject.getInt("currentPage");
        int pageSize = jsonObject.get("pageSize") == null ? 10 : jsonObject.getInt("pageSize");

        IPage<Map<String,Object>> page= modelProductService.getProductPageByConditions(currentPage,pageSize,modelProduct);
        return ResultTemplate.success(page);
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

    @PostMapping(value = "/api/modelFile/uploadModelProducts")
    public ResultTemplate<Object> uploadModelProducts(@RequestParam("productFile") MultipartFile file,
                                                      @RequestParam("className") String className,
                                                      @RequestParam("filename") String filename,
                                                      @RequestParam("userName") String userName,
                                                      @RequestParam("observationTime") String observationTime,
                                                      @RequestParam("startTime") String startTime,
                                                      @RequestParam("endTime") String endTime) {

        try {
            // 1. 获取文件类型（扩展名）
            String originalFilename = file.getOriginalFilename();
            String type = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }

            // 2. 构建保存路径
            String basePath = "D:"+ File.separator+"recognition"+ File.separator+"products";
            String savePath = basePath + File.separator + className + File.separator + type + File.separator + file.getOriginalFilename();

            // 3. 创建目录（如果不存在）
            Path directoryPath = Paths.get(basePath, className, type);
            Files.createDirectories(directoryPath);

            // 4. 保存文件
            Path targetPath = Paths.get(savePath);
            file.transferTo(targetPath.toFile());

            // 5. 时间格式转换（假设时间格式为yyyy-MM-dd HH:mm:ss，如果不是可以调整）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd"); // 如果只需要日期


            // 6. 创建ModelProduct对象并设置属性
            ModelProduct modelProduct = new ModelProduct();
            modelProduct.setClassName(className);
            modelProduct.setType(type); // 设置文件类型
            modelProduct.setFilename(filename);
            modelProduct.setFilepath(savePath); // 保存文件路径
            modelProduct.setOwner(userName);
            modelProduct.setObservationTime(observationTime);
            modelProduct.setStartTime(startTime);
            modelProduct.setEndTime(endTime);
            modelProduct.setCreateTime(new Date());

            // 7. 保存到数据库
            boolean saveResult = modelProductService.save(modelProduct);

            if (!saveResult) {
                return ResultTemplate.fail("保存到数据库失败");
            }

            return ResultTemplate.success();

        } catch (IOException e) {
            return ResultTemplate.fail("文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            return ResultTemplate.fail("处理失败: " + e.getMessage());
        }
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
