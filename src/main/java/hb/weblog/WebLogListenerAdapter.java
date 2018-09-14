package hb.weblog;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.log4j.FileAppender;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Blanco Hector
 * @version 1.0.0
 * @since 1.0.0
 */
public class WebLogListenerAdapter extends TailerListenerAdapter {
	
	private final LinkedList<String> queue = new LinkedList<>();
	private Tailer tailer;
	private ExecutorService executor;
	private FileAppender appender;
	
	
	public WebLogListenerAdapter(RollingFileAppender appender) {
		this.appender = appender;
	}
	
	
	public void handle(String line) {
		synchronized (queue) {
			queue.add(line);
		}
	}
	
	
	public List<String> readLines() {
		LinkedList<String> toReturn;
		synchronized (queue) {
			toReturn = new LinkedList<>(queue);
		}
		clear();
		return toReturn;
	}
	
	
	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}
	
	
	public void start() {
		executor = Executors.newSingleThreadExecutor();
		tailer = new Tailer(new File(appender.getFile()), this);
		executor.execute(tailer);
	}
	
	
	public void stop() {
		tailer.stop();
		queue.clear();
		executor.shutdownNow();
		
		// close appender
		String appenderFile = appender.getFile();
		appender.close();
		new File(appenderFile).delete();
	}
	
	
	public FileAppender getAppender() {
		return appender;
	}
}
