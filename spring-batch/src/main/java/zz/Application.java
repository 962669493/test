package zz;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
@EnableBatchProcessing
public class Application {
    public static void main(String[] args) {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>(2);
        objectObjectHashMap.put(1,1);
        objectObjectHashMap.put(2,1);
        objectObjectHashMap.put(3,1);
        SpringApplication.run(Application.class, args);
    }
}
