package Yplat.model;

/**
 * 请求数据头部的model 
 */
public class RequestHeadModel{
		
		private String bussCode;//业务代号
		
		private String mark;//防重标记
		
		public String getBussCode() {
			return bussCode;
		}

		public void setBussCode(String bussCode) {
			this.bussCode = bussCode;
		}

		public String getMark() {
			return mark;
		}

		public void setMark(String mark) {
			this.mark = mark;
		}
		
}
