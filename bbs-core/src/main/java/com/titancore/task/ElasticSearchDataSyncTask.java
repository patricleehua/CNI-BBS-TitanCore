package com.titancore.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.titancore.domain.entity.Posts;
import com.titancore.domain.mapper.PostsMapper;
import com.titancore.service.ElasticSearch8Service;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class ElasticSearchDataSyncTask {
    private final ElasticSearch8Service elasticSearchService;
    private final PostsMapper postsMapper;
    private final String CNI_POST_INDEX = "cni-post";

    //todo 关闭先置
//    @Scheduled(cron = "0 */15 * * * ?")
    public void pullEsSearchData() throws IOException {

        if(elasticSearchService.checkIndexExists(CNI_POST_INDEX)){
           return;
       }else {
           elasticSearchService.createIndexByReader(CNI_POST_INDEX, ElasticSearch8Service.CNI_POST_MAPPINGS);
       }
        Long postsDatabaseCount = postsMapper.selectCount(
                new LambdaQueryWrapper<Posts>().eq(Posts::getIsPublish, "0"));

        Long postsElasticSearchCount = elasticSearchService.countIndexData(CNI_POST_INDEX);
        //设定阈值
        double thresholdPercentage = 0.05;//5%的差异
        long fixedThreshold = 30L;//固定数量的差异

        //计算差异
        long difference =Math.abs(postsDatabaseCount - postsElasticSearchCount);

        //是否超出阈值
        boolean rebuildRequired = false;

        //按百分比判断
        if(difference > postsDatabaseCount * thresholdPercentage){
            rebuildRequired = true;
        }
        //按照固定数量比较
        if (difference > fixedThreshold){
            rebuildRequired = true;
        }
        if(rebuildRequired){
            //从构索引逻辑
            rebuildPostsIndex();
        }
    }
    @SneakyThrows
    private void rebuildPostsIndex() {
        elasticSearchService.deleteIndex(CNI_POST_INDEX);
        elasticSearchService.postsBulkDoc(CNI_POST_INDEX);
        log.info("同步es数据");
    }

}
