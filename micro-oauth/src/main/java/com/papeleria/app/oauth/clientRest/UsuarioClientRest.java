package com.papeleria.app.oauth.clientRest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.papeleria.app.commons.models.entity.Usuario;

@FeignClient(name = "servicio-usuarios")
public interface UsuarioClientRest {
	
	@GetMapping("/usuarios/search/buscar-username")
	public Usuario findByUsernameIgnoreCase(@RequestParam String nombre);
	
	@PutMapping("/usuarios/{id}")
	public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id);

}
