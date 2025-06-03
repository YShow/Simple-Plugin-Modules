package testeeee;

import java.util.Base64;
import java.util.random.RandomGenerator;

import com.yshow.mychat.events.Event;
import com.yshow.mychat.events.EventHandler;
import com.yshow.mychat.events.impl.HiEvent;

public class GenericEvent implements EventHandler {

    private final RandomGenerator rand;

    public GenericEvent() {

	this.rand = RandomGenerator.getDefault();
    }

    @Override
    public Event onEvent(Event event) {

	if (event instanceof HiEvent e) {

	    var asd = e.hi().contains("oi");

	    if (asd) {
		return e.cancelEvent();
	    }

	    return event;
	}

	var a = new byte[30];
	    rand.nextBytes(a);

	    var aa = Base64.getEncoder().encodeToString(a);

	    aa.contains("oi");


	return event;
    }

}
