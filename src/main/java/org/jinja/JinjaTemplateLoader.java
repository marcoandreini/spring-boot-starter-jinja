package org.jinja;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourceLoader;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ResourceLocator;

import lombok.Setter;

/**
 * @author Marco Andreini
 *
 */
public class JinjaTemplateLoader implements ResourceLocator, ServletContextAware {

	private ResourceLoader resourceLoader;
	@Setter
	private String basePath = "";
	@Setter
	private String suffix = ".html";

	@Setter
	private ServletContext servletContext;

	@PostConstruct
	public void init() {
		if (this.resourceLoader == null) {
			this.resourceLoader = new ServletContextResourceLoader(servletContext);
		}
	}

    @Override
	public String getString(String fullName, Charset encoding,
			JinjavaInterpreter interpreter) throws IOException {
    	Preconditions.checkNotNull(resourceLoader, "post construct not called");
    	Preconditions.checkNotNull(fullName);
    	Preconditions.checkNotNull(encoding);

		return Files.toString(resourceLoader
				.getResource((fullName.contains(".") ? (basePath + fullName) :
					(basePath + fullName + suffix))).getFile(), encoding);
	}
}
