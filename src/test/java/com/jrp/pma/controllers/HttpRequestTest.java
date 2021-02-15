package com.jrp.pma.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class HttpRequestTest
{
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void isVersionNumberDisplayedOnHomePage_thenSuccess(){
        String renderedHtml = this.restTemplate.getForObject("http://localhost:" + port + "/", String.class);
//        System.out.println("\n\n\n\n\n\n" + renderedHtml + "\n\n\n\n\n\n");
        assertTrue(renderedHtml.contains("0.0.1"));
    }
}
