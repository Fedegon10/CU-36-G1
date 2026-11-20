package ar.edu.utn.dsi.bolsines.dto;

// Datos del correo enviado al Gerente de la CM destino
public record MailEnviadoDTO(String destinatario, String nombreGerente, String asunto, String cuerpo) {
}
