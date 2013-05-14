package org.lwes.listener;

import java.io.IOException;
import java.net.DatagramPacket;

import org.xerial.snappy.Snappy;

/**
 * @author dgoya
 *
 */
public class SnappyDatagramDequeuer extends DatagramDequeuer {

	@Override
	protected byte[] getData(DatagramPacket packet) throws IOException {
		return Snappy.uncompress(packet.getData());
	}
}
