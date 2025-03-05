package com.app.service.impl;

import com.app.model.entity.Cliente;
import com.app.repository.ClienteRepository;
import com.app.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    @Override
    public ResponseEntity buscarTodos(Pageable pageable) {

        Page<Cliente> listaClientes = repository.findAll(pageable);
        if(listaClientes.isEmpty()){
            log.debug("Não foi encontrado nenhum cliente cadastrado.");
            throw new ResourceNotFoundException("Não há clientes cadastrados no sistema!");
        }
        log.debug("Retornando a lista de todos os clientes cadastrados.");
        return new ResponseEntity<>(listaClientes, HttpStatus.OK);
    }
}