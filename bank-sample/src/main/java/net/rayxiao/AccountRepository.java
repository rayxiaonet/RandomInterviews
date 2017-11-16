package net.rayxiao;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by rxiao on 10/31/16.
 */
public interface AccountRepository extends MongoRepository<Account, String> {

    public List<Account> findByName(String name);

}
