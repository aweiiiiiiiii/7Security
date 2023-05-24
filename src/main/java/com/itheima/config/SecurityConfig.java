package com.itheima.config;

import com.itheima.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.sql.DataSource;
import java.security.PrivateKey;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    //设置身份认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();

        //1、内存身份认证
//        auth.inMemoryAuthentication().passwordEncoder(bCryptPasswordEncoder)
//                .withUser("awei").password(bCryptPasswordEncoder
//                .encode("123456")).roles("common")
//                .and()
//                .withUser("awei1").password(bCryptPasswordEncoder
//                        .encode("123456")).roles("common");

        //2、jdbc身份认证
//        System.out.println(bCryptPasswordEncoder.encode("123"));
//        String userSQL= "select username,password,valid from t_customer " + "where username = ?";
//        String authoritySQL = "select c.username,a.authority from t_customer c,t_authority a,"+
//                "t_customer_authority ca where ca.customer_id=c.id " +
//                "and ca.authority_id=a.id and c.username =?";
//        auth.jdbcAuthentication().dataSource(dataSource)
//                .passwordEncoder(bCryptPasswordEncoder)
//                .usersByUsernameQuery(userSQL)
//                .authoritiesByUsernameQuery(authoritySQL);


        //3、userDetailService身份认证
        auth.userDetailsService(userDetailsServiceImp).passwordEncoder(bCryptPasswordEncoder);

    }

    //权限控制
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests().antMatchers("/").permitAll()
                .antMatchers("/userLogin").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/detail/common/**").hasAnyRole("common","vip")
                .antMatchers("/detail/vip/**").hasAnyRole("vip")
                .anyRequest().authenticated();
        //登录
        http.formLogin().loginPage("/userLogin")
                .usernameParameter("username").passwordParameter("pwd")
                .defaultSuccessUrl("/").failureUrl("/userLogin?error");
        //注销
        http.logout().logoutUrl("/mylogout").logoutSuccessUrl("/userLogin");

        //记住我功能
        http.rememberMe().rememberMeParameter("remember-me").tokenValiditySeconds(60)
            .tokenRepository(tokenRepository());

        //关闭csrf防护功能
        http.csrf().disable();
        }
    @Bean
    public JdbcTokenRepositoryImpl tokenRepository(){
        JdbcTokenRepositoryImpl jr=new JdbcTokenRepositoryImpl();
        jr.setDataSource(dataSource);
        return jr;
    }
}
