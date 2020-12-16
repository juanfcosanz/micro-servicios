package com.papeleria.app.items.models.services.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.papeleria.app.commons.models.entity.Producto;
import com.papeleria.app.items.models.Item;
import com.papeleria.app.items.models.services.IItemService;

@Service("serviceTemplate")
public class ItemServiceImpl implements IItemService {

	@Autowired
	private RestTemplate clienteRest;
	
	private String endpoint = "http://servicio-productos";
	
	@Override
	public List<Item> findAll() {
		List<Producto> productos = Arrays.asList(clienteRest.getForObject(endpoint + "/listar", Producto[].class));
		return productos.stream().map(p -> new Item(p, 1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		Map<String, Long> pathVariables = new HashMap<String, Long>();
		pathVariables.put("id", id);
		Producto producto = clienteRest.getForObject(endpoint + "/ver/{id}", Producto.class, pathVariables);
		return new Item(producto, cantidad);
	}

	@Override
	public Producto save(Producto producto) {
		HttpEntity<Producto> body = new HttpEntity<Producto>(producto);
		ResponseEntity<Producto> response = clienteRest.exchange(endpoint + "/crear", HttpMethod.POST, body, Producto.class);
		return response.getBody();
	}

	@Override
	public Producto update(Producto producto, Long id) {
		Map<String, Long> pathVariables = new HashMap<String, Long>();
		pathVariables.put("id", id);
		HttpEntity<Producto> body = new HttpEntity<Producto>(producto);
		ResponseEntity<Producto> response = clienteRest.exchange(endpoint + "/editar/{id}", HttpMethod.POST, body, Producto.class, pathVariables);
		return response.getBody();
	}

	@Override
	public void dalete(Long id) {
		Map<String, Long> pathVariables = new HashMap<String, Long>();
		pathVariables.put("id", id);
		clienteRest.delete(endpoint + "/eliminar/{id}", pathVariables);
	}

}
