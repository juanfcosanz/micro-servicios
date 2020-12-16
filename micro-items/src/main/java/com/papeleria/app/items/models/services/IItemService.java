package com.papeleria.app.items.models.services;

import java.util.List;

import com.papeleria.app.commons.models.entity.Producto;
import com.papeleria.app.items.models.Item;

public interface IItemService {
	
	public List<Item> findAll();
	
	public Item findById(Long id, Integer cantidad);
	
	public Producto save(Producto producto);
	
	public Producto update(Producto producto, Long id);
	
	public void dalete(Long id);

}
