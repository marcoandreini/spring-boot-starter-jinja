package org.jinja;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import com.google.common.base.Charsets;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.FatalTemplateErrorsException;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Marco Andreini
 *
 */
public class JinjaView extends AbstractTemplateView {

	@Getter @Setter
	private Charset encoding = Charsets.UTF_8;
	@Getter @Setter
	private Jinjava engine;
	@Getter @Setter
	private boolean renderExceptions = false;
	@Getter @Setter
	private String contentType;

	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
					throws Exception {

		doRender(model, response);
	}

	private void doRender(Map<String, Object> model,
			HttpServletResponse response) throws IOException {
		logger.trace("Rendering Jinja template [" + getUrl() + "] in JinjaView '"
			+ getBeanName() + "'");

		if (contentType != null) {
			response.setContentType(contentType);
		}

		PrintWriter responseWriter = response.getWriter();

		if (renderExceptions) {
			try {
				responseWriter.write(engine.render(getTemplate(), model));
			} catch (FatalTemplateErrorsException e) {
				// TODO: render exception
				responseWriter.write(e.getLocalizedMessage());
				logger.error("failed to render template [" + getUrl() + "]", e);
			} catch (IOException e) {
				responseWriter.write("<pre>could not find template: " + getUrl() + "\n");
				e.printStackTrace(responseWriter);
				responseWriter.write("</pre>");
				logger.error("could not find template", e);
			}
		} else {
			try {
				responseWriter.write(engine.render(getTemplate(), model));
			} catch (Throwable e) {
				logger.error("failed to render template [" + getUrl() + "]\n", e);
			}
		}
	}

	protected String getTemplate() throws IOException {
		// XXX: interpreter could be null...
		return engine.getResourceLocator().getString(getUrl(), encoding,
				null);
	}

	@Override
	public boolean checkResource(Locale locale) throws Exception {
		try {
			// XXX: interpreter could be null...
			engine.getResourceLocator().getString(getUrl(), encoding, null);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
