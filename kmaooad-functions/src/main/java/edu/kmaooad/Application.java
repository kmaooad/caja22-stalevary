package edu.kmaooad;

import edu.kmaooad.core.Dispatcher;
import edu.kmaooad.core.session.ISessionService;
import edu.kmaooad.core.state.StateMachine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Dispatcher dispatcher(StateMachine stateMachine, ISessionService sessionService) {
        return new Dispatcher(stateMachine, sessionService);
    }
}
