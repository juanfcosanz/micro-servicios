package com.papeleria.app.oauth.config.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.papeleria.app.commons.models.entity.Usuario;
import com.papeleria.app.oauth.models.services.IUsuarioService;

import feign.FeignException;

@Component
public class AuthenticationFaultHandler implements AuthenticationEventPublisher{
	
	private static final Logger log = LoggerFactory.getLogger(AuthenticationFaultHandler.class);
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		
		if(authentication.getAuthorities().size() == 0){
		    return; // para que no use el username de Basic Auth
		}
		
		UserDetails user = (UserDetails) authentication.getPrincipal(); //a traves de UserDetails obtenemos los datos del principal(roles, nombre del usuario)
		log.info(String.format("Success Login: %s", user.getUsername()));
		
		//en caso de autenticación correcta setemaos el parámetro intentos = 0
		Usuario usuarioDb = usuarioService.findByUsername(user.getUsername());
		if (usuarioDb.getIntentos() != null && usuarioDb.getIntentos() > 0) {
			usuarioDb.setIntentos(0);
			usuarioService.update(usuarioDb, usuarioDb.getId());
		}		
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		if(authentication.getAuthorities().size() == 0){
		    return; // para que no use el username de Basic Auth
		}
		
		//log.error("Error, Login: " + exception.getMessage());
		//despues de 3 reintos con error, desa-habilitamos el usuario
		try {			
			Usuario usuarioDb = usuarioService.findByUsername(authentication.getName());
			
			if (usuarioDb.getIntentos() == null) {
				usuarioDb.setIntentos(0);
			}
			
			usuarioDb.setIntentos(usuarioDb.getIntentos() + 1);
			if(usuarioDb.getIntentos() >= 3) {
				log.info(String.format("El usuario %s des-habilitado por máximos de reintentos.", authentication.getName()));
				usuarioDb.setEnabled(false);
			}
			
			usuarioService.update(usuarioDb, usuarioDb.getId());
		} catch (FeignException e) {
			log.error(String.format("El usuario %s no éxiste en el sistema.", authentication.getName()));
		}
	}

}
