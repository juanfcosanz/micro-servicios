package com.papeleria.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class TiempoTrascurridoFilterPre extends ZuulFilter{
	
	private static final Logger log = LoggerFactory.getLogger(TiempoTrascurridoFilterPre.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();  
		HttpServletRequest request = ctx.getRequest(); //obtenemos el request por medio del objeto RequestContext
		
		Long tiempoInicial = System.currentTimeMillis();
		request.setAttribute("tiempoInicial", tiempoInicial); //le pasamoe el tiempo inicial a la petición
		log.info(String.format("Entrando a PRE en %s, Method %s enrutado a %s", tiempoInicial, request.getMethod(), request.getRequestURL().toString()));
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
