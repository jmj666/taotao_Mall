package cn.e3mall.common.pojo;
//展示数据结果
import java.io.Serializable;
import java.util.List;

public class EasyUIDataGridResult implements Serializable{
	private Long total;
	private List<?> rows;
	public EasyUIDataGridResult(Long total, List<?> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public EasyUIDataGridResult() {
		// TODO Auto-generated constructor stub
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<?> getRows() {
		return rows;
	}
	public void setRows(List<?> rows) {
		this.rows = rows;
	}
	
}
