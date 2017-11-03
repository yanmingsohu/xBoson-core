/* CatfoOD 2017年11月3日 上午9:21:06 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.j2ee.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPath {
	String value();
}
