package org.axe.captain.constant;

public enum CaptainExceptionEnum {

	HOST_EXISTED("HOST_EXISTED","host已存在");
	
	public String code;
	public String desc;
	private CaptainExceptionEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	
	public static CaptainExceptionEnum getException(String code){
		CaptainExceptionEnum[] values = CaptainExceptionEnum.values();
		for(CaptainExceptionEnum exception:values){
			if(exception.code.equals(code)){
				return exception;
			}
		}
		return null;
	}
	
}
