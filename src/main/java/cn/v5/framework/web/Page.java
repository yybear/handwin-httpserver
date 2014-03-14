package cn.v5.framework.web;

import java.io.Serializable;
import java.util.List;

/** 
 * @author qgan
 * @version 2013年12月19日 上午11:48:40
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = 1010189606977515707L;
	private List<T> list;				// list result of this page
	private long pageNumber;				// page number
	private long pageSize;				// result amount of this page
	private long totalPage;				// total page
	private long totalRow;				// total row
	
	public Page(List<T> list, long pageNumber, long pageSize, long totalPage, long totalRow) {
		this.list = list;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalPage = totalPage;
		this.totalRow = totalRow;
	}
	
	/**
	 * Return list of this page.
	 */
	public List<T> getList() {
		return list;
	}
	
	/**
	 * Return page number.
	 */
	public long getPageNumber() {
		return pageNumber;
	}
	
	/**
	 * Return page size.
	 */
	public long getPageSize() {
		return pageSize;
	}
	
	/**
	 * Return total page.
	 */
	public long getTotalPage() {
		return totalPage;
	}
	
	/**
	 * Return total row.
	 */
	public long getTotalRow() {
		return totalRow;
	}
}
