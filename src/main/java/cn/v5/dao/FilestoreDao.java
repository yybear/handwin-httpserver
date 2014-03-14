package cn.v5.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import cn.v5.model.DuduVideo;
import cn.v5.model.DuduVideo.DuduVideoMapper;

/** 
 * @author qgan
 * @version 2014年2月25日 下午2:14:32
 */
public interface FilestoreDao {
	@SqlQuery("select * from t_dudu_video where short_ext=:shortExt")
	@Mapper(DuduVideoMapper.class)
	DuduVideo findVideoByShortExt(@Bind("shortExt") String shortExt);
	
	@SqlQuery("select * from t_dudu_video where video_url=:video")
	@Mapper(DuduVideoMapper.class)
	DuduVideo findVideoByVideo(@Bind("video") String video);
	
	@SqlUpdate("insert into t_dudu_video (video_url) values (:videoUrl)")
	long saveVideo(@BindBean DuduVideo video);
}
