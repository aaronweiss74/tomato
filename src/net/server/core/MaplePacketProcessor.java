package net.server.core;

import net.server.opcodes.RecvOpcode;

/**
 * A processor to get handlers based on opcode.
 * @author tomato
 * @version 1.0
 * @since alpha
 */
public class MaplePacketProcessor {
	private static MaplePacketProcessor instance;
	private MaplePacketHandler[] handlers;
	
	/**
	 * Creates a packet processor wrapping an array of handlers.
	 */
	private MaplePacketProcessor() {
		int maxOpcode = 0;
		for (RecvOpcode opcode : RecvOpcode.values()) {
			if (opcode.getOpcode() > maxOpcode) {
				maxOpcode = opcode.getOpcode();
			}
		}
		handlers = new MaplePacketHandler[maxOpcode + 1];
	}
	
	/**
	 * Gets the <code>MaplePacketHandler</code> for the supplied opcode.
	 * @param opcode the opcode for the desired handler
	 * @return the desired <code>MaplePacketHandler</code>
	 */
	public MaplePacketHandler getHandler(int opcode) {
		try {
			return handlers[opcode];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Registers a handler with a specified opcode.
	 * @param opcode the opcode to be registered
	 * @param handler the <code>MaplePacketHandler</code> to be registered
	 */
	public void register(int opcode, MaplePacketHandler handler) {
		handlers[opcode] = handler;
	}
	
	/**
	 * Resets the packet processor.
	 */
	public void reset() {
		int maxOpcode = 0;
		for (RecvOpcode opcode : RecvOpcode.values()) {
			if (opcode.getOpcode() > maxOpcode) {
				maxOpcode = opcode.getOpcode();
			}
		}
		handlers = new MaplePacketHandler[maxOpcode + 1];
	}
	
	/**
	 * Registers all the handlers with the packet processor.
	 */
	public void configure() {
		// TODO: register some handlers, yo.
	}
	
	/**
	 * Gets the universal instance of the packet processor.
	 * @return the universal <code>MaplePacketProcessor</code>
	 */
	public static MaplePacketProcessor getInstance() {
		if (instance == null) {
			instance = new MaplePacketProcessor();
			instance.configure();
		}
		return instance;
	}
}
