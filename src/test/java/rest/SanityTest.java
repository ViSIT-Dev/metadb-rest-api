package rest;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rest.web.controller.DigitalRepresentationController;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Short test to check if controllers are correctly mapped for testing purposes.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SanityTest {

    @Autowired
    DigitalRepresentationController controller;

    @Test
    public void contextLoads() {assertThat(controller).isNotNull();}
}
