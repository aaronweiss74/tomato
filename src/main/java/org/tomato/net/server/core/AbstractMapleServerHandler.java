package org.tomato.net.server.core;

import org.tomato.net.server.encryption.MaplePacketDecoder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.tomato.tools.ConsoleOutput;
import org.tomato.tools.HexTool;
import org.tomato.tools.data.input.ByteArrayByteStream;
import org.tomato.tools.data.input.GenericSeekableLittleEndianAccessor;
import org.tomato.tools.data.input.SeekableLittleEndianAccessor;
import org.tomato.client.core.Client;
import org.tomato.client.core.KeepAliveClient;
import org.tomato.constants.SourceConstants;

public abstract class AbstractMapleServerHandler extends SimpleChannelHandler implements MapleServerHandler {
	private ChannelGroup connections = new DefaultChannelGroup();
	private AbstractMaplePacketProcessor processor;
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		synchronized (e.getChannel()) {
			Client client = (Client) e.getChannel().getAttachment();
			if (client != null) {
				try {
					client.disconnect();
				} finally {
					e.getChannel().setAttachment(null);
					// TODO: empty org.tomato.client.
				}
			}
		}
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		MaplePacketDecoder mpd = new MaplePacketDecoder();
		MaplePacket mp = (MaplePacket) mpd.decode(ctx, e.getChannel(), (ChannelBuffer) e.getMessage());
		if (mp != null) {
			byte[] data = mp.getBytes();
			if (SourceConstants.VERBOSE_PACKETS) ConsoleOutput.print("[Recv] " + HexTool.toString(data));
			SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(data));
			KeepAliveClient client = (KeepAliveClient) e.getChannel().getAttachment();
			MaplePacketHandler mph = processor.getHandler(slea.readShort());
			if (mph != null && mph.validate(client)) {
				try {
					mph.process(slea, client);
				} catch (Throwable t) {
					t.printStackTrace();
					// TODO: log all packet processing exceptions.
				}
			}
		} else if (SourceConstants.VERBOSE_PACKETS) {
			ConsoleOutput.print("Packet dropped.");
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.getChannel().close();
		e.getCause().printStackTrace();
		// TODO: log networking exceptions.
	}

	@Override
	public void setPacketProcessor(MaplePacketProcessor processor) {
		this.processor = (AbstractMaplePacketProcessor) processor;
	}
	
	@Override
	public ChannelGroup getConnections() {
		return connections;
	}
}
