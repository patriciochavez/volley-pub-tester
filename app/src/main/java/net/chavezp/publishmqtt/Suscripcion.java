package net.chavezp.publishmqtt;

import android.app.Activity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static net.chavezp.publishmqtt.MainActivity.textviewEstado;


/**
 * Created by pato on 18/06/2017.
 */

public class Suscripcion extends Activity implements MqttCallback{

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        textviewEstado.setText(new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}