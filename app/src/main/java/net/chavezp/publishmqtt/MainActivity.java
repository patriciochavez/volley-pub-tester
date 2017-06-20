package net.chavezp.publishmqtt;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    final String STRING_CONN = "tcp://200.5.235.52:1883";
    final String TAG = "casa";

    private String clientId;
    private Boolean iamconnected = false;
    private Button buttonConnect;
    public static Button buttonTemperatura;
    public static Button buttonLuzPorton;
    public static TextView textviewEstado;
    public MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                disconnect();
            }
        });

        textviewEstado = (TextView) findViewById(R.id.textview_estado);

        buttonTemperatura = (Button) findViewById(R.id.button_temperatura);
        buttonTemperatura.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                publish("casa/temperatura/living", "0");
            }
        });

        buttonLuzPorton = (Button) findViewById(R.id.button_luz_porton);
        buttonLuzPorton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                publish("casa/luz/porton", "1");
            }
        });

        connect();
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
        //Disconnect
        disconnect();
    }

    public void connect() {
        if (iamconnected) return;

        clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), STRING_CONN, clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        options.setUserName("mi_usuario");
        options.setPassword("mi_clave".toCharArray());

        {
            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "onSuccess");
                        iamconnected = true;
                        suscribe("casa/#", 0);
                        refreshUI();
                        publish("casa/luz/porton", "0");
                        publish("casa/temperatura/living", "0");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "onFailure");
                        iamconnected = false;
                        refreshUI();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshUI() {
        if (iamconnected) {
            //Connect button
            buttonConnect.setText("Desconectar");
            buttonConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    disconnect();
                }
            });

            //Light button
            buttonLuzPorton.setEnabled(true);
        }
        //Not connected
        else {
            //Connect Button
            buttonConnect.setText("Conectar");
            buttonConnect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    connect();
                }
            });

            //Light button
            buttonLuzPorton.setEnabled(false);

        }
        //Temperature

        //Light

        //Buzzer
    }

    public void disconnect() {
        if (!iamconnected) return;
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    iamconnected = false;
                    refreshUI();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    //refreshUI();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

 public void publish(String topic, String payload) {
        byte[] encodedPayload = new byte[0];
        {
            try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void suscribe(String topic, int qos){
        try {
            final IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    client.setCallback(new Suscripcion());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
