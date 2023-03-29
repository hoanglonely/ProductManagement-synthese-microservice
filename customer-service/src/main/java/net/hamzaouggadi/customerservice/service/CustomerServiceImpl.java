package net.hamzaouggadi.customerservice.service;

import java.security.Principal;
import java.util.List;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import net.hamzaouggadi.customerservice.entities.Customer;
import net.hamzaouggadi.customerservice.exceptions.CustomerException;
import net.hamzaouggadi.customerservice.repositories.CustomerRepository;

@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	private CustomerRepository customerRepository;

	@Override
	public List<Customer> listCustomers(Principal principal) {
		KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;

		System.out.println(keycloakAuthenticationToken.getName());

		AccessToken accessToken = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken();

		System.out.println(keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getTokenString());
		
		System.out.println(accessToken.getPreferredUsername());
		System.out.println(accessToken.getEmail());
		System.out.println(accessToken.getFamilyName());
		System.out.println(accessToken.getGivenName());
		System.out.println(accessToken.getIssuer());
		System.out.println(accessToken.getId());
		System.out.println(accessToken.getSubject());

		Access realmAccess = accessToken.getRealmAccess();
		System.out.println(realmAccess.getRoles());

		return customerRepository.findAll();
	}

	@Override
	public Customer getCustomerById(Long customerId) throws CustomerException {
		return customerRepository.findById(customerId).orElseThrow(() -> new CustomerException("Customer Not Found!"));
	}

	@Override
	public Customer addCustomer(Customer customer) throws CustomerException {
		if (customerRepository.findCustomerByCustomerEmail(customer.getCustomerEmail()) != null) {
			throw new CustomerException("Customer Already Exists!");
		} else {
			Customer newCustomer = Customer.builder().customerName(customer.getCustomerName())
					.customerEmail(customer.getCustomerEmail()).build();
			return customerRepository.save(newCustomer);
		}
	}

	@Override
	public void deleteCustomerById(Long customerID) throws CustomerException {
		if (customerRepository.findById(customerID).isPresent()) {
			customerRepository.deleteById(customerID);
		} else {
			throw new CustomerException("Customer Not Found!");
		}
	}
}
