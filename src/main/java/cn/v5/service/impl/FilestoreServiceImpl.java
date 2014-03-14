package cn.v5.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.skife.jdbi.v2.IDBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlOperations;
import org.springframework.transaction.annotation.Transactional;

import cn.v5.dao.FilestoreDao;
import cn.v5.model.DuduVideo;
import cn.v5.service.FilestoreService;

import javax.annotation.Resource;

/** 
 * @author qgan
 * @version 2014年2月25日 下午3:09:08
 */
@Transactional
public class FilestoreServiceImpl implements FilestoreService {
	private static final Logger log = LoggerFactory.getLogger(FilestoreServiceImpl.class);

    @Resource(name = "dbi")
    private IDBI dbi;
    
	final static String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

    private String generateKey(int num) {
    	FilestoreDao filestoreDao = dbi.open(FilestoreDao.class);
        String result =  RandomStringUtils.random(num, letters);
        DuduVideo video = filestoreDao.findVideoByShortExt(result);
        if(video == null){
           return result;
        }
        return generateKey(num+1);
    }

	@Override
	public DuduVideo findVideoByVideo(String video) {
		log.debug("video url is {}", video);
		
		FilestoreDao filestoreDao = dbi.open(FilestoreDao.class);
		/*DuduVideo v = new DuduVideo();
		filestoreDao.saveVideo(v);*/
		return filestoreDao.findVideoByVideo(video);
	}
}
