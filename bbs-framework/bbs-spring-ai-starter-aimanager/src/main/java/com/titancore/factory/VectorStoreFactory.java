package com.titancore.factory;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.SimilarityFunction;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VectorStoreFactory {

    @Value("${elasticsearch.serverUrl}")
    private String elasticsearchUri;

    private final EmbeddingModel embeddingModel;

    public VectorStoreFactory(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    private RestClient getRestClient() {
        return RestClient.builder(HttpHost.create(elasticsearchUri)).build();
    }
    public VectorStore createElasticsearchVectorStore(String indexName) {
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        options.setIndexName(indexName);
        options.setDimensions(1536);
        options.setSimilarity(SimilarityFunction.valueOf("cosine"));

        return new ElasticsearchVectorStore(
                options,
                getRestClient(),
                embeddingModel,
                false
        );
    }
}
