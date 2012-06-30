package tools.data.input;

import java.io.IOException;
import tools.HexTool;

/**
 * A <code>SeekableInputStreamBytestream</code> wrapping a byte array.
 * @author tomato
 * @version 1.0
 * @since alpha
 */
public class ByteArrayByteStream implements SeekableInputStreamBytestream {
	private int pos = 0;
	private long bytesRead = 0;
	private byte[] arr;

	/**
	 * Creates a byte stream wrapping a byte array.
	 * @param arr the array to wrap
	 */
	public ByteArrayByteStream(byte[] arr) {
		this.arr = arr;
	}

	@Override
	public long getPosition() {
		return pos;
	}

	@Override
	public void seek(long offset) throws IOException {
		pos = (int) offset;
	}

	@Override
	public long getBytesRead() {
		return bytesRead;
	}

	@Override
	public int readByte() {
		bytesRead++;
		return ((int) arr[pos++]) & 0xFF;
	}

	@Override
	public String toString() {
		String nows = "";
		if (arr.length - pos > 0) {
			byte[] now = new byte[arr.length - pos];
			System.arraycopy(arr, pos, now, 0, arr.length - pos);
			nows = HexTool.toString(now);
		}
		return "All: " + HexTool.toString(arr) + "\nNow: " + nows;
	}

	@Override
	public long available() {
		return arr.length - pos;
	}
}
