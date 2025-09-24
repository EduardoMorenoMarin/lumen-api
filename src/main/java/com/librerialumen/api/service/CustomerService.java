package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.customer.CustomerCreateDTO;
import com.librerialumen.api.web.dto.customer.CustomerUpdateDTO;
import com.librerialumen.api.web.dto.customer.CustomerViewDTO;
import java.util.List;
import java.util.UUID;

public interface CustomerService {

  CustomerViewDTO create(CustomerCreateDTO dto);

  CustomerViewDTO update(UUID customerId, CustomerUpdateDTO dto);

  CustomerViewDTO patch(UUID customerId, CustomerUpdateDTO dto);

  void delete(UUID customerId);

  CustomerViewDTO get(UUID customerId);

  List<CustomerViewDTO> list();
}
