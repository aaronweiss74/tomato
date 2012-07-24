package org.tomato.net.server.opcodes.internal;

/**
 * An enumeration of internally sent opcodes.
 * @author tomato
 * @author XiaoKia
 * @version 1.0
 * @since alpha
 */
public enum InternalSendOpcode {
	AliveReq(0x11);
	private int opcode = 0;

	/**
	 * Creates a new opcode.
	 * @param opcode the value of the opcode
	 */
	private InternalSendOpcode(int opcode) {
		this.opcode = opcode;
	}
	
	/**
	 * Gets the value of the opcode.
	 * @return the value of the opcode
	 */
	public int getOpcode() {
		return opcode;
	}
}
