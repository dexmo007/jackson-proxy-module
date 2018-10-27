package com.dexmohq.jackson.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class BasicReadValuesTests extends TestBase {

    public interface Dto {
        int getFoo();
    }

    @Test
    void testBasic() throws IOException {
        final Dto[] dtos = mapper.readValue("[" +
                "{\"foo\":1}," +
                "{\"foo\":2}," +
                "{\"foo\":3}" +
                "]", Dto[].class);
        assertThat(Arrays.stream(dtos).map(Dto::getFoo)).containsExactly(1, 2, 3);
    }
}
