package org.jinja;

import java.nio.charset.Charset;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.google.common.base.Charsets;
import com.hubspot.jinjava.Jinjava;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Marco Andreini
 *
 */
public class JinjaViewResolver extends AbstractTemplateViewResolver {

	@Setter
	private Jinjava engine;
	@Setter
	private Charset charset = Charsets.UTF_8;
	@Setter
	private boolean renderExceptions = false;
	@Getter @Setter
	private String contentType = "text/html;charset=UTF-8";

	public JinjaViewResolver() {
		setViewClass(requiredViewClass());
	}

	@Override
	protected Class<?> requiredViewClass() {
		return JinjaView.class;
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		JinjaView view = (JinjaView) super.buildView(viewName);
		view.setEngine(this.engine);
		view.setContentType(contentType);
		view.setRenderExceptions(renderExceptions);
		view.setEncoding(charset);
		return view;
	}

}
