package com.papeleria.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class TiempoTrascurridoFilterPost extends ZuulFilter{
	
	private static final Logger log = LoggerFactory.getLogger(TiempoTrascurridoFilterPost.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();  
		HttpServletRequest request = ctx.getRequest(); //obtenemos el request por medio del objeto RequestContext
		
		Long tiempoInicial = (Long) request.getAttribute("tiempoInicial");
		Long tiempoTrascurrido = System.currentTimeMillis() - tiempoInicial;
		log.info(String.format("Entrando a POST -> tiempo transacurrido en seg %s", tiempoTrascurrido.doubleValue()/1000.00));
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
