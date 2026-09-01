package br.com.unisenaisc.cadastro_produtos.service;

import br.com.unisenaisc.cadastro_produtos.model.Cliente;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;
    private final SMSService smsService;

    public NotificationService(EmailService emailService, SMSService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public void notificar(Cliente cliente, String acao){

        if(acao.equals("Compra")){
            emailService.send(cliente.getEmail());
        } else {
            smsService.send(cliente.getFone());
            emailService.send(cliente.getEmail());
        }

    }


}
