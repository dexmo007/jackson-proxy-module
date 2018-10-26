package com.dexmohq.jackson.test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InMixWithTypeResolverTests extends TestBase {

    @JsonDeserialize(as = DtoImpl.class)
    public interface DtoDeserializeAs {
        String getFoo();
    }

    @Test
    void testDeserializeAs() throws IOException {
        final DtoDeserializeAs dto = mapper.readValue("{\"foo\":\"bar\"}", DtoDeserializeAs.class);
        assertThat(dto).isInstanceOf(DtoImpl.class);
        assertThat(dto.getFoo()).isEqualTo("bar");
    }

    @Data
    public static class DtoImpl implements DtoDeserializeAs {
        private String foo;
    }
}
