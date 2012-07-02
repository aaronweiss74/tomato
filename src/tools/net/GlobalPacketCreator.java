package tools.net;

import net.server.core.MaplePacket;
import tools.data.output.MaplePacketLittleEndianWriter;
import constants.ServerConstants;

public class GlobalPacketCreator {
	/**
	 * Gets the basic introduction packet to send to new clients.
	 * @param majorVersion the major version number
	 * @param minorVersion the minor version number
	 * @param sendIv the initialization vector for sent packets
	 * @param recvIv the initialization vector for received packets
	 * @return the basic introduction packet to send to new clients
	 */
	public static MaplePacket getHandshake(short majorVersion, short minorVersion, byte[] sendIv, byte[] recvIv) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
		mplew.writeShort(0x0E);
		mplew.writeShort(majorVersion);
		mplew.writeShort(minorVersion);
		mplew.write(0x31); // Unknown
		mplew.write(recvIv);
		mplew.write(sendIv);
		mplew.write(ServerConstants.REGION_CODE);
		return mplew.getPacket();
	}
}
