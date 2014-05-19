package com.example.nfcdemo.presentation;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import com.example.nfcdemo.NFCApplication;
import com.example.nfcdemo.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ScanFragment extends Fragment implements View.OnClickListener{
	
	private static final String MY_CARDIO_APP_TOKEN = "12b664ae91d04ed5a33f302cd6df17c9";
	private static final int MY_SCAN_REQUEST_CODE = 121;
	
	private Button _scan;
	private Button _reset;
	private TextView _cardNumber;
	private TextView _expiryDate;
	private String _noScannedData;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.scanner_fragment, container, false);
        _scan = (Button) rootView.findViewById(R.id.scan);
        _cardNumber = (TextView) rootView.findViewById(R.id.card_number);
        _expiryDate = (TextView) rootView.findViewById(R.id.expirydate);
        _reset = (Button) rootView.findViewById(R.id.reset);
        _scan.setOnClickListener(this);
        _reset.setOnClickListener(this);
        _noScannedData = getString(R.string.no_scanned_data);
        update(null);
        return rootView;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan:
			scanCreditCard();
			break;
		case R.id.reset:
			resetCardDetails(); 
			break;
		default:
			break;
		}
		
	}
	
	private void resetCardDetails() {
		((NFCApplication)getActivity().getApplication()).setCardDetails(null);
		update(null);
	}

	private void update(CreditCard scanResult) {
		if( scanResult != null ) {
			_cardNumber.setText(scanResult.cardNumber);
			_expiryDate.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
		} else {
			_cardNumber.setText(_noScannedData);
			_expiryDate.setText(_noScannedData);
		}
		
	}
	
	public void scanCreditCard() {
		Intent scanIntent = new Intent(this.getActivity(), CardIOActivity.class);

		// required for authentication with card.io
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, MY_CARDIO_APP_TOKEN);

		// customize these values to suit your needs.
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MY_SCAN_REQUEST_CODE) {
			if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
				CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
				((NFCApplication)getActivity().getApplication()).setCardDetails(scanResult);
				update(scanResult);
			}
		}
	}
}
