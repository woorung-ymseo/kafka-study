package com.kafka.study.chapter3.example01;

import com.kafka.study.callback.PeterProducerCallback;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Properties;

public class Chapter3Example01 {

    @DisplayName("프로듀서 예제 - 메세지 보내고 확인 x")
    @Test
    void example01() {
        Properties props = new Properties(); //Properties 오브젝트를 시작.
        props.put("bootstrap.servers", "kafka1:9091,kafka2:9092,kafka3:9093"); //브로커 리스트를 정의.
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer"); //메시지 키와 벨류에 문자열을 사용하므로 내장된 StringSerializer를 지정.
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props); //Properties 오브젝트를 전달해 새 프로듀서를 생성.

        try {
            for (int i = 0; i < 10; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("woorung-topic01", "우렁 우렁 - " + i); //ProducerRecord 오브젝트를 생성.
                producer.send(record); //send()메소드를 사용하여 메시지를 전송 후 Java Future Ojbect로 RecordMetadata를 리턴 받지만, 리턴값을 무시하므로 메시지가 성공적으로 전송되었는지 알 수 없음.
            }
        } catch (Exception e){
            e.printStackTrace(); //카프카 브로커에게 메시지를 전송한 후의 에러는 무시하지만, 전송 전 에러가 발생하면 예외를 처리할 수 있음.
        } finally {
            producer.close(); // 프로듀서 종료
        }
    }

    @DisplayName("프로듀서 예제 - 메세지 동기 전송")
    @Test
    void example02() {
        Properties props = new Properties(); //Properties 오브젝트를 시작.
        props.put("bootstrap.servers", "kafka1:9091,kafka2:9092,kafka3:9093"); //브로커 리스트를 정의.
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer"); //메시지 키와 벨류에 문자열을 사용하므로 내장된 StringSerializer를 지정.
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props); //Properties 오브젝트를 전달해 새 프로듀서를 생성.

        try {
            for (int i = 0; i < 3; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("woorung-topic01", "우렁 우렁 - " + i); //ProducerRecord 오브젝트를 생성.
                RecordMetadata metadata = producer.send(record).get(); //get() 메소드를 이용해 카프카의 응답을 기다립니다. 메시지가 성공적으로 전송되지 않으면 예외가 발생하고, 에러가 없다면 RecordMetadata를 얻음.
                System.out.printf("Topic: %s, Partition: %d, Offset: %d, Key: %s, Received Message: %s\n", metadata.topic(), metadata.partition()
                        , metadata.offset(), record.key(), record.value());
            }
        } catch (Exception e){
            e.printStackTrace(); //카프카로 메시지를 보내기 전과 보내는 동안 에러가 발생하면 예외가 발생함.
        } finally {
            producer.close(); // 프로듀서 종료
        }
    }

    @DisplayName("프로듀서 예제 - 메세지 비동기 전송")
    @Test
    void example03() {
        Properties props = new Properties(); //Properties 오브젝트를 시작합니다.
        props.put("bootstrap.servers", "kafka1:9091,kafka2:9092,kafka3:9093"); //브로커 리스트를 정의.
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer"); //메시지 키와 벨류에 문자열을 지정하므로 내장된 StringSerializer를 지정함.
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props); //Properties 오브젝트를 전달해 새 프로듀서를 생성.

        try {
            for (int i = 0; i < 3; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("woorung-topic01", "우렁 우렁 - " + i); //ProducerRecord 오브젝트를 생성.
                producer.send(record, new PeterProducerCallback(record)); //프로듀서에서 레코드를 보낼 때 콜백 오브젝트를 같이 보냄.
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            producer.close(); // 프로듀서 종료
        }
    }


    @DisplayName("컨슈머 예제 - 오토커밋")
    @Test
    void example05() {
        Properties props = new Properties(); //Properties 오브젝트를 시작.
        props.put("bootstrap.servers", "kafka1:9091,kafka2:9092,kafka3:9093"); //브로커 리스트를 정의.
        props.put("group.id", "woorung-consumer01"); //컨슈머 그룹 아이디 정의.
        props.put("enable.auto.commit", "true"); //오토 커밋을 사용.
        props.put("auto.offset.reset", "latest"); //컨슈머 오프셋을 찾지 못하는 경우 latest로 초기화 합니다. 가장 최근부터 메시지를 가져옴.
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); //문자열을 사용했으므로 StringDeserializer 지정.
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props); //Properties 오브젝트를 전달하여 새 컨슈머를 생성.
        consumer.subscribe(Arrays.asList("woorung-topic01")); //구독할 토픽을 지정.

        try {
            while (true) { //무한 루프 시작. 메시지를 가져오기 위해 카프카에 지속적으로 poll()을 함.
                ConsumerRecords<String, String> records = consumer.poll(1000); //컨슈머는 폴링하는 것을 계속 유지하며, 타임 아웃 주기를 설정.해당 시간만큼 블럭.
                for (ConsumerRecord<String, String> record : records) { //poll()은 레코드 전체를 리턴하고, 하나의 메시지만 가져오는 것이 아니므로, 반복문 처리.
                    System.out.printf("Topic: %s, Partition: %s, Offset: %d, Key: %s, Value: %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            consumer.close(); //컨슈머를 종료.
        }
    }

    @DisplayName("컨슈머 예제 - 동기 가져오기")
    @Test
    void example06() {
        Properties props = new Properties(); //Properties 오브젝트를 시작.
        props.put("bootstrap.servers", "kafka1:9091,kafka2:9092,kafka3:9093"); //브로커 리스트를 정의.
        props.put("group.id", "woorung-consumer01"); //컨슈머 그룹 아이디 정의.
        props.put("enable.auto.commit", "false"); //오토 커밋을 사용하지 않음.
        props.put("auto.offset.reset", "latest"); //컨슈머 오프셋을 찾지 못하는 경우 latest로 초기화 합니다. 가장 최근부터 메시지를 가져옴.
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); //문자열을 사용했으므로 StringDeserializer 지정.
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props); //Properties 오브젝트를 전달하여 새 컨슈머를 생성.
        consumer.subscribe(Arrays.asList("woorung-topic01")); //구독할 토픽을 지정.

        try {
            while (true) { //무한 루프 시작. 메시지를 가져오기 위해 카프카에 지속적으로 poll()을 함.
                ConsumerRecords<String, String> records = consumer.poll(1000); //컨슈머는 폴링하는 것을 계속 유지하며, 타임 아웃 주기를 설정.해당 시간만큼 블럭함.
                for (ConsumerRecord<String, String> record : records) { //poll()은 레코드 전체를 리턴하고, 하나의 메시지만 가져오는 것이 아니므로, 반복문 처리함.
                    System.out.printf("Topic: %s, Partition: %s, Offset: %d, Key: %s, Value: %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                consumer.commitSync(); //현재 배치를 통해 읽은 모든 메시지들을 처리한 후, 추가 메시지를 폴링하기 전 현재의 오프셋을 동기 커밋.
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            consumer.close(); //컨슈머를 종료.
        }
    }

    @DisplayName("컨슈머 예제 - 비동기 가져오기")
    @Test
    void example07() {
        Properties props = new Properties(); //Properties 오브젝트를 시작.
        props.put("bootstrap.servers", "kafka1:9091,kafka2:9092,kafka3:9093"); //브로커 리스트를 정의.
        props.put("group.id", "woorung-consumer01"); //컨슈머 그룹 아이디 정의.
        props.put("enable.auto.commit", "false"); //오토 커밋을 사용하지 않음.
        props.put("auto.offset.reset", "latest"); //컨슈머 오프셋을 찾지 못하는 경우 latest로 초기화. 가장 최근부터 메시지를 가져옴.
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); //문자열을 사용했으므로 StringDeserializer 지정.
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props); //Properties 오브젝트를 전달하여 새 컨슈머를 생성.
        consumer.subscribe(Arrays.asList("woorung-topic01")); //구독할 토픽을 지정.

        try {
            while (true) { //무한 루프 시작. 메시지를 가져오기 위해 카프카에 지속적으로 poll()을 함.
                ConsumerRecords<String, String> records = consumer.poll(1000); //컨슈머는 폴링하는 것을 계속 유지하며, 타임 아웃 주기를 설정.해당 시간만큼 블럭함.
                for (ConsumerRecord<String, String> record : records) { //poll()은 레코드 전체를 리턴하고, 하나의 메시지만 가져오는 것이 아니므로, 반복문 처리.
                    System.out.printf("Topic: %s, Partition: %s, Offset: %d, Key: %s, Value: %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                consumer.commitAsync(); //현재 배치를 통해 읽은 모든 메시지들을 처리한 후, 추가 메시지를 폴링하기 전 현재의 오프셋을 비동기 커밋합니다.
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            consumer.close(); //컨슈머를 종료.
        }
    }
}
