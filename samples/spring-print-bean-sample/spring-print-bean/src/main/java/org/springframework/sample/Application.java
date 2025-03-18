package org.springframework.sample;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * For run sample from root project dir (default declarative-bytecode-patcher):
 *  1) build samples projects: mvn package -f samples
 *  2) go to dir samples/spring-print-bean-sample
 *  3) run: java -javaagent:javaagent-spring/target/javaagent-spring-1.0.jar -jar spring-print-bean/target/spring-print-bean-1.0.jar
 */
@SpringBootApplication(scanBasePackages = "org.springframework.sample")
public class Application {
    @Autowired
    TestComponentBean componentBean;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Component
class TestComponentBean {
}

