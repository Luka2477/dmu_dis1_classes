package task6;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;

public class App {
  public static void  publishMessage(MqttClient sampleClient, String topicsend, String content) throws MqttException {
    // Laver en publish p� sampleClient med topic topicsend og indhold content.
    MqttMessage message = new MqttMessage();
    message.setPayload(content.getBytes());
    System.out.println(Arrays.toString(content.getBytes()));
    sampleClient.publish(topicsend, message);
    System.out.println("Message published");
  }

  public static void main(String[] args) {
    String broker = "tcp://192.168.1.1:1883";
    MemoryPersistence persistence = new MemoryPersistence();
    try {
      MqttClient sampleClient = new MqttClient(broker,  MqttClient.generateClientId(), persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      System.out.println("Connecting to broker: " + broker);
      sampleClient.connect(connOpts);
      System.out.println("Connected");

      String topic = "cmnd/grp7719/Power1";

      publishMessage(sampleClient, topic, "1");
      Thread.sleep(5000);
      publishMessage(sampleClient, topic, "0");

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
