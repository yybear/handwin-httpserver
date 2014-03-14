package cn.v5.web.api;


import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.v5.model.DuduVideo;
import cn.v5.service.FilestoreService;
import cn.v5.util.ApiResultCode;
import cn.v5.util.FileHelper;
import cn.v5.util.RedisUtil;
import cn.v5.util.SystemConstants;

import com.google.common.collect.Maps;

/** 
 * @author qgan
 * @version 2014年2月25日 下午2:02:19
 */
@Controller
@RequestMapping("/api/fs")
public class FileApi {
	private static final Logger log = LoggerFactory.getLogger(UserApi.class);
	
	@Autowired
	private FilestoreService filestoreService;
    
    @RequestMapping(value="/size/{name:.*}", method=RequestMethod.GET)
    public Map<String, Object> getFileLength(@PathVariable("name") String name, HttpServletResponse response) {
    	log.debug("name is {}", RedisUtil.get(SystemConstants.USER_SESSION+"8e8290a438f4115b7f4963d2646983a9"));
    	Map<String, Object> res = Maps.newHashMap();
    	log.debug("getFileLength path is {}", FileHelper.getStorgePath(name));
        File file = new File(FileHelper.getStorgePath(name));
        long length = file.length();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Size", String.valueOf(length));
        DuduVideo video = filestoreService.findVideoByVideo(FileHelper.getRelativeStorgePath(name));
        if(video != null){
        	log.debug("getFileLength video has been in database");
            res.put("result_code", ApiResultCode.SUCCESS);
            res.put("video_url", video.getVideoUrl());
            res.put("short_url", video.getShortUrl());
            
            return res;
        }
        
        File fileTmp = new File(FileHelper.getStorgePath(name) + FileHelper.TMP_SUFFIX);
        if(log.isDebugEnabled())
        	log.debug("getFileLength temp path is " + fileTmp.getAbsolutePath() + ", existed is: " + fileTmp.exists());
        if(fileTmp.exists()) {
        	// 临时文件存在，则保存临时文件
        	log.debug("getFileLength save tmp file");
        	try {
				FileHelper.saveFileFromTmp(fileTmp, file);
			} catch (IOException ingore) {
				log.error(ingore.getMessage(), ingore);
			}
        	length = file.length();
        }
        response.setHeader("Content-Size", String.valueOf(length));
        
        return res;
    }
    
    @RequestMapping(value="/get/{name:.*}", method=RequestMethod.GET)
    public DuduVideo get(@PathVariable("name") String name, HttpServletResponse response) {
    	Map<String, Object> res = Maps.newHashMap();
    	log.debug("getFileLength path is {}", FileHelper.getStorgePath(name));
        File file = new File(FileHelper.getStorgePath(name));
        long length = file.length();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Size", String.valueOf(length));
        DuduVideo video = filestoreService.findVideoByVideo(FileHelper.getRelativeStorgePath(name));
        if(video != null){
        	log.debug("getFileLength video has been in database");
            return video;
        }
        
        return null;
    }
}
