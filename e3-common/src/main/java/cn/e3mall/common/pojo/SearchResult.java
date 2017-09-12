package cn.e3mall.common.pojo;

import java.io.Serializable;
import java.util.List;

//查询索引库结果

public class SearchResult implements Serializable{
	private List<SearchItem> itemList;//查询列表
	private int totalPages;//总页数
	private int recordCount;//总记录数
	public List<SearchItem> getitemList() {
		return itemList;
	}
	public void setitemList(List<SearchItem> itemList) {
		this.itemList = itemList;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	
	
}
