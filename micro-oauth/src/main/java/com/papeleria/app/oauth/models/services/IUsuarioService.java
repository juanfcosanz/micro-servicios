package com.papeleria.app.oauth.models.services;

import com.papeleria.app.commons.models.entity.Usuario;

public interface IUsuarioService {
	
	public Usuario findByUsername(String username);
	
	public Usuario update(Usuario usuario, Long id);
}
