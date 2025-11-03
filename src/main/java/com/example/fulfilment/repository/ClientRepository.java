package com.example.fulfilment.repository;

import com.example.fulfilment.entity.Client;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {
  Optional<Client> findByApiKey(String apiKey);
}
