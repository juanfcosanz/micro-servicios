package com.papeleria.app.oauth.models.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.papeleria.app.commons.models.entity.Usuario;
import com.papeleria.app.oauth.clientRest.UsuarioClientRest;

import feign.FeignException;

@Service //para que sea un componente de spring y podamos inyectarnlo a otra clases
public class UsuarioService implements UserDetailsService, IUsuarioService {
	
	private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	private UsuarioClientRest clientRest;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		try {
			Usuario usuario = clientRest.findByUsernameIgnoreCase(username); //recuperamos el usuario
			
			List<GrantedAuthority> authorities = usuario.getRoles().stream() //obtenemos los roles de tipo List y los castemaos a GrantedAuthority con Stream
					.map(role -> new SimpleGrantedAuthority(role.getNombre())) //por cada role lo convertimos a GrantedAuthority mediante la calse concreta SimpleGrantedAuthority
					.peek(authority -> log.info("Role: " + authority.getAuthority()))  //muestra el nombre de cada rol por medio de cada authority
					.collect(Collectors.toList()); // convertimos a una lista de tipo GrantedAuthority
			
			log.info("Usuario authenticado: "+ username);
			
			//retona un UserDetails que representa un User de spring security autenticado
			return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);  
		} catch (FeignException e) {
			log.error(String.format("Error, no existe el usuario %s en el sistema.", username));
			throw new UsernameNotFoundException(String.format("Error, no existe el usuario %s en el sistema.", username));
		}
	}

	@Override
	public Usuario findByUsername(String username) {
		return clientRest.findByUsernameIgnoreCase(username);
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {
		return clientRest.update(usuario, id);
	}
	
}
