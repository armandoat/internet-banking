package com.app.controller;

import com.app.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    @GetMapping("/")
    public ResponseEntity getClientes(Pageable pageable){
        logger.debug("Retornar todos os clientes cadastrados.");
        return clienteService.buscarTodos(pageable);
    }
}
