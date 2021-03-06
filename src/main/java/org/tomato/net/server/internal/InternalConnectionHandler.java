package org.tomato.net.server.internal;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomato.net.server.core.AbstractMapleServerHandler;
import org.tomato.net.server.core.MaplePacket;
import org.tomato.net.server.core.MaplePacketProcessor;
import org.tomato.net.server.encryption.MaplePacketDecoder;
import org.tomato.net.server.handlers.internal.HandshakeHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.tomato.tools.HexTool;
import org.tomato.client.internal.InternalClient;
import org.tomato.constants.SourceConstants;

/**
 * A handler for internal connections.
 * @author tomato
 * @version 1.0
 * @since alpha2
 */
public class InternalConnectionHandler extends AbstractMapleServerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalConnectionHandler.class);

	private ChannelGroup connections = new DefaultChannelGroup("internal-connections");
	private InternalPacketProcessor processor;
	
	/**
	 * Creates a internal connection handler.
	 */
	public InternalConnectionHandler() {
		this.setPacketProcessor(InternalPacketProcessor.getInstance());
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        LOGGER.debug("Channel opened with {}", e.getChannel().getRemoteAddress());
		connections.add(e.getChannel());
		InternalClient c = new InternalClient(e.getChannel());
		e.getChannel().setAttachment(c);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		MaplePacketDecoder mpd = new MaplePacketDecoder();
		try {
			MaplePacket mp = (MaplePacket) mpd.decode(ctx, e.getChannel(), (ChannelBuffer) e.getMessage());
			if (mp != null) {
				byte[] data = mp.getBytes();
				if (SourceConstants.VERBOSE_PACKETS) LOGGER.debug("Received {}", HexTool.toString(data));
				ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(ByteOrder.LITTLE_ENDIAN, data);
				InternalClient client = (InternalClient) e.getChannel().getAttachment();
				InternalPacketHandler iph = processor.getHandler(buffer.readShort());
				if (iph != null && iph.validate(client)) {
					try {
						iph.process(buffer, client);
					} catch (Throwable t) {
						// TODO: log all packet processing exceptions.
					}
				}
			}
		} catch (NullPointerException ex) {
			ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
			InternalClient client = (InternalClient) e.getChannel().getAttachment();
			short length = buffer.readShort();
			if (buffer.readableBytes() == length) {
				new HandshakeHandler().process(buffer, client);
			}
		}
		
	}
	
	@Override
	public void setPacketProcessor(MaplePacketProcessor processor) {
		this.processor = (InternalPacketProcessor) processor;
	}
	
	@Override
	public ChannelGroup getConnections() {
		return connections;
	}
}
