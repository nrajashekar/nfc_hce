nfc_hce
=======


NFC-Host Card Emulation Demo

Requirements:

  Two Android devices running 4.4 (Since host card emulation was introduced in 4.4)

Description:

  This is a simple application to demonstrate Host card emulation as well as the NFC reader mode.
The card.io SDK is used to scan a credit/debit card and get the details on one device. These details are sent across to the other 
device via NFC. So the device scanning the credit card demonstrates Host card emulation and the other device demonstrates how it can be used as a NFC reader.


TODO:

Better handling of the communication between the hostapduservice and the Activity.

