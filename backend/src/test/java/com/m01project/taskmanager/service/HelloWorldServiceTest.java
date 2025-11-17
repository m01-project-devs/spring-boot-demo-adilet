package com.m01project.taskmanager.service;

import org.junit.jupiter.api.Test;

import com.m01project.taskmanager.demo.service.HelloWorldService;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldServiceTest {

    private final HelloWorldService service = new HelloWorldService();

    @Test
    void testSayHelloWithName() {
        assertEquals("Hello Adilet", service.sayHelloTo("Adilet"));
    }

    @Test
    void testSayHelloWithoutName() {
        assertEquals("Error: In Islam, a child is named through Azan — please provide a name!", service.sayHelloTo(""));
    }
}