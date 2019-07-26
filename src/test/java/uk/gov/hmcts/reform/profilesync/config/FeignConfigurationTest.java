package uk.gov.hmcts.reform.profilesync.config;

import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.junit.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.assertj.core.api.Assertions.assertThat;

public class FeignConfigurationTest {


    @Configuration
    public class FeignConfiguration {
        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        @Primary
        Encoder feignFormEncoder() {
            return new FormEncoder(new SpringEncoder(this.messageConverters));
        }
    }


    @Test
    public void testFeignFormEncoder() {
        FeignConfiguration sut = new FeignConfiguration();

        Encoder result = sut.feignFormEncoder();

        assertThat(result).isNotNull();

    }
}