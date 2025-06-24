package com.springBatchSanjay.writer;

import com.springBatchSanjay.entity.User;
import com.springBatchSanjay.repository.UserRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomItemWriter implements ItemWriter<User> {
    @Autowired
    private UserRepository userRepository;


    @Override
    public void write(Chunk<? extends User> chunk) throws Exception {
        userRepository.saveAll(chunk.getItems());

    }
}
