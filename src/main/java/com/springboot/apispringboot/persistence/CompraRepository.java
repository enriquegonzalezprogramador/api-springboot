package com.springboot.apispringboot.persistence;

import com.springboot.apispringboot.domain.Purchase;
import com.springboot.apispringboot.domain.repository.PurchaseRepository;
import com.springboot.apispringboot.persistence.crud.CompraCrudRepository;
import com.springboot.apispringboot.persistence.entity.Compra;
import com.springboot.apispringboot.persistence.mapper.PurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompraRepository implements PurchaseRepository {

    @Autowired
    private CompraCrudRepository compraCrudRepository;

    @Autowired(required = false)
    private PurchaseMapper mapper;

    @Override
    public List<Purchase> getAll() {
        return mapper.toPurchases((List<Compra>)compraCrudRepository.findAll());
    }

    @Override
    public Optional<List<Purchase>> getByClient(String clientId) {

        return compraCrudRepository.findByIdCliente(clientId)
                .map(compras -> mapper.toPurchases(compras));
    }

    @Override
    public Purchase save(Purchase purchase) {

        Compra compra = mapper.toCompra(purchase);

        compra.getProductos().forEach(producto -> producto.setCompra(compra));


        return mapper.toPurchase(compraCrudRepository.save(compra));
    }
}
