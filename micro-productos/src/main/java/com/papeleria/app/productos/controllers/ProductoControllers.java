package com.papeleria.app.productos.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.papeleria.app.commons.models.entity.Producto;
import com.papeleria.app.productos.models.services.IProductoService;

@RestController
@RequestMapping
public class ProductoControllers {
	
	@Autowired
	private Environment env;	
	
	@Value("${server.port}")
	private Integer port;
	
	@Autowired
	private IProductoService iProductoService;
	
	@GetMapping("/listar")
	public List<Producto> listar(){
		return iProductoService.findAll().stream().map(p-> {
			//p.setPort(Integer.parseInt(env.getProperty("local.server.port")));
			p.setPort(port);
			return p;
		}).collect(Collectors.toList());
	}
	
	@GetMapping("/ver/{id}")
	public Producto detalle(@PathVariable Long id) throws Exception {
		Producto producto = iProductoService.findById(id);
		if (producto == null) {
			throw new Exception("Error, no se puede cargar el producto");
		}
		
		if (producto.getId() == 1){
			Thread.sleep(2000L);
		}
		producto.setPort(Integer.parseInt(env.getProperty("local.server.port")));
		return producto;
	}
	
	@PostMapping("/crear")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return iProductoService.save(producto);
	}
	
	@PutMapping("/editar/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable Long id) throws Exception {
		Producto productoDb = iProductoService.findById(id);
		if (productoDb != null) {
			productoDb.setNombre(producto.getNombre());
			productoDb.setPrecio(producto.getPrecio());			
		} else {
			throw new Exception(String.format("El producto %s no éxisteñ.", producto.getNombre()));
		}
		return iProductoService.save(productoDb);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		iProductoService.deleteById(id);
	}

}
