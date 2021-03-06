package org.tomato.client.core;

import org.tomato.net.encryption.MapleObfuscator;

/**
 * An interface describing a crypographically-inclined org.tomato.client.
 * @author tomato
 * @version 1.0
 * @since alpha2
 */
public interface CryptoClient extends Client {
	/**
	 * Gets the packet obfuscator for sending packets.
	 * @return the <code>MapleObfuscator</code> handling packets being sent
	 */
	public MapleObfuscator getSendCrypto();

	/**
	 * Gets the packet obfuscator for receiving packets.
	 * @return the <code>MapleObfuscator</code> handling packets being received
	 */
	public MapleObfuscator getRecvCrypto();
}
