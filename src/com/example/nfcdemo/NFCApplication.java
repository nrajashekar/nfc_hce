package com.example.nfcdemo;

import io.card.payment.CreditCard;
import android.app.Application;

public class NFCApplication extends Application{
	
	private static CreditCard _cardDetails;

	public CreditCard getCardDetails() {
		return _cardDetails;
	}
	
	public void setCardDetails(CreditCard value ) {
		_cardDetails = value;
	}

}
