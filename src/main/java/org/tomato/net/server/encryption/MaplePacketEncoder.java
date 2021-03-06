package org.tomato.net.server.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomato.net.server.core.MaplePacket;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.tomato.tools.HexTool;
import org.tomato.client.core.CryptoClient;
import org.tomato.constants.SourceConstants;

/**
 * A one-to-one encoder for converting a <code>MaplePacket</code> to an encrypted <code>ChannelBuffer</code>.
 * @author tomato
 * @version 1.0
 * @since alpha
 */
public class MaplePacketEncoder extends OneToOneEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaplePacketEncoder.class);
    
	@Override
	public Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		MaplePacket packet = (MaplePacket) msg;
		CryptoClient client = (CryptoClient) channel.getAttachment();
		if (SourceConstants.VERBOSE_PACKETS) LOGGER.debug("Sending {}", HexTool.toString(packet.getBytes()));
		if (client != null && client.getSendCrypto() != null) {
			final byte[] input = packet.getBytes();
			final byte[] unencrypted = new byte[input.length];
			System.arraycopy(input, 0, unencrypted, 0, input.length);
			final byte[] ret = new byte[unencrypted.length + 4];
			final byte[] header = client.getSendCrypto().getPacketHeader(unencrypted.length);
			MapleEncryption.encryptData(unencrypted);
			synchronized (client.getSendCrypto()) {
				client.getSendCrypto().crypt(unencrypted);
				System.arraycopy(header, 0, ret, 0, 4);
				System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
				return ChannelBuffers.wrappedBuffer(ChannelBuffers.LITTLE_ENDIAN, ret);
			}
		} else {
			return ChannelBuffers.wrappedBuffer(ChannelBuffers.LITTLE_ENDIAN, packet.getBytes());
		}
	}
}
