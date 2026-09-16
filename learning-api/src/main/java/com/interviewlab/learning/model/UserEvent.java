package com.interviewlab.learning.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO REST y evento Kafka. El mismo contrato se valida al entrar por HTTP y luego se serializa
 * como JSON; en producción conviene versionar eventos cuando el esquema evoluciona.
 */
public record UserEvent(@NotBlank String id, @NotBlank String name, @Email String email) { }
