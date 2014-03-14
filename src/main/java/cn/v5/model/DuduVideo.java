package cn.v5.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.v5.util.ConfigUtils;

/** 
 * @author qgan
 * @version 2014年2月25日 下午2:05:33
 */
public class DuduVideo {
	private Long id;     //视频ID

	private Long userId;    //拍摄人ID

	private String video;   //视频地址

	private String shortExt;    //视频短地址后缀

	private String videoBody;    //视频内容描述

	private Date createDate;  //创建时间

	private Integer status;
	
	@JsonIgnore
	private String shortUrl;    //视频短地址
	
	@JsonIgnore
	private String videoUrl;   //视频地址

	@JsonIgnore
	private String picUrl;    //视频封面地址
	
	public static class DuduVideoMapper implements ResultSetMapper<DuduVideo> {
		public DuduVideo map(int index, ResultSet r, StatementContext ctx)
				throws SQLException {
			DuduVideo duduVideo = new DuduVideo();
			duduVideo.setId(r.getLong("id"));
			duduVideo.setUserId(r.getLong("user_id"));
			duduVideo.setShortExt(r.getString("short_ext"));
			duduVideo.setVideo(r.getString("video_url"));
			duduVideo.setVideoBody(r.getString("video_body"));
			duduVideo.setCreateDate(r.getDate("create_date"));
			duduVideo.setStatus(r.getInt("status"));
			return duduVideo;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getVideoUrl() {
        if (video != null) {
            this.videoUrl = ConfigUtils.getString("short.url.addr") + "api/fs/video/"+ video.substring(this.video.lastIndexOf("/")+1);
        }
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getShortExt() {
		return shortExt;
	}

	public void setShortExt(String shortExt) {
		this.shortExt = shortExt;
	}

	public String getShortUrl() {
		shortUrl = ConfigUtils.getString("short.url.addr") + this.shortExt;
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getVideoBody() {
		return videoBody;
	}

	public void setVideoBody(String videoBody) {
		this.videoBody = videoBody;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	} 
    
    
}
