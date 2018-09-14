package hb.weblog;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Blanco Hector
 * @version 1.0.0
 * @since 1.0.0
 */
public class ActionResultWithContent extends ActionResult {
	
	// Serial version
	private static final long serialVersionUID = -5883761436933210444L;
	
	private int total;
	private List content = new ArrayList<>();
	
	
	public ActionResultWithContent(int total, List content) {
		super(0, "Success");
		this.total = total;
		this.content = content;
	}
	
	
	public int getTotal() {
		return total;
	}
	
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	
	public List getContent() {
		return content;
	}
	
	
	public void setContent(List content) {
		this.content = content;
	}
}
