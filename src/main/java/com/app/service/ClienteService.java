package com.app.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ClienteService {

    ResponseEntity buscarTodos(Pageable pageable);
}
