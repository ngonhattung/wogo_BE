package com.nhattung.wogo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WogoConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
