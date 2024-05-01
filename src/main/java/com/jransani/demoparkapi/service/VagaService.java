package com.jransani.demoparkapi.service;

import com.jransani.demoparkapi.entity.Vaga;
import com.jransani.demoparkapi.exception.CodigoUniqueViolationException;
import com.jransani.demoparkapi.exception.EntityNotFoundException;
import com.jransani.demoparkapi.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VagaService {

    private final VagaRepository vagaRepository;

    @Transactional
    public Vaga salvar(Vaga vaga){
        try{
            return vagaRepository.save(vaga);
        }catch (DataIntegrityViolationException ex){
            throw new CodigoUniqueViolationException(String.format("Vaga com código %s já cadastrada", vaga.getCodigo())
            );
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo){
        return vagaRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vaga com cógio %s não foi encontrada", codigo))
        );
    }
}
