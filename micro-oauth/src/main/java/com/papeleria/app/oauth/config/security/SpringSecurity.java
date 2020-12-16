package com.papeleria.app.oauth.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationEventPublisher eventPublisher;
	
	@Autowired
	private UserDetailsService usuarioService; //spring va a buscar un bean que implemente la interfaz UserDetailsService y lo inyecta -> en nuestro caso fue en la clase Service UsuarioService

	//registra el UserDetailsService en nuestro Autentication Manager
	@Override
	@Autowired //como se pasa el arguemnto AuthenticationManagerBuilder debemos de inyectar mediante el metodo
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.usuarioService) //registramos nuestro impl de UserDetailsService
			.passwordEncoder(passwordEncoder())  //encriptamos la contraseña para mayor seguridad con BCrypt
			.and()
			.authenticationEventPublisher(eventPublisher); //registramos nuestro manejador de errrores en caso de Exito/ Error
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception { //para inyectar en la configuración de Oauth2
		return super.authenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {  //para encriptar la contraseña en el processoo de authenticacion en SpringSecurity y configurar Oauth2
		return new BCryptPasswordEncoder();
	}	

}
