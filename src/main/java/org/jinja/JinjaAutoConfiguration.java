package org.jinja;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import com.hubspot.jinjava.Jinjava;

/**
 * @author Marco Andreini
 *
 */
@Configuration
@ConditionalOnClass(JinjaTemplateLoader.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class JinjaAutoConfiguration {

	public static final String DEFAULT_PREFIX = "classpath:/templates/";
	public static final String DEFAULT_SUFFIX = ".html";

	@Configuration
	@ConditionalOnMissingBean(name = "defaultSpringTemplateLoader")
	public static class DefaultTemplateResolverConfiguration implements EnvironmentAware {

		@Autowired
		private final ResourceLoader resourceLoader = new DefaultResourceLoader();

		private RelaxedPropertyResolver environment;

		@Override
		public void setEnvironment(Environment environment) {
			this.environment = new RelaxedPropertyResolver(environment, "spring.jinja.");
		}

		@PostConstruct
		public void checkTemplateLocationExists() {
			Boolean checkTemplateLocation = this.environment
					.getProperty("checkTemplateLocation", Boolean.class, true);
			if (checkTemplateLocation) {
				Resource resource = this.resourceLoader
						.getResource(this.environment.getProperty("prefix", DEFAULT_PREFIX));
				Assert.state(resource.exists(), "Cannot find template location: "
						+ resource +
						" (please add some templates or check your jinjava configuration)");
			}
		}

		@Bean
		public JinjaTemplateLoader defaultSpringTemplateLoader() {
			JinjaTemplateLoader resolver = new JinjaTemplateLoader();

			resolver.setBasePath(this.environment.getProperty("prefix", DEFAULT_PREFIX));
			resolver.setSuffix(this.environment.getProperty("suffix", DEFAULT_SUFFIX));
			return resolver;
		}

		@Bean
		public Jinjava jinja(JinjaTemplateLoader loader) {
			// TODO: aggiungere la jinjavaconfig
			Jinjava engine = new Jinjava();
			engine.setResourceLocator(loader);
			return engine;
		}

	}


	@Configuration
	@ConditionalOnClass({Servlet.class})
	@ConditionalOnWebApplication
	protected static class JinjavaViewResolverConfiguration implements EnvironmentAware {

		private RelaxedPropertyResolver environment;

		@Autowired
		private Jinjava engine;

		@Override
		public void setEnvironment(Environment environment) {
			this.environment = new RelaxedPropertyResolver(environment, "spring.jinja.");
		}

		@Bean
		@ConditionalOnMissingBean(name = "jinjaViewResolver")
		public JinjaViewResolver jinjaViewResolver() {
			final Charset encoding = Charset.forName(this.environment
					.getProperty("encoding", "UTF-8"));
			final JinjaViewResolver resolver = new JinjaViewResolver();
			resolver.setCharset(encoding);
			resolver.setEngine(engine);

			resolver.setContentType(appendCharset(
					this.environment.getProperty("contentType", "text/html"),
					encoding.name()));

			resolver.setViewNames(this.environment.getProperty("viewNames", String[].class));
			// This resolver acts as a fallback resolver (e.g. like a
			// InternalResourceViewResolver) so it needs to have low precedence
			resolver.setOrder(this.environment.getProperty("resolver.order",
					Integer.class, Ordered.LOWEST_PRECEDENCE - 50));
			return resolver;
		}

		@Bean
		public BeanPostProcessor jinjaBeanPostProcessor() {
			return new BeanPostProcessor() {

				@Override
				public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
					return bean;
				}

				@Override
				public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
					JinjaHelper annotation = AnnotationUtils.findAnnotation(bean.getClass(), JinjaHelper.class);
					if (annotation != null) {
						engine.getGlobalContext().put(beanName, bean);
					}
					return bean;
				}
			};
		}


		private String appendCharset(String type, String charset) {
			if (type.contains("charset=")) {
				return type;
			}
			return type + ";charset=" + charset;
		}
	}
}