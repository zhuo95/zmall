package com.zmall.service.impl;

import com.google.common.collect.Lists;
import com.zmall.service.IFileService;
import com.zmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceimpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceimpl.class);

    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //获取扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件，上传文件名：{}，新文件名：{}",fileName,path,uploadFileName);

        //创建路径
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //创建文件
        File targetFile = new File(path,uploadFileName);
        //把springmvc 封装的MultiFile 传入到targetfile
        try {
            file.transferTo(targetFile);
            //上传到ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传完毕后删除upload下的文件
            targetFile.delete();
        } catch (IOException e) {
           logger.error("上传文件异常",e);
           return null;
        }
        return targetFile.getName();
    }

}
