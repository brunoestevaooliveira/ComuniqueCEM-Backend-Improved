package com.comuniquecem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ComuniqueCEM Backend Application - Versão Melhorada
 * 
 * Sistema de comunicação educacional moderno com:
 * - Arquitetura robusta e escalável
 * - Segurança avançada com JWT
 * - WebSocket para chat em tempo real
 * - Cache Redis para performance
 * - Documentação automática
 * - Testes abrangentes
 * - Monitoramento e observabilidade
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class ComuniqueCemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComuniqueCemApplication.class, args);
    }
}
