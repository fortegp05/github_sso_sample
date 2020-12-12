package com.example.github_sso_sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@SpringBootApplication
@Controller
@EnableWebSecurity
class GithubSsoSampleApplication : WebSecurityConfigurerAdapter() {

	@GetMapping("/user")
	fun user(@AuthenticationPrincipal principal: OAuth2User, model: Model): String {
		val name = principal.attributes.get("name") ?: "not exists"
		model.addAttribute("user", name)
		return "user"
	}

	override fun configure(http: HttpSecurity) {
		http.authorizeRequests { a ->
			a.antMatchers("/", "/error").permitAll()
					.anyRequest().authenticated()
		}.logout { l ->
			l.logoutUrl("/logout").logoutSuccessUrl("/").permitAll()
		}.exceptionHandling { e ->
			e.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
		}.oauth2Login{it ->
			it.defaultSuccessUrl("/user")
		}
	}
}

fun main(args: Array<String>) {
	runApplication<GithubSsoSampleApplication>(*args)
}


