package com.ydsw.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author zhangkaifei
 * @description: TODO
 * @date 2024/11/26  17:45
 * @Version 1.0
 */
@Slf4j
@ControllerAdvice
public class FileExceptionHandler {
    //
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    @ResponseBody
//    public ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
//        // 记录日志（通常在生产环境中不会将堆栈跟踪直接返回给客户端）
//        // log.error("上传文件大小超过限制", ex);
//
//        // 构建错误信息并返回
//        return new ResponseEntity<>("上传文件大小超过限制，请重新上传小于允许大小的文件。", HttpStatus.REQUEST_ENTITY_TOO_LARGE);
//    }
//    // 你还可以添加其他异常的全局处理，例如：
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleException(Exception ex) {
//        return new ResponseEntity<>("发生未知错误，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    @ExceptionHandler(SizeLimitExceededException.class)
//    @ResponseBody
//    public ResultTemplate<Object> handleSizeLimitExceededException(SizeLimitExceededException ex) {
//        System.out.println(ex.getMessage());
//        return ResultTemplate.fail("上传的文件大小超过限制，请重新上传小于允许大小的文件。");
//    }
//    @ExceptionHandler(SizeLimitExceededException.class)
//    @ResponseBody
//    public void handleSizeLimitExceededException(HttpServletRequest request, HttpServletResponse response,SizeLimitExceededException ex) throws IOException {
//        System.out.println(ex.getMessage());
//        //设置客户端响应的内容类型
//        response.setContentType("application/json;charset=utf-8");
//        //获取输出流
//        ServletOutputStream outputStream = response.getOutputStream();
//        String result =JSONUtil.toJsonStr( ResultTemplate.fail("上传的文件大小超过限制，请重新上传小于允许大小的文件。"));
//        outputStream.write(result.getBytes(StandardCharsets.UTF_8));
//        outputStream.flush();
//        outputStream.close();
//    }
}
