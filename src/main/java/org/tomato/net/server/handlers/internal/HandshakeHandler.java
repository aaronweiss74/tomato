package org.tomato.net.server.handlers.internal;

import org.tomato.net.encryption.MapleObfuscator;
import org.tomato.net.server.internal.InternalPacketHandler;
import org.tomato.tools.ConsoleOutput;
import org.tomato.tools.HexTool;
import org.tomato.tools.data.input.SeekableLittleEndianAccessor;
import org.tomato.tools.net.InternalPacketCreator;
import org.tomato.client.internal.InternalClient;
import org.tomato.constants.ServerConstants;

public class HandshakeHandler extends InternalPacketHandler {
	@Override
	public void process(SeekableLittleEndianAccessor slea, InternalClient c) {
		short majorVersion = slea.readShort();
		short minorVersion = slea.readShort();
		if (majorVersion != ServerConstants.MAJOR_VERSION || minorVersion != ServerConstants.MINOR_VERSION) {
			ConsoleOutput.print("[Internal] Server versions do not match.");
			return;
		}
		slea.readByte(); // throw it away. it's useless.
		byte[] sendIv = new byte[4];
		for (int i = 0; i < sendIv.length; i++) {
			sendIv[i] = slea.readByte();
		}
		byte[] recvIv = new byte[4];
		for (int i = 0; i < recvIv.length; i++) {
			recvIv[i] = slea.readByte();
		}
		ConsoleOutput.print("Send IV: " + HexTool.toString(sendIv));
		ConsoleOutput.print("Recv IV: " + HexTool.toString(recvIv));
		byte key[] = {0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00, 0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00};
		MapleObfuscator send = new MapleObfuscator(key, sendIv, majorVersion);
		MapleObfuscator recv = new MapleObfuscator(key, recvIv, (short) (0xFFFF - majorVersion));
		c.setObfuscators(send, recv);
		ConsoleOutput.print("[Internal] Shook hands.");
		c.getChannel().write(InternalPacketCreator.getConnected());
	}
}
