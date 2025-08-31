package com.stockify.project.configuration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@AllArgsConstructor
public class GridFSConfig {

    private final MongoTemplate mongoTemplate;

    @Bean
    public GridFSBucket gridFSBucket() {
        MongoDatabase database = mongoTemplate.getDb();
        return GridFSBuckets.create(database);
    }
}
