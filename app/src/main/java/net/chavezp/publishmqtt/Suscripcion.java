package net.chavezp.publishmqtt;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static net.chavezp.publishmqtt.MainActivity.textviewEstado;
import static net.chavezp.publishmqtt.MainActivity.buttonLuzPorton;


/**
 * Created by pato on 18/06/2017.
 */

public class Suscripcion extends Activity implements MqttCallback{

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String response = new String((message.getPayload()));

        if (topic.contains("casa/luz/porton")){
            if (response.contains("encendido")){
                buttonLuzPorton.setBackgroundColor(Color.GREEN);
                } else {
                    buttonLuzPorton.setBackgroundColor(Color.GRAY);
                }
        }
        textviewEstado.setText(new String(message.getPayload()));

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}