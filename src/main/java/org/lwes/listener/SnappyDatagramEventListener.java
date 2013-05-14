package org.lwes.listener;

/**
 * @author dgoya
 *
 */
public class SnappyDatagramEventListener extends DatagramEventListener {

    public SnappyDatagramEventListener(){
    	 super(new DatagramEnqueuer(), new SnappyDatagramDequeuer());
    }
}
