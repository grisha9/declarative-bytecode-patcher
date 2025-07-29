[![Maven Central](https://img.shields.io/maven-central/v/io.github.grisha9/declarative-bytecode-patcher.svg)](https://search.maven.org/artifact/io.github.grisha9/declarative-bytecode-patcher)
[![License](https://img.shields.io/github/license/grisha9/declarative-bytecode-patcher)](LICENSE)

# Declarative Bytecode Patcher

A Java agent library that allows for declarative modification of Java bytecode at runtime. This tool makes it easier to instrument and patch Java applications without modifying source code, using simple annotations.

> Uses **javassist** inside

### It is fork from [ytsaurus-patch](https://github.com/ytsaurus/ytsaurus-spyt/blob/main/spark-patch/src/main/java/tech/ytsaurus/spyt/patch/)

[ytsaurus Joker 2024](https://vkvideo.ru/playlist/-796_56/video-796_456240553)

- [declarative-bytecode-patcher](.) - Main library project
- [samples](samples) samples for usage.

## Key Features

- Annotation-based bytecode instrumentation
- Method decoration and interception
- Adding new methods to existing classes
- Simple integration as a Java agent
- Built on Javassist for reliable bytecode manipulation

## Installation

Available on Maven Central:

```xml
<dependency>
    <groupId>io.github.grisha9</groupId>
    <artifactId>declarative-bytecode-patcher</artifactId>
    <version>0.2</version>
</dependency>
```

## Usage

### Patch Classes Naming Convention

Classes containing bytecode patches must include any of these substrings in their name (case-insensitive):

- "patch" (e.g., `ContextPatcher.java`)
- "subclass" (e.g., `ContextSubclass.java`)
- "decorat" (e.g., `AbstractApplicationContextDecorator.java`)

### Basic Example

Example agent for Spring application for printing all bean definitions: [javaagent-spring-sample](samples/spring-print-bean-sample/javaagent-spring)
Instrumenting Spring method [registerBeanPostProcessors](https://github.com/spring-projects/spring-framework/blob/3.0.x/org.springframework.context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java#L410) and printing beans in our custom method _printBeans_

```java
@Decorate
@OriginClass("org.springframework.context.support.AbstractApplicationContext")
public class SpringContextPatcher {

    @DecoratedMethod
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        __registerBeanPostProcessors(beanFactory); // Call original method
        printBeans(beanFactory); // Call our custom method
        throw new RuntimeException("Agent error");
    }

    @AddMethod
    private void printBeans(ConfigurableListableBeanFactory beanFactory) {
        //print beans
    }

    // stub for calling the original method
    protected void __registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    }
}
```

## Sample Projects

This repository includes sample projects demonstrating real-world usage:

### 1. Simple Java Agent Sample

A basic Java agent implementation showing core functionality.

- [Agent implementation](samples/simple-java-sample/javaagent-simple)
- [Application using the agent](samples/simple-java-sample/javaagent-simple-app)
- [Main class and run instructions](samples/simple-java-sample/javaagent-simple-app/src/main/java/com/example/AppSample.java)

### 2. Spring Bean Printer Sample

A practical example showing how to instrument Spring Framework to print all bean definitions.

- [Agent implementation](samples/spring-print-bean-sample/javaagent-spring)
- [Spring application](samples/spring-print-bean-sample/spring-print-bean)
- [Main class and run instructions](samples/spring-print-bean-sample/spring-print-bean/src/main/java/org/springframework/sample/Application.java)

#### How to build and run samples

1. Build samples: `mvn package -f samples`
2. Navigate to a sample project: `cd samples/spring-print-bean-sample`
3. Run: `java -javaagent:javaagent-spring/target/javaagent-spring-1.0.jar -jar spring-print-bean/target/spring-print-bean-1.0.jar`

## How It Works

This library uses Javassist to manipulate bytecode at runtime through a Java agent. The declarative approach uses annotations to specify which classes and methods to modify, making the code more readable and maintainable than traditional bytecode manipulation.

- `@Decorate` - Marks a class as a decorator for an existing class
- `@OriginClass` - Specifies the fully qualified name of the class to be modified
- `@DecoratedMethod` - Marks a method that should replace or enhance an existing method
- `@AddMethod` - Designates a new method to be added to the target class
- `@AddClass` - Added new class to the target application
- `@Subclass` - create subclass for OriginClass
- `@AddInterfaces` - add interfaces for OriginClass

## Credits

This project is a fork of [ytsaurus-patch](https://github.com/ytsaurus/ytsaurus-spyt/blob/main/spark-patch/src/main/java/tech/ytsaurus/spyt/patch/) with enhancements and additional features.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.