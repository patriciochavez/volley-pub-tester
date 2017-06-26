package net.chavezp.publishmqtt;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static net.chavezp.publishmqtt.MainActivity.buttonTemperatura;
import static net.chavezp.publishmqtt.MainActivity.textviewEstado;
import static net.chavezp.publishmqtt.MainActivity.buttonLuzPorton;
import static net.chavezp.publishmqtt.MainActivity.buttonBuzzer;


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
                buttonLuzPorton.setBackgroundColor(Color.argb(100, 0, 255, 128));
                buttonLuzPorton.setText("Reflector Encendido");
                }
            else if (response.contains("apagado")){
                buttonLuzPorton.setBackgroundColor(Color.GRAY);
                buttonLuzPorton.setText("Reflector Apagado");
                    }
        }
        if (topic.contains("casa/temperatura/living")){
            buttonTemperatura.setBackgroundColor(Color.argb(100, 255, 0, 128));
            buttonTemperatura.setText("Living: " + response + " °C");
        }

        //textviewEstado.setText(response);

        if (topic.contains("casa/buzzer/sonido")){
            if (response.contains("encendido")){
                buttonBuzzer.setBackgroundColor(Color.argb(100, 0, 255, 128));
                buttonBuzzer.setText("Sonido Activado");
            }
            else if (response.contains("apagado")){
                buttonBuzzer.setBackgroundColor(Color.GRAY);
                buttonBuzzer.setText("Sonido Desactivado");
                }
            }


        }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}