/**
 * File Name: MessageDTO.java
 * Date: 2019-08-16 17:28:26
 */
package me.belucky.fishpond.dto;

/**
 * Description: 
 * @author shenzulun
 * @date 2019-08-16
 * @version 1.0
 */
public class MessageDTO implements java.io.Serializable{
	private static final long serialVersionUID = -7317533085696249223L;
	
	private String retMsg;

	private int retCode;
	
	public MessageDTO(){}
	
	public MessageDTO(String retMsg, int retCode) {
		super();
		this.retMsg = retMsg;
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	
	
}
