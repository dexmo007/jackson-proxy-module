package com.dexmohq.jackson.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JsonPropertyTests extends TestBase {

    public interface DtoRenamed {
        @JsonProperty("$foo")
        String getFoo();
    }

    @Test
    void testBasicRenaming() throws IOException {
        assertThatThrownBy(() -> mapper.readValue("{\"foo\":\"bar\"}", DtoRenamed.class))
                .isInstanceOf(UnrecognizedPropertyException.class);

        final DtoRenamed dto = mapper.readValue("{\"$foo\":\"bar\"}", DtoRenamed.class);
        assertThat(dto.getFoo()).isEqualTo("bar");
    }


}
