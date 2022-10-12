package task7;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Arrays;

public class SimpleMqttCallBack implements MqttCallback {
  private final MqttClient mqttClient;

  private int status = 0;

  public SimpleMqttCallBack(MqttClient mqttClient) {
    this.mqttClient = mqttClient;
  }

  public void connectionLost(Throwable throwable) {
    System.out.println("Connection to MQTT broker lost!");
  }

  public void messageArrived(String s, MqttMessage mqttMessage) {
    String res = new String(mqttMessage.getPayload());

    JSONObject jo = new JSONObject(res);
    JSONObject data = jo.getJSONObject("AM2301");
    double temp = data.getDouble("Temperature");
    double hum = data.getDouble("Humidity");

    if (hum > 20 && status == 0) {
      status = 1;
      publishMessage("1");
    } else if (hum <= 20 && status == 1) {
      status = 0;
      publishMessage("0");
    }

    System.out.printf("Temp: %.2f | Hum: %.2f%n", temp, hum);
  }

  public void  publishMessage(String content) {
    MqttMessage message = new MqttMessage();
    message.setPayload(content.getBytes());
    System.out.println(Arrays.toString(content.getBytes()));
    String topic = "cmnd/grp7719/Power1";

    try {
      mqttClient.publish(topic, message);
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }

    System.out.println("Message published");
  }

  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    // do nothing...
  }

  public static void main(String[] args) {
    String broker = "tcp://192.168.1.1:1883";
    MemoryPersistence persistence = new MemoryPersistence();
    try {
      MqttClient sampleClient = new MqttClient(broker,  MqttClient.generateClientId(), persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      System.out.println("Connecting to broker: " + broker);

      sampleClient.setCallback(new SimpleMqttCallBack(sampleClient));

      sampleClient.connect(connOpts);
      System.out.println("Connected");

      String topic = "tele/grp7719/SENSOR";
      sampleClient.subscribe(topic);
      Thread.sleep(200000);

      sampleClient.disconnect();
      System.out.println("Disconnected");
      System.exit(0);
    } catch (MqttException me) {
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
