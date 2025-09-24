package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.CustomerMapper;
import com.librerialumen.api.repository.CustomerRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.CustomerService;
import com.librerialumen.api.web.dto.customer.CustomerCreateDTO;
import com.librerialumen.api.web.dto.customer.CustomerUpdateDTO;
import com.librerialumen.api.web.dto.customer.CustomerViewDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;
  private final AuditService auditService;

  @Override
  public CustomerViewDTO create(CustomerCreateDTO dto) {
    Customer customer = customerMapper.toEntity(dto);
    Customer saved = customerRepository.save(customer);
    auditService.record("Customer", saved.getId().toString(), "CREATE", null,
        buildAuditDetails(saved));
    return customerMapper.toView(saved);
  }

  @Override
  public CustomerViewDTO update(UUID customerId, CustomerUpdateDTO dto) {
    Customer customer = loadCustomer(customerId);
    applyUpdates(customer, dto);
    return saveAndAudit(customer, "UPDATE");
  }

  @Override
  public CustomerViewDTO patch(UUID customerId, CustomerUpdateDTO dto) {
    Customer customer = loadCustomer(customerId);
    applyUpdates(customer, dto);
    return saveAndAudit(customer, "PATCH");
  }

  @Override
  public void delete(UUID customerId) {
    Customer customer = loadCustomer(customerId);
    try {
      customerRepository.delete(customer);
      customerRepository.flush();
    } catch (DataIntegrityViolationException ex) {
      throw new BusinessException("CUSTOMER_DELETE_CONSTRAINT",
          "Unable to delete customer because it is referenced by other records.", ex);
    }
    auditService.record("Customer", customer.getId().toString(), "DELETE", null,
        buildAuditDetails(customer));
  }

  @Override
  @Transactional(readOnly = true)
  public CustomerViewDTO get(UUID customerId) {
    return customerMapper.toView(loadCustomer(customerId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CustomerViewDTO> list() {
    return customerMapper.toViewList(customerRepository.findAll());
  }

  private Customer loadCustomer(UUID customerId) {
    return customerRepository.findById(customerId)
        .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Customer not found"));
  }

  private void applyUpdates(Customer customer, CustomerUpdateDTO dto) {
    customerMapper.updateEntity(dto, customer);
  }

  private CustomerViewDTO saveAndAudit(Customer customer, String action) {
    Customer saved = customerRepository.save(customer);
    auditService.record("Customer", saved.getId().toString(), action, null,
        buildAuditDetails(saved));
    return customerMapper.toView(saved);
  }

  private Map<String, Object> buildAuditDetails(Customer customer) {
    Map<String, Object> details = new HashMap<>();
    if (customer.getDni() != null) {
      details.put("dni", customer.getDni());
    }
    if (customer.getEmail() != null) {
      details.put("email", customer.getEmail());
    }
    if (customer.getFirstName() != null) {
      details.put("firstName", customer.getFirstName());
    }
    if (customer.getLastName() != null) {
      details.put("lastName", customer.getLastName());
    }
    if (customer.getPhone() != null) {
      details.put("phone", customer.getPhone());
    }
    return details;
  }
}
