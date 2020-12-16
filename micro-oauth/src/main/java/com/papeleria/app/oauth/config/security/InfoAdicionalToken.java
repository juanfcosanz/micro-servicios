package com.papeleria.app.oauth.config.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.papeleria.app.commons.models.entity.Usuario;
import com.papeleria.app.oauth.models.services.IUsuarioService;

@Component
public class InfoAdicionalToken implements TokenEnhancer{
	
	@Autowired
	private IUsuarioService usuarioService;  //implementamos una interfaz solo para obtener los datos del usuario

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		Map<String, Object> info = new HashMap<>();
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		info.put("nombre", usuario.getNombre());
		info.put("apellido", usuario.getApellido());
		info.put("correo", usuario.getEmail());
		
		//debemos implementar la clase concreta con DefaultOAuth2AccessToken para invocar el método setAdditionalInformation
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info); //agregamos los CLAIMS al token
		
		return accessToken;
	}
	
}
