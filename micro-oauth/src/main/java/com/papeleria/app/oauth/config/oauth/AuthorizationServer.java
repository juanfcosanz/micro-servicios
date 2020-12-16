package com.papeleria.app.oauth.config.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.papeleria.app.oauth.config.security.InfoAdicionalToken;

@Configuration
@EnableAuthorizationServer
@RefreshScope
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter{
	
	@Autowired
	private Environment env;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private InfoAdicionalToken infoAdicional;
	
	//registra nuestro AuthenticationManager en el AuthorizationServer, el token storage de tipo jwt y el access token converter que se encarga de guardar los datos del usuario en el token(claims)
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {  //es invocado/manejado por el endpoint de Oauth2 -> /oauth/token que se encagra de genrar el token
		
		//unimos los nuevos CLAIMS del TokenEnhancer con JwtAccessTokenConverter
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicional, accessTokenConverter()));
		
		endpoints.authenticationManager(this.authenticationManager) //registramos el AuthenticationManager
				.tokenStore(tokenStore())//genera el token con los datos de JwtAccessTokenConverter
				.accessTokenConverter(accessTokenConverter()) //configuramos el token de tipo JwtAccessTokenConverter junto con su codigo secreto
				.tokenEnhancer(tokenEnhancerChain) //agregamos a la configuración del endpoint la nueva cadena de tokenEnhancerChain
				;
	}
	
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter()); //para crear el token, necesitamos los datos de JwtAccessTokenConverter
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(env.getProperty("config.security.oauth.jwt.key"));//codigo secreto para firma el token y también será el usaremos en el Resource Server para validar el token que sea correcto.
		return tokenConverter;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {  //permiso que se asignara a los endpoints del servidor de Autorizacion(oath2) para generar y validar el token 
		security.tokenKeyAccess("permitAll()") //cualquier cliente tiene acceso a la ruta  /oauth/token(POST) para autenticarnos y genera el token
				.checkTokenAccess("isAuthenticated()"); //valida el token, pero para eso debe estar autenticado por medio del metodo de spring isAuthenticated() que valida que el cleinte este autenticado				
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {  //configura las aplicaiones clientes que van acceder a nuestros micro-servicios con su cliente_id y secret_id
		clients.inMemory()//podemos configurarlos en memoria, JDBC, u otro tipo de almacenamiento.
				.withClient(env.getProperty("config.security.oauth.client.id"))  //cliente_id -> identificador de nuetro cliente
				.secret(passwordEncoder.encode(env.getProperty("config.security.oauth.client.secret"))) //secret_id -> constraseña encriptada por Bcrypt
				.scopes("read", "write") //permisos de nuestro cliente
				.authorizedGrantTypes("password","refresh_token")  //tipo de autenticaión para obtener el token -> password: cuando se usan credenciales(user y pass), 
				//la concesion refresh_token nos permite obtener un nuevo token justo antes de que caduque el token actual
				.accessTokenValiditySeconds(3600) //configuramos el tiempo que nuestro token será valido, antes de quee caduque
				.refreshTokenValiditySeconds(3600)  //tiempo del refresh_token
				.and()
				.withClient("frontendappphp")  //para otro cliente
				.secret(passwordEncoder.encode("12345"))
				.scopes("read")
				.authorizedGrantTypes("password")
				.accessTokenValiditySeconds(3600);
	}

}
