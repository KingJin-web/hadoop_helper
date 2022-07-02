package com.king.hadoop_helper.controller;

import com.king.hadoop_helper.service.HdfsServiceImpl;
import com.king.hadoop_helper.util.FileUtil;
import com.king.hadoop_helper.util.HdfsUtil;
import com.king.hadoop_helper.vo.JsonModel;
import com.king.hadoop_helper.vo.ResultObj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @program: kingcloud
 * @description:
 * @author: King
 * @create: 2021-06-04 00:30
 */
@RestController
@RequestMapping("/file")
@Api(value = "文件上传接口", tags = {"文件操作接口"})
public class FileUploadController {

    @Value(value = "${file.UploadPath}")
    private String UploadPath;


    private HdfsServiceImpl hdfsService;
    @Autowired
    public void setHdfsService(HdfsServiceImpl hdfsService) {
        this.hdfsService = hdfsService;
    }



    //日志
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);


    public String getUploadPath() {
        return UploadPath;
    }

    /**
     * 实现文件上传
     */
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @RequestMapping(value = "/uploads", method = RequestMethod.POST)
    @ResponseBody
    public ResultObj fileUpload(@RequestParam("fileName") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultObj.error("文件为空");
        }
        String fileName = file.getOriginalFilename();
        int size = (int) file.getSize();
        logger.info(fileName + "-->" + size);


        File dest = new File(UploadPath + "/" + fileName);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest); //保存文件
           return ResultObj.success();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
           return ResultObj.error("文件上传失败");
        }
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */

    @CrossOrigin
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件到hdfs", notes = "上传")
    public ResultObj file(@RequestParam("file") MultipartFile file, String uploadPath) {
        if (file.isEmpty()) {
            return ResultObj.error("文件为空");
        }

//        String name = redisUtil.getValue(session.getId(),"name");
        File path = new File(UploadPath + file.getOriginalFilename());
        try {
            file.transferTo(path);
            Path path1 = new Path(path.getPath());
            hdfsService.upload(path1, "", uploadPath);
            return ResultObj.success();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
           return ResultObj.error("文件上传失败");
        }
    }

    /**
     * 上传文件夹
     *
     * @param files
     * @return
     */
    @RequestMapping(value = "/uploadDir", method = RequestMethod.POST)
    public ResultObj uploadFolder(MultipartFile[] files) {

        if (FileUtil.saveMultiFile(UploadPath, files)) {
            return ResultObj.success();
        } else {
            return ResultObj.error("文件上传失败");
        }

    }



}
