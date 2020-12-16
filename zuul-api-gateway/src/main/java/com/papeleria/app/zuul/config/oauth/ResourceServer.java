package com.papeleria.app.zuul.config.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
@RefreshScope
public class ResourceServer extends ResourceServerConfigurerAdapter {
	
	@Value("${config.security.oauth.jwt.key}")
	private String jwtKey;//como es una sola propiedad se puede inyectar con @Value
	
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter()); //para crear el token, necesitamos los datos de JwtAccessTokenConverter
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(jwtKey);//codigo secreto para firma el token y también será el que usaremos en el Resource Server para validar el token que sea correcto.
		return tokenConverter;
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore());//configuramos el tokenStore
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()   //protegemos cada ruta de Zuul
			.antMatchers("/api/security/oauth/token")    //establecamos la ruta para darle permisos para cualquier verbo(POST, GET, PUT, DELETE)
			.permitAll()   //todo usuario se puede autenticar
			.antMatchers(HttpMethod.GET, "/api/productos/listar", "/api/items/listar", "/api/usuarios/usuarios").permitAll()  //todos estos endpoint de tipo GET, son de acceso publico
			.antMatchers(HttpMethod.GET, "/api/productos/ver/{id}", "/api/items/ver/{id}/cantidad/{cantidad}").hasAnyRole("ADMIN", "USER")  //no es necesario agregar el prefijo ROLE_ ya que es insertado por debajo,
																																			//en la base de datos si lo debe llevar
			.antMatchers(HttpMethod.POST, "/api/productos/crear", "/api/items/crear").hasRole("ADMIN")  //solo los admin puede crear
			.antMatchers(HttpMethod.PUT, "/api/productos/editar/{id}", "/api/items/editar/{id}", "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/api/productos/eliminar/{id}", "/api/items/eliminar/{id}", "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
			.anyRequest().authenticated()  //para cualquier otra ruta no configurada va requeririr autentificaión
			.and()
			.cors()   //habilitamos los CORS
			.configurationSource(corsConfigurationSource()); //configuramos el CORS
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();  
		corsConfig.addAllowedOrigin("*"); //configuramos nuestras aplicaciones clientes(origenes), con * agregamos de forma automatica cualquier origen
		corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS")); //metodos permitidos 
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		
		//pasamos la configución de CorsConfiguration a nuestras rutas URL(endpoints)
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig); //registramos la configuración e indicamos que se aplique a todas la rutas el corsConfig
		return source;
	}
	
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		//creamos una instancia de FilterRegistrationBean y mediante constructor pasamos una instancia de CorsFilter y a este le pasamos la configuración del CORS
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE); //le damos el tipo de propiedad
		return bean; // este metodo se encarga de registar el filtro CorsFilter
	}
}
