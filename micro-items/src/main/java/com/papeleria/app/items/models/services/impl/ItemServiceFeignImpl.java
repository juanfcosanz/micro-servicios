package com.papeleria.app.items.models.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.papeleria.app.commons.models.entity.Producto;
import com.papeleria.app.items.clients.ProductoClientRest;
import com.papeleria.app.items.models.Item;
import com.papeleria.app.items.models.services.IItemService;

@Service("serviceFeign")
//@Primary
public class ItemServiceFeignImpl implements IItemService{
	
	@Autowired
	private ProductoClientRest clienteFeignRest;

	@Override
	public List<Item> findAll() {
		return clienteFeignRest.listar().stream().map(p -> new Item(p, 1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		return new Item(clienteFeignRest.detalle(id), cantidad);
	}

	@Override
	public Producto save(Producto producto) {
		return clienteFeignRest.crear(producto);
	}

	@Override
	public Producto update(Producto producto, Long id) {
		return clienteFeignRest.update(producto, id);
	}

	@Override
	public void dalete(Long id) {
		clienteFeignRest.eliminar(id);
	}

}
