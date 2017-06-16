package net.chavezp.publishmqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private String clientId;
    final String TAG = "PublishMQTTMainActivity";
    private Boolean iamconnected = false;
    private Button buttonConnect;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Log.d("PublishMQTTMainActivity","Boton presionado");
                connect(v);
            }
        });

        buttonSend = (Button) findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                sendMessage(v);
                Log.d("PublishMQTTMainActivity","Boton presionado");
            }
        });
        buttonSend.setEnabled(false);
    }

    public MqttAndroidClient client;

    public void connect(View v){
        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://IP:PORT", clientId);

        {
            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                        //iamconnected = true;
                        buttonConnect.setText("Desconectar");
                        buttonConnect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Do something in response to button click
                                disconnect(v);
                                Log.d("PublishMQTTMainActivity","Boton presionado");
                            }
                        });
                        buttonSend.setEnabled(true);

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");
                        //iamconnected = false;
                        buttonConnect.setText("Conectar");
                        buttonSend.setEnabled(false);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(View v){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    //iamconnected = false;
                    buttonConnect.setText("Conectar");
                    buttonConnect.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Do something in response to button click
                            connect(v);
                            Log.d("PublishMQTTMainActivity","Boton presionado");
                        }
                    });
                    buttonSend.setEnabled(false);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    buttonSend.setEnabled(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
         }

    }

    /** Called when the user touches the button */
    public void sendMessage(View view) {
        // Do something in response to button click
        //if (iamconnected){
          Log.d(TAG, "I am connected");
          Log.d(TAG, "Sending new message");

          String topic = "lgexo";
          String payload = TAG + ": new message";
          byte[] encodedPayload = new byte[0];
          { try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
               } catch (UnsupportedEncodingException | MqttException e) {
                   e.printStackTrace();
                  }
              }
          //}
    }

}
