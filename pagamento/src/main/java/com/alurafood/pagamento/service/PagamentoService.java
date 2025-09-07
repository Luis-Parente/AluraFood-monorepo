package com.alurafood.pagamento.service;

import com.alurafood.pagamento.dto.PagamentoDTO;
import com.alurafood.pagamento.http.PedidoClient;
import com.alurafood.pagamento.model.Pagamento;
import com.alurafood.pagamento.model.Status;
import com.alurafood.pagamento.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PedidoClient pedido;

    public Page<PagamentoDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(pagamento -> mapper.map(pagamento, PagamentoDTO.class));
    }

    public PagamentoDTO findById(Long id) {
        Pagamento resultado = repository.findById(id).orElseThrow(EntityNotFoundException::new);

        return mapper.map(resultado, PagamentoDTO.class);
    }

    public PagamentoDTO insert(PagamentoDTO dto) {
        Pagamento novoPagamento = mapper.map(dto, Pagamento.class);
        novoPagamento.setStatus(Status.CRIADO);
        repository.save(novoPagamento);

        return mapper.map(novoPagamento, PagamentoDTO.class);
    }

    public PagamentoDTO update(Long id, PagamentoDTO dto) {
        Pagamento pagamentoAtualizado = mapper.map(dto, Pagamento.class);
        pagamentoAtualizado.setId(id);
        repository.save(pagamentoAtualizado);

        return mapper.map(pagamentoAtualizado, PagamentoDTO.class);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void confirmPayment(Long id){
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        repository.save(pagamento.get());
        pedido.updatePayment(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        repository.save(pagamento.get());
    }
}
