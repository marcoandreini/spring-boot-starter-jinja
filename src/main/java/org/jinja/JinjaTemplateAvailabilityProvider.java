package org.jinja;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/**
 * @author Marco Andreini
 *
 */
public class JinjaTemplateAvailabilityProvider
	implements TemplateAvailabilityProvider {

	@Override
	public boolean isTemplateAvailable(String view, Environment environment,
			ClassLoader classLoader, ResourceLoader resourceLoader) {
		if (ClassUtils.isPresent("org.jinja.JinjaTemplateLoader",
				classLoader)) {
			String prefix = environment.getProperty("spring.jinja.prefix",
					JinjaAutoConfiguration.DEFAULT_PREFIX);
			String suffix = environment.getProperty("spring.jinja.suffix",
					JinjaAutoConfiguration.DEFAULT_SUFFIX);
			return resourceLoader.getResource(prefix + view + suffix).exists();
		}

		return false;
	}
}
