package hb.weblog;

import com.google.gson.Gson;

import java.io.Serializable;


/**
 * @author Blanco Hector
 * @version 1.0.0
 * @since 1.0.0
 */
public class ActionResult implements Serializable {
	
	// Serial version
	private static final long serialVersionUID = 8088824904256149097L;
	
	// default success result
	public static final ActionResult SUCCESS = new ActionResult(0, "Success");
	
	
	private int code;
	private String message;
	
	
	public ActionResult() {
	}
	
	
	public ActionResult(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	
	public int getCode() {
		return code;
	}
	
	
	public void setCode(int code) {
		this.code = code;
	}
	
	
	public String getMessage() {
		return message;
	}
	
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public String toJson() {
		return new Gson().toJson(this);
	}
	
	
	@Override
	public String toString() {
		return "ActionResult{code=" + code + ", message='" + message + '\'' + '}';
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ActionResult)) return false;
		
		ActionResult that = (ActionResult) o;
		
		if (getCode() != that.getCode()) return false;
		return getMessage() != null ? getMessage().equals(that.getMessage()) : that.getMessage() == null;
	}
	
	
	@Override
	public int hashCode() {
		int result = getCode();
		result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
		return result;
	}
}
