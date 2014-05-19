package com.example.nfcdemo.presentation;

import io.card.payment.CreditCard;

import com.example.nfcdemo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReadFragment extends Fragment{
	
	private TextView _cardNumber;
	private TextView _expiryDate;
	private String _noReceivedData;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reader_fragment, container, false);
        _cardNumber = (TextView) rootView.findViewById(R.id.card_number);
        _expiryDate = (TextView) rootView.findViewById(R.id.expirydate);  
        _noReceivedData = getString(R.string.no_data_received);
        update(null);
        return rootView;
    }

	public void update(CreditCard scanResult) {
		if( scanResult != null ) {
			_cardNumber.setText(scanResult.cardNumber);
			_expiryDate.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
		} else {
			_cardNumber.setText(_noReceivedData);
			_expiryDate.setText(_noReceivedData);
		}
		
	}

}
