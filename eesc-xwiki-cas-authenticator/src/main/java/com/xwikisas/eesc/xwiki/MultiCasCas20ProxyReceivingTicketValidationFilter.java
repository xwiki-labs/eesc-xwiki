package com.xwikisas.eesc.xwiki;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;

public class MultiCasCas20ProxyReceivingTicketValidationFilter implements
		Filter {
	private Cas20ProxyReceivingTicketValidationFilter jasigCas20ProxyReceivingTicketValidationFilter;

	private static final String CAS_TO_REDIRECT_PARAMETER = "porteur";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.jasigCas20ProxyReceivingTicketValidationFilter = new Cas20ProxyReceivingTicketValidationFilter();
		this.jasigCas20ProxyReceivingTicketValidationFilter.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		String casId = request
				.getParameter(MultiCasCas20ProxyReceivingTicketValidationFilter.CAS_TO_REDIRECT_PARAMETER);
		if (casId == null) {
			casId = "demo";
		}
		final String proxyReceptorUrl = "https://" + casId
				+ ".monent.fr/connexion";

		this.jasigCas20ProxyReceivingTicketValidationFilter
				.setProxyReceptorUrl(proxyReceptorUrl);
		this.jasigCas20ProxyReceivingTicketValidationFilter.doFilter(
				servletRequest, servletResponse, filterChain);
	}

	@Override
	public void destroy() {
		this.jasigCas20ProxyReceivingTicketValidationFilter.destroy();
	}
}
