### Declarative java agent byte code patcher   
Uses **javassist** inside

### It is fork from [ytsaurus-patch](https://github.com/ytsaurus/ytsaurus-spyt/blob/main/spark-patch/src/main/java/tech/ytsaurus/spyt/patch/)
[ytsaurus Joker 2024](https://vkvideo.ru/playlist/-796_56/video-796_456240553)


- [declarative-bytecode-patcher](.) - Main library project
- [samples](samples) samples for usage.

#### Available on Maven Central   
```xml
<dependency>
    <groupId>io.github.grisha9</groupId>
    <artifactId>declarative-bytecode-patcher</artifactId>
    <version>0.1</version>
</dependency>
```

#### Documentation and samples

Classes with byte code patches must contain any substrings from {"patch", "subclass", "decorat"} in the class name in any case.   
Examples: AbstractApplicationContextDecorator.java, ContextPatcher.java, ContextSubclass.java.

Example agent for Spring application for printing all bean definitions: [javaagent-spring-sample](samples/spring-print-bean-sample/javaagent-spring) 
Instrumenting Spring method [registerBeanPostProcessors](https://github.com/spring-projects/spring-framework/blob/3.0.x/org.springframework.context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java#L410) and printing beans in our custom method *printBeans*
```java
@Decorate
@OriginClass("org.springframework.context.support.AbstractApplicationContext")
public class SpringContextPatcher {

    @DecoratedMethod
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        __registerBeanPostProcessors(beanFactory);
        printBeans(beanFactory);
        throw new RuntimeException("Agent error");
    }

    @AddMethod
    private void printBeans(ConfigurableListableBeanFactory beanFactory) {
        //print beans
    }

    //stub for calling original method - registerBeanPostProcessors
    protected void __registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    }
}
```


- [simple-java-sample](samples/simple-java-sample) - simple java agent sample. [agent-sample](samples/simple-java-sample/javaagent-simple), [agent-usage-sample](samples/simple-java-sample/javaagent-simple-app)  
  Main class and instruction for run: [AppSample.java](samples/simple-java-sample/javaagent-simple-app/src/main/java/com/example/AppSample.java)  
- [spring-print-bean-sample](samples/spring-print-bean-sample) - Spring app sample for printing bean definitions from Spring Context. [javaagent-spring-sample](samples/spring-print-bean-sample/javaagent-spring), [spring-agent-usage-sample](samples/spring-print-bean-sample/spring-print-bean)  
  Main class and instruction for run: [Application.java](samples/spring-print-bean-sample/spring-print-bean/src/main/java/org/springframework/sample/Application.java)

#### Run Spring sample instruction (from [Application.java](samples/spring-print-bean-sample/spring-print-bean/src/main/java/org/springframework/sample/Application.java)):
```
For run sample from root project dir (default declarative-bytecode-patcher):
  1) build samples projects: mvn package -f samples
  2) go to dir: samples/spring-print-bean-sample
  3) run: java -javaagent:javaagent-spring/target/javaagent-spring-1.0.jar -jar spring-print-bean/target/spring-print-bean-1.0.jar
```