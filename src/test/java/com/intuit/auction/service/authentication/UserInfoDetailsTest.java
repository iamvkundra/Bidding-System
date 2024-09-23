package com.intuit.auction.service.authentication;

import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.entity.account.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class UserInfoDetailsTest {

    private Account customerAccount;
    private Account vendorAccount;
    private UserInfoDetails customerDetails;
    private UserInfoDetails vendorDetails;

    @BeforeEach
    void setUp() {
        customerAccount = Mockito.mock(Account.class);
        vendorAccount = Mockito.mock(Vendor.class);

        Mockito.when(customerAccount.getUsername()).thenReturn("customer1");
        Mockito.when(customerAccount.getPassword()).thenReturn("password123");

        Mockito.when(vendorAccount.getUsername()).thenReturn("vendor1");
        Mockito.when(vendorAccount.getPassword()).thenReturn("password123");

        customerDetails = new UserInfoDetails(customerAccount);
        vendorDetails = new UserInfoDetails(vendorAccount);
    }

    @Test
    void constructor_shouldSetUsernameAndPassword() {
        assertThat(customerDetails.getUsername()).isEqualTo("customer1");
        assertThat(customerDetails.getPassword()).isEqualTo("password123");

        assertThat(vendorDetails.getUsername()).isEqualTo("vendor1");
        assertThat(vendorDetails.getPassword()).isEqualTo("password123");
    }

    @Test
    void getAuthorities_shouldReturnCustomerAuthorityForCustomer() {
        assertThat(customerDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("CUSTOMER");
    }

    @Test
    void getAuthorities_shouldReturnVendorAuthorityForVendor() {
        assertThat(vendorDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("VENDOR");
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        assertThat(customerDetails.isAccountNonExpired()).isTrue();
        assertThat(vendorDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        assertThat(customerDetails.isAccountNonLocked()).isTrue();
        assertThat(vendorDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertThat(customerDetails.isCredentialsNonExpired()).isTrue();
        assertThat(vendorDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        assertThat(customerDetails.isEnabled()).isTrue();
        assertThat(vendorDetails.isEnabled()).isTrue();
    }
}
