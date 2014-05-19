package com.example.nfcdemo.presentation;

import com.example.nfcdemo.IsoDepTransceiver;
import com.example.nfcdemo.R;
import com.google.gson.Gson;

import io.card.payment.CreditCard;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.tech.IsoDep;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TabHost.OnTabChangeListener;

public class NFCActivity extends ActionBarActivity implements com.example.nfcdemo.IsoDepTransceiver.OnMessageReceived, ReaderCallback, OnTabChangeListener{

	private static final String ScannerFragment_Tag = "Scanner";
	private static final String ReaderFragment_Tag = "Reader";
	private NfcAdapter _nfcAdapter;
	private FragmentTabHost _tabhost;
	private Vibrator _vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		_tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		_tabhost.addTab(_tabhost.newTabSpec(ScannerFragment_Tag).setIndicator(ScannerFragment_Tag),ScanFragment.class, null);
		_tabhost.addTab(_tabhost.newTabSpec(ReaderFragment_Tag).setIndicator(ReaderFragment_Tag),ReadFragment.class, null);
		_tabhost.setOnTabChangedListener(this);
		_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
		//Check for available NFC Adapter
		_nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (_nfcAdapter == null) {
			//TODO: Show a dialog to the user informing that the NFC Adapter is not available on the device
			Log.d("NFC DEMO", "NFC adapter not found");
			this.finish();
		}
	}

	/**
	 * Enabling reader mode disables host card emulation and p2p communications
	 */
	private void enableReaderMode() {
		_nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
	}

	/**
	 * Disabling the reader mode enables the host card emulation
	 */
	private void disableReaderMode() {
		_nfcAdapter.disableReaderMode(this);
	}

	/**
	 * Listener for devices identified to be in the vicinity(NFC)
	 */
	@Override
	public void onTagDiscovered(Tag tag) {
		IsoDep isoDep = IsoDep.get(tag);
		IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, this);
		Thread thread = new Thread(transceiver);
		thread.start();
	}

	/**
	 * Listener for message from the reader
	 */
	@Override
	public void onMessage(final byte[] message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				CreditCard creditCard = null;
				try {
					Gson json = new Gson();
					creditCard = json.fromJson(new String(message), CreditCard.class);
					if( creditCard != null ) {
						vibrate(); 
						onDataReceived(creditCard);
					}
				}catch( Exception ex ) {
					Log.d("NFC DEMO","error occured while deserializing Json String");
				}

			}
		});
	}
	
	/**
	 * Vibrate the device for 500 milliseconds
	 */
	private void vibrate() {
		if(_vibrator != null ) {
			_vibrator.vibrate(500);
		}
	}

	@Override
	public void onError(Exception exception) {
		onMessage(exception.getMessage().getBytes());
	}
	
	private void onDataReceived(CreditCard creditCard) {
		ReadFragment readFragment = (ReadFragment)getSupportFragmentManager().findFragmentByTag(ReaderFragment_Tag);
		if(readFragment != null ) {
			readFragment.update(creditCard);
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		if( tabId.equals(ScannerFragment_Tag) ) {
			disableReaderMode();
		} else if(tabId.equals(ReaderFragment_Tag)) {
			enableReaderMode();
		}
	}
}
