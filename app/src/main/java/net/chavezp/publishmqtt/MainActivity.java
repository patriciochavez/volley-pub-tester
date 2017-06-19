package net.chavezp.publishmqtt;

import android.content.Context;
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

    final String STRING_CONN = "tcp://10.105.231.153:1883";
    final String TAG = "casa";

    private String clientId;
    private Boolean iamconnected = false;
    private Button buttonConnect;
    private Button buttonLuzPorton;

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

        buttonLuzPorton = (Button) findViewById(R.id.button_luz_porton);
        buttonLuzPorton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                publish("casa/luz/porton", "cambiar");
                //getStatus(v);
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
        //getStatus("casa/estado", "todo");

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
                        refreshUI();
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

        //Ligth

        //Buzzer
    }


    public void connect(View v) {
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
                        // We are connected
                        Log.d(TAG, "onSuccess");
                        iamconnected = true;
                        /*buttonConnect.setText("Desconectar");
                        buttonConnect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Do something in response to button click
                                disconnect(v);
                                //Log.d(TAG,"Boton presionado");
                            }
                        });*/

                        /*buttonConnect.setText("Desconectar");
                        buttonConnect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Do something in response to button click
                                disconnect(v);
                                //Log.d(TAG,"Boton presionado");
                            }
                        });*/
                        //buttonLuzPorton.setEnabled(true);

                        String topic = "casa/#";
                        int qos = 0;
                        try {
                            final IMqttToken subToken = client.subscribe(topic, qos);
                            subToken.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    // The message was published

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

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");
                        //iamconnected = false;
                        //buttonConnect.setText("Conectar");
                        //buttonLuzPorton.setEnabled(false);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
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
                    refreshUI();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(View view) {

        String topic = "casa/luz/porton";
        String payload = "a";
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

    public void getStatus(String topic, String payload) {

        if (!iamconnected) {
            Context context = getApplicationContext();
            CharSequence text = "No conectado!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            return;
        } else {
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
    }
}
