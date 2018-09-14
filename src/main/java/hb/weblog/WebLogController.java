package hb.weblog;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Blanco Hector
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
@RequestMapping("/log")
public class WebLogController {
	
	// log tailer's session key
	private static final String TAIL_LOGGER_SESSION_KEY = "tailer";
	
	// logger
	private static final Logger LOGGER = Logger.getLogger(WebLogController.class);
	
	
	@ResponseBody
	@RequestMapping("/startWebLog")
	public String startWebLog(HttpServletRequest request) {
		try {
			// preapre the log listener
			prepareWebLogListener(request);
			
			for (int i = 0; i < 10; i++) {
				LOGGER.info("interaction " + i);
				TimeUnit.MILLISECONDS.sleep(500);
			}
			
			return ActionResult.SUCCESS.toJson();
		} catch (Exception e) {
			LOGGER.warn("Error: " + e.getLocalizedMessage(), e);
			return new ActionResult(99, "Generic error: " + e.getLocalizedMessage()).toJson();
		}
	}
	
	
	@ResponseBody
	@RequestMapping("/readWebLog")
	public String readWebLog(HttpServletRequest request) {
		WebLogListenerAdapter tailer = (WebLogListenerAdapter) request.getSession().getAttribute(TAIL_LOGGER_SESSION_KEY);
		if (tailer != null) {
			List<String> lines = tailer.readLines();
			return new ActionResultWithContent(lines.size(), lines).toJson();
		}
		return ActionResult.SUCCESS.toJson();
	}
	
	
	@ResponseBody
	@RequestMapping("/endWebLog")
	public String endWebLog(HttpServletRequest request) {
		WebLogListenerAdapter tailer = (WebLogListenerAdapter) request.getSession().getAttribute(TAIL_LOGGER_SESSION_KEY);
		if (tailer != null) {
			
			Logger.getRootLogger().removeAppender(tailer.getAppender());
			request.getSession().removeAttribute(TAIL_LOGGER_SESSION_KEY);
			
			// read last lines
			List<String> lines = tailer.readLines();
			tailer.stop();
			
			return new ActionResultWithContent(lines.size(), lines).toJson();
		}
		return ActionResult.SUCCESS.toJson();
	}
	
	
	/**
	 * Prepare the new log listener
	 *
	 * @param request the request in which session will this listiner be created
	 */
	private void prepareWebLogListener(HttpServletRequest request) {
		if (request.getSession(true).getAttribute(TAIL_LOGGER_SESSION_KEY) == null) {
			String fileName = "./log/weblog-" + request.getSession().getId() + ".log";
			
			RollingFileAppender appender = createWebLogFileAppender(fileName);
			Logger.getRootLogger().addAppender(appender);
			
			WebLogListenerAdapter tailer = new WebLogListenerAdapter(appender);
			request.getSession().setAttribute(TAIL_LOGGER_SESSION_KEY, tailer);
			tailer.start();
		}
	}
	
	
	/**
	 * Create a new Log file appender to the web log
	 *
	 * @param logFileName the name of the file to use
	 */
	private RollingFileAppender createWebLogFileAppender(String logFileName) {
		RollingFileAppender appender = new RollingFileAppender();
		appender.setThreshold(Level.ALL);
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{HH:mm:ss} %m%n");
		appender.setLayout(layout);
		appender.setAppend(true);
		appender.setFile(logFileName);
		appender.setBufferedIO(false);
		appender.setMaxBackupIndex(100);
		appender.setMaxFileSize("50MB");
		appender.setImmediateFlush(true);
		appender.activateOptions();
		return appender;
	}
	
}
