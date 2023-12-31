package com.support.supportPortal.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UserPrincipal implements UserDetails {
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        /*
        * List<SimpleGrantedAuthority> authoritiesList = new ArrayList<SimpleGrantedAuthority>();
List<Authority> authorities = this.user.getAuthorities(); // Assuming Authority is the type of the authorities.

for (Authority authority : authorities) {
    SimpleGrantedAuthority simpleAuthority = new SimpleGrantedAuthority(authority.getAuthorityName()); // Assuming authorityName is the relevant property.
    authoritiesList.add(simpleAuthority);
}

return authoritiesList;
        * */
        return stream(this.user.getAuthorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }
}
