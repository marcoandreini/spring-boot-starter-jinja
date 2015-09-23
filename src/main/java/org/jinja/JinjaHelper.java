package org.jinja;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Indicates that an annotated class is a "Jinjava Helper"
 * <p/>
 * <p>This annotation serves as a specialization of
 * {@link Component @Component}, allowing for implementation classes to be
 * autodetected through classpath scanning.
 *
 * @author Marco Andreini
 * @see org.springframework.stereotype.Component
 * @see org.springframework.context.annotation.ClassPathBeanDefinitionScanner
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface JinjaHelper {
  /**
   * The value may indicate a suggestion for a logical component name,
   * to be turned into a Spring bean in case of an autodetected component.
   *
   * @return the suggested component name, if any
   */
  @AliasFor(annotation = Component.class, attribute = "value")
  String value() default "";
}