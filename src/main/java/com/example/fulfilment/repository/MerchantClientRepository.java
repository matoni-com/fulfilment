package com.example.fulfilment.repository;

import com.example.fulfilment.entity.MerchantClient;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface MerchantClientRepository extends CrudRepository<MerchantClient, Long> {
  Optional<MerchantClient> findByApiKey(String apiKey);
}
