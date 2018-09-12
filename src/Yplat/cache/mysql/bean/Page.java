package Yplat.cache.mysql.bean;

import java.util.List;

public class Page<T> {

	private int pageNo = 1;//当前页
	
	private int pageSize = 10;//每页条数
	
	private int totalSize;//总条数
	
	private int totalPage;//总页数
	
	private int startSize;//开始页数
	
	private int endSize;//结束页数
	
	private List<T> resultList;//结果树

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getTotalPage() {
		if (this.totalSize % this.pageSize == 0) {
			totalPage = this.totalSize/this.pageSize;
		}else {
			totalPage = this.totalSize/this.pageSize + 1;
		}
		return totalPage;
	}

	public List<T> getResultList() {
		return resultList;
	}

	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}

	public int getStartSize() {
		if (startSize == 0) {
			startSize = (this.pageNo-1) * this.pageSize;
		}
		return startSize;
	}

	public void setStartSize(int startSize) {
		this.startSize = startSize;
	}

	public int getEndSize() {
		if (endSize == 0) {
			endSize = this.pageNo * this.pageSize;
		}
		return endSize;
	}

	public void setEndSize(int endSize) {
		this.endSize = endSize;
	}
	
	
}
