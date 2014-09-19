package com.xwikisas.eesc.xwiki;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.authentication.AuthenticationFilter;

public class MultiCasAuthenticationFilter implements Filter {
	private AuthenticationFilter jasigAuthenticationFilter;

	private static final String CAS_TO_REDIRECT_PARAMETER = "porteur";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.jasigAuthenticationFilter = new AuthenticationFilter();
		this.jasigAuthenticationFilter.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		String casId = request
				.getParameter(MultiCasAuthenticationFilter.CAS_TO_REDIRECT_PARAMETER);
		if (casId == null) {
			casId = "demo";
		}
		final String casServerLoginUrl = "https://" + casId
				+ ".monent.fr/connexion";

		this.jasigAuthenticationFilter.setCasServerLoginUrl(casServerLoginUrl);
		this.jasigAuthenticationFilter.doFilter(servletRequest,
				servletResponse, filterChain);
	}

	@Override
	public void destroy() {
		this.jasigAuthenticationFilter.destroy();
	}
}
