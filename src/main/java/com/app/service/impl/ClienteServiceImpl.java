package com.app.service.impl;

import com.app.model.entity.Cliente;
import com.app.repository.ClienteRepository;
import com.app.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository repository;

    @Override
    public ResponseEntity buscarTodos(Pageable pageable) {

        Page<Cliente> listaClientes = repository.findAll(pageable);
        if(listaClientes.isEmpty()){
            logger.debug("Não foi encontrado nenhum cliente cadastrado.");
            throw new ResourceNotFoundException("Não há clientes cadastrados no sistema!");
        }
        logger.debug("Retornando a lista de todos os clientes cadastrados.");
        return new ResponseEntity<>(listaClientes, HttpStatus.OK);
    }
}