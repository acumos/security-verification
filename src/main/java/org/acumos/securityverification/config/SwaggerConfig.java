/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.securityverification.config;

import java.util.ArrayList;

import org.acumos.securityverification.SecurityVerificationApplication;
import org.acumos.securityverification.controller.AbstractController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 * 
 * @author Chris Lott
 * @author Aimee Ukasick
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * @return new Docket
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(AbstractController.class.getPackage().getName())) 
				.paths(PathSelectors.any()) 
				.build() 
				.apiInfo(apiInfo());
	}

	@SuppressWarnings("rawtypes")
	private ApiInfo apiInfo() {
		final String version = SecurityVerificationApplication.class.getPackage().getImplementationVersion();
		Contact contact = new Contact("Security Verification Team", "http://docs.acumos.org",
				"acumosaidevdiscuss@lists.acumos.org");
		String title = "Acumos Security Verification Service REST API";
		String desc = "Provides functionality to scan uploaded model artifacts for license and vulnerability issues";
		String versionInfo = version == null ? "version not available" : version;
		return new ApiInfo(title
				, desc
				, versionInfo
				, "Terms of Service"
				, contact
				, "Apache 2.0",
				"https://www.apache.org/licenses/LICENSE-2.0",
				new ArrayList<VendorExtension>());
	}
}