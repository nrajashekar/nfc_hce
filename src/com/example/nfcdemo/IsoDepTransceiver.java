package com.example.nfcdemo;

import java.io.IOException;

import android.nfc.tech.IsoDep;

public class IsoDepTransceiver implements Runnable {


	public interface OnMessageReceived {

		void onMessage(byte[] message);

		void onError(Exception exception);
	}

	private IsoDep isoDep;
	private OnMessageReceived onMessageReceived;

	public IsoDepTransceiver(IsoDep isoDep, OnMessageReceived onMessageReceived) {
		this.isoDep = isoDep;
		this.onMessageReceived = onMessageReceived;
	}

	private static final byte[] CLA_INS_P1_P2 = { 0x00, (byte)0xA4, 0x04, 0x00 };
	private static final byte[] AID_ANDROID = { (byte)0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x09 };
	
	private byte[] createSelectAidApdu(byte[] aid) {
		byte[] result = new byte[6 + aid.length];
		System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
		result[4] = (byte)aid.length;
		System.arraycopy(aid, 0, result, 5, aid.length);
		result[result.length - 1] = 0;
		return result;
	}

	@Override
	public void run() {
		int messageCounter = 0;
		try {
			isoDep.connect();
			byte[] response = isoDep.transceive(createSelectAidApdu(AID_ANDROID));
			while (isoDep.isConnected() && !Thread.interrupted()) {
				//TODO: This sending of message is more of a formality and not really used, as of now.
				String message = android.os.Build.DEVICE + messageCounter++;
				response = isoDep.transceive(message.getBytes());
				onMessageReceived.onMessage(response);
			}
			isoDep.close();
		}
		catch (IOException e) {
			onMessageReceived.onError(e);
		}
	}
}
