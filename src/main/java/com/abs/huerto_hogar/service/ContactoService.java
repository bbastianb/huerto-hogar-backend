package com.abs.huerto_hogar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.abs.huerto_hogar.model.Contacto;
import com.abs.huerto_hogar.repository.ContactoRepository;

@Service
public class ContactoService {

    private final ContactoRepository repo;

    public ContactoService(ContactoRepository repo) {
        this.repo = repo;
    }

    public Contacto guardar(Contacto contacto) {
        return repo.save(contacto);
    }

    public List<Contacto> listarTodos() {
        return repo.findAll();
    }

    public Contacto buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
