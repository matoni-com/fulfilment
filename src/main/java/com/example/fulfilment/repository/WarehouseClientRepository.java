package com.example.fulfilment.repository;

import com.example.fulfilment.entity.WarehouseClient;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface WarehouseClientRepository extends CrudRepository<WarehouseClient, Long> {
  Optional<WarehouseClient> findByApiKey(String apiKey);
}
