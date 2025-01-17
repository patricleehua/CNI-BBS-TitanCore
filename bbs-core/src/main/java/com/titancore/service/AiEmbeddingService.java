package com.titancore.service;

import com.titancore.factory.VectorStoreFactory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiEmbeddingService {

    @Resource
    private VectorStoreFactory vectorStoreFactory;


}
