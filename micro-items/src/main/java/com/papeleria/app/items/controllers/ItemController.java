package com.papeleria.app.items.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.papeleria.app.commons.models.entity.Producto;
import com.papeleria.app.items.models.Item;
import com.papeleria.app.items.models.services.IItemService;

@RestController
@RequestMapping
@RefreshScope
public class ItemController {
	
	@Autowired
	@Qualifier("serviceFeign")
	private IItemService iItemService;
	
	@Value("${configuracion.texto}")
	private String texto;
	
	@Autowired
	private Environment env; //obtenemos el perfil

	@GetMapping("/config")
	public ResponseEntity<?> obtenerConfig( @Value("${server.port}") String port){
		Map<String, String> json = new HashMap<>();
		json.put("text", texto);
		json.put("port", port);
		
		if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
			json.put("autor.nombre", env.getProperty("autor.nombre"));
			json.put("autor.email", env.getProperty("autor.email"));
		}
		return new ResponseEntity<>(json, HttpStatus.OK);
	}
	
	@GetMapping("/listar")
	public List<Item> listar(){
		return iItemService.findAll();
	}
	
	@HystrixCommand(fallbackMethod = "metodoAlternativo")
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
		return iItemService.findById(id, cantidad);
	}
	
	public Item metodoAlternativo(Long id, Integer cantidad) {
		Producto producto = new Producto();
		producto.setId(id);
		producto.setNombre("Desconocido");
		producto.setPrecio(0.0);
		Item item = new Item(producto , cantidad);
		return item;
	}
	
	@PostMapping("/crear")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return iItemService.save(producto);
	}
	
	@PutMapping("/editar/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable Long id){
		return iItemService.update(producto, id);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		iItemService.dalete(id);
	}
}
