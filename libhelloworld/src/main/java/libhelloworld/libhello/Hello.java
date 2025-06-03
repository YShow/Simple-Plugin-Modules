package libhelloworld.libhello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hello {
	
	private static final Logger log = LoggerFactory.getLogger(Hello.class);

	public void sayHello() {
		
	    log.atInfo().log("hello from plugin libhello");
	}

}
