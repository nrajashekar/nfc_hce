package com.example.nfcdemo;

import com.google.gson.Gson;

import io.card.payment.CreditCard;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class MyHostApduService extends HostApduService {

	private static final String TEXT_WAITING= "waiting....";
	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
		if (selectAidApdu(apdu)) {
			Log.i("HCEDEMO", "Application selected");
			return getWelcomeMessage();
		}
		else {
			Log.i("HCEDEMO", "Received: " + new String(apdu));
			return getNextMessage();
		}
	}

	private byte[] getWelcomeMessage() {
		return "Hello Desktop!".getBytes();
	}

	private byte[] getNextMessage() {
		return getNextMessageFromApp().getBytes();
	}

	private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}
	
	private String getNextMessageFromApp() {
		if(getApplication() instanceof NFCApplication ) {
			CreditCard creditCard = ((NFCApplication)getApplication()).getCardDetails();
			if( creditCard != null) {
				Gson json = new Gson();
	            return  json.toJson(creditCard);
			}
		}
		return TEXT_WAITING;
	}
}