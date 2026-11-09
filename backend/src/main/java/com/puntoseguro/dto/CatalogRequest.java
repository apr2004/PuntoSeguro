package com.puntoseguro.dto;
import jakarta.validation.constraints.*; import lombok.Getter; import lombok.Setter;
@Getter @Setter public class CatalogRequest { @NotBlank @Size(max=50) private String nombre; }
