package controller.communication.events;

import java.util.concurrent.ConcurrentLinkedQueue;

import controller.communication.events.RemoteEvent;

/**
 * Created by whiteshad on 12/11/15.
 */
public class RemoteEventHandler {
    private ConcurrentLinkedQueue<RemoteEvent> eventsQueue;

    public RemoteEventHandler() {
        this.eventsQueue = new ConcurrentLinkedQueue<RemoteEvent>();
    }

    public void addRemoteEvent(RemoteEvent event){
        this.eventsQueue.add(event);
    }

    public RemoteEvent getRemoteEvent(){
        return this.eventsQueue.poll();
    }
}
