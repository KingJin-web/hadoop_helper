package com.king.hadoop_helper.controller;


import com.king.hadoop_helper.entity.HdfsFileStatus;
import com.king.hadoop_helper.service.HdfsServiceImpl;
import com.king.hadoop_helper.util.EmptyUtil;
import com.king.hadoop_helper.util.FileUtil;
import com.king.hadoop_helper.util.HdfsUtil;
import com.king.hadoop_helper.vo.JsonLayui;
import com.king.hadoop_helper.vo.JsonModel;
import com.king.hadoop_helper.vo.ResultObj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @program: kingcloud
 * @description: hadoopHdfs
 * @author: King
 * @create: 2021-05-30 21:26
 */
@RestController
@Api(value = "Hdfs文件接口", tags = {"Hdfs文件接口"})
public class HdfsController {
    @Autowired
    private HdfsServiceImpl hdfsService;


    //日志
    private static final Logger logger = LoggerFactory.getLogger(HdfsController.class);

    /**
     * 获取用户目录下或指定目录下的文件
     *
     * @param path
     * @return
     */
    @RequestMapping(value = "/getFile", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取用户下的文件目录", notes = "User")
    @ApiImplicitParam(name = "path", value = "目录", required = false)
    public ResultObj gstFile(String path) {
        List<HdfsFileStatus> list = hdfsService.query("", path);
        if (list.size() < 1) {
            HdfsFileStatus hdfsFileStatus = new HdfsFileStatus();
            hdfsFileStatus.setName("这是一个空文件夹");
            hdfsFileStatus.setIsDirectory(false);
            hdfsFileStatus.setFileSize(null);
            hdfsFileStatus.setAccess_time(" ");
            list.add(hdfsFileStatus);
        }

        return ResultObj.success(list);

    }

    /**
     * 获取用户目录下或指定目录下的文件
     *
     * @return
     */
    @RequestMapping(value = "/getAllFile", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取用户下的所有文件", notes = "User")

    public ResultObj gstAllFile(String path) {


        List<HdfsFileStatus> list = hdfsService.queryAll(path);

        return ResultObj.success(list);
    }

    /**
     * 获取用户目录下或指定目录下的文件
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "/getAllFileType", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取用户下指定文件类型文件", notes = "file")
    @ApiImplicitParam(name = "type", value = "type", required = true)
    public JsonLayui gstAllFileType(int type, int page, int limit) {
        JsonLayui js = new JsonLayui();

//        String name = redisUtil.getValue(session.getId(), "name");

        List<HdfsFileStatus> list = hdfsService.queryAllType("", type);
        int size = list.size();
        int a = (page - 1) * limit; //开始行数
        int b = page * limit; //结束行数
        if (b > size) {
            b = size;
        }
        list = list.subList(a, b);
        js.setCode(0);
        js.setData(list);
        js.setCount(size);
        return js;
    }

    /**
     * 新建文件夹
     *
     * @param dirPath 带路径的文件名
     * @return
     */
    @RequestMapping(value = "/newDir", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "新建文件夹", notes = "新建")
    @ApiImplicitParam(name = "dirPath", value = "dirPath", required = true)
    public ResultObj newDir(String dirPath) {

        //String name = (String) session.getAttribute("name");
//        String name = redisUtil.getValue(session.getId(), "name");
        logger.info(dirPath);
        if (hdfsService.mkdir("", dirPath)) {
            return ResultObj.success("新建成功");
        } else {

            return ResultObj.error("新建失败!");
        }

    }


    @RequestMapping(value = "/newFile", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "新建文件", notes = "新建")
    @ApiImplicitParam(name = "FilePath", value = " FilePath", required = true)
    public ResultObj newFile(String FilePath) {
        return ResultObj.success("TOODO");
    }

    /**
     * 修改文件名
     *
     * @param path    路径
     * @param oldName 文件名
     * @param newName 新的文件名
     * @return
     */
    @RequestMapping(value = "/changeFileName", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "重命名文件", notes = "重命名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "path", required = false),
            @ApiImplicitParam(name = "oldName", value = "oldName", required = true),
            @ApiImplicitParam(name = "newName", value = "newName", required = true)
    })
    public ResultObj changeFileName(String path, String oldName, String newName) {

        if (path == null || oldName == null || newName == null) {
            return ResultObj.error("参数不能为空!");

        }
        //String name = (String) session.getAttribute("name");
        logger.info(" " + path + " " + oldName + " " + newName);
        if (hdfsService.changeFileName("", path, oldName, newName)) {

            return ResultObj.success("修改成功!");
        } else {

            return ResultObj.error("修改失败!");
        }

    }

    @RequestMapping(value = "/delFile", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "删除文件", notes = "删除")
    @ApiImplicitParam(name = "path", value = " path", required = true)
    public ResultObj delFile(String path) {
        // String name = (String) session.getAttribute("name");

        if (hdfsService.delete("", path)) {
            return ResultObj.success("删除成功!");
        } else {
            return ResultObj.error("删除失败!");
        }


    }

    @RequestMapping(value = "/downFile", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "下载文件", notes = "下载")
    @ApiImplicitParam(name = "path", value = " path", required = true)
    public ResponseEntity<InputStreamResource> downFile(@RequestParam("path") String path) {

        logger.info(" " + path);

//        String name = redisUtil.getValue(session.getId(), "name");
        ResponseEntity<InputStreamResource> res = null;
        try {

            res = hdfsService.downFile("", path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }


    @RequestMapping(value = "/lookPhoto", method = RequestMethod.GET)
    @ApiOperation(value = "查看图片", notes = "查看")
    @ApiImplicitParam(name = "path", value = " path", required = true)
    public ResultObj lookPhoto(String path, HttpServletResponse resp) throws IOException {

        if (EmptyUtil.isEmpty(path)) {
            return ResultObj.error("参数不能为空!");
        } else {
            //  String name = redisUtil.getValue(session.getId(), "name");
            path = "/" + path;
            hdfsService.outputImage(resp, path);
            return ResultObj.success("查看成功!");
        }

    }


    /**
     * 查看文档
     *
     * @param path
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/lookDoc", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "查看文本文件", notes = "查看")
    @ApiImplicitParam(name = "path", value = " path", required = true)
    public JsonLayui lookDoc(String path) throws IOException {
        JsonLayui jsonLayui = new JsonLayui();
        if (EmptyUtil.isEmpty(path)) {
            jsonLayui.setCode(0);
            jsonLayui.setMsg("ERROR");
        } else {
            //String name = redisUtil.getValue(session.getId(), "name");
            path = "/" + path;
            jsonLayui.setCode(1);
            jsonLayui.setData(hdfsService.lookDoc(path));
            jsonLayui.setMsg("OK");
        }
        return jsonLayui;
    }

    /**
     * 下载文件夹
     *
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/downDir", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downDir(String path, HttpServletRequest request) {
        //String name = redisUtil.getValue(session.getId(), "name");
        ResponseEntity<InputStreamResource> result = null;
        try {
            String filename = hdfsService.getFileName(new Path(path));
            path = "/" + path;
            result = FileUtil.downDir(path, filename, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取 hadoop 服务器信息
     *
     * @return
     */
    @RequestMapping(value = "/LookHdfs", method = RequestMethod.GET)
    public JsonLayui LookHdfs() {
        JsonLayui js = new JsonLayui();


        return js;
    }
}
