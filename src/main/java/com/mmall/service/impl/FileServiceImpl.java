package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by ionolab-DP on 2018/12/21.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    public Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    //返回上传文件名
    public String upload(MultipartFile file, String path){
        String fileName=file.getOriginalFilename();
        String fileExtentionName=fileName.substring(fileName.lastIndexOf(".")+1);

        String newFilename= UUID.randomUUID().toString()+"."+fileExtentionName;
        logger.info("Start uploading files,Filename:{},Path:{},NewFileName:{}",fileName,path,newFilename);

        File fileDir=new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();

        }

        File targetFile = new File(path,newFilename);

        try {
            file.transferTo(targetFile);
            //将targetFile上传到FTP服务器

            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return targetFile.getName();
    }
}
