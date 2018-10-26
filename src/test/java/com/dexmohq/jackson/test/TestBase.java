package com.dexmohq.jackson.test;

import com.dexmohq.jackson.ProxyModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

    ObjectMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new ObjectMapper().registerModule(new ProxyModule());
    }

}
