package com.abs.huerto_hogar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service // Indica que esta clase contiene servicio
public class EmailService {

    @Autowired
    private JavaMailSender emailSender; // Inyecta el bean JavaMailSender de Spring

    public void enviarEmail(String para, String asunto, String texto) {
        SimpleMailMessage mensaje = new SimpleMailMessage(); // Crea un objeto SimpleMailMessage
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        emailSender.send(mensaje);

    }
}
