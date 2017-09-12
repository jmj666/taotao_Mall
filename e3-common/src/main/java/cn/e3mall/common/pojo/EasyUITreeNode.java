package cn.e3mall.common.pojo;

import java.io.Serializable;

public class EasyUITreeNode implements Serializable{
	private long id;//父节点id
	private String text;//节点文本信息
	private String state;//节点状态，有/无子节点，closed/open
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
