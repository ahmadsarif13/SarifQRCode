package com.easyit.sarifqrcode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewNama, textViewKelas, textViewNim;
    //Menentukan Objek QR
    private IntentIntegrator qrScan;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View objects
        //View Objects
        ImageButton imageButtonWeb = (ImageButton) findViewById(R.id.imageButtonWeb);
        ImageButton imageButtonTelp = (ImageButton) findViewById(R.id.imageButtonTelp);
        //imageButtonEmail = (ImageButton) findViewById(R.id.imageButtonEmail);
        //imageButtonMaps = (ImageButton) findViewById(R.id.imageButttonMaps);
        Button buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewKelas = (TextView) findViewById(R.id.textViewKelas);
        textViewNim = (TextView) findViewById(R.id.textViewNim);//intialisasi scan object
        qrScan = new IntentIntegrator(this);

        //Penerapan onclick listener
        buttonScan.setOnClickListener(this);
        imageButtonWeb.setOnClickListener(this);
        imageButtonTelp.setOnClickListener(this);
        //imageButtonEmail.setOnClickListener(this);
        //imageButtonMaps.setOnClickListener(this);
    }
    //Logika untuk mendapatkan hasil scanning

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        if (result != null) {
        //jika QRCode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil SCAN tidak ada", Toast.LENGTH_LONG).show();

            } else {
        //jika QRCode ada atau ditemukan data
                //1.Logika jika data yang masuk url http://...
                String url = result.getContents();
                String address;
                String http = "http://";
                String https = "https://";
                address = result.getContents();
                if (address.contains(http) || address.contains(https)) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                // 2.Logika Jika ada email pada barcode yang sudah ter-scan

                String alamat = result.getContents();
                String at = "@gmail";

                if (alamat.contains(at)) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String[] recipients = {alamat.replace("http://", "")};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Email");
                    intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
                    intent.putExtra(Intent.EXTRA_CC, "");
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                }

                //3. Logika jika Nomor Telepon ada untuk persiapan Telepon
                String number;
                number = result.getContents();

                if (number.matches("^[0-9,+]*$") && number.length() > 10) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    Intent dialIntent = new Intent(Intent.ACTION_CALL);
                    dialIntent.setData(Uri.parse("tel:" + number));
                    dialIntent.setPackage("https://api.whatsapp.com/send?phone=%s&text=%s" + number);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                    startActivity(dialIntent);
                }

                //4.Logika Membuka Koordinat Maps

                String uriMaps = result.getContents();
                String maps = "http://maps.google.com/maps?q=loc:" + uriMaps;
                String testDoubleData1 = ",";
                String testDoubleData2 = ".";

                boolean b = uriMaps.contains(testDoubleData1) && uriMaps.contains(testDoubleData2);
                if (b) {
                    Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(maps));
                    mapsIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapsIntent);
                }



                try {
                //Perintah Konversi Data Scan ke JSON
                    JSONObject obj = new JSONObject(result.getContents());
                //Ambil nilai data JSON  ke TextViews
                    textViewNama.setText(obj.getString("nama"));
                    textViewKelas.setText(obj.getString("kelas"));
                    textViewNim.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
                //jika kontrolling ada di sini
                //itu berarti format encoded tidak cocok
                //dalam hal ini kita dapat menampilkan data apapun yg tesedia pada qrcode
                //untuk di toas atau ditampilkan
                    Toast.makeText(this, result.getContents(),
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick (View view){
    //Perintah Scanning QRCode
        qrScan.initiateScan();
    }
}
