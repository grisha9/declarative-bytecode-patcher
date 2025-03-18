package com.example;

/**
 * For run sample from root project dir (default declarative-bytecode-patcher):
 *  1) build samples projects: mvn package -f samples
 *  2) go to dir samples/simple-java-sample
 *  3) run: java --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED -javaagent:javaagent-simple/target/javaagent-simple-1.0.jar -jar javaagent-simple-app/target/javaagent-simple-app-1.0.jar
 */
public class AppSample {

    public static void main(String[] args) {
        System.out.print("Dog: " );
        new Dog().woof();
        System.out.println("------------");

        System.out.print("Cat: " );
        new Cat().meow();
        System.out.println("------------");

        System.out.println("Any animal: " );
        new AnyAnimal().sayAny("I am any animal");
        System.out.println("------------");
    }
}
