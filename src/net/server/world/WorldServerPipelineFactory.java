package net.server.world;

import net.server.encryption.MaplePacketDecoder;
import net.server.encryption.MaplePacketEncoder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * A <code>ChannelPipelineFactory</code> for world server pipelines.
 * @author tomato
 * @version 1.0
 * @since alpha2
 */
public class WorldServerPipelineFactory implements ChannelPipelineFactory {
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline cp = Channels.pipeline(); // TODO: new WorldServerHandler();
		cp.addLast("customDecoder", new MaplePacketDecoder());
		cp.addLast("customEncoder", new MaplePacketEncoder());
		return cp;
	}
}
