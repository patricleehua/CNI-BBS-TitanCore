package com.titancore.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.titancore.domain.entity.*;
import com.titancore.domain.mapper.*;
import com.titancore.domain.param.PageResult;
import com.titancore.domain.param.PostSearchParam;
import com.titancore.domain.vo.PostMediaUrlVo;
import com.titancore.domain.vo.PostViewVo;
import com.titancore.domain.vo.UserVo;
import com.titancore.enums.LinkType;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ElasticSearch8Service {

    @Resource
    private ElasticsearchClient client;

    @Resource
    private PostsMapper postsMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PostContentMapper postContentMapper;
    @Resource
    private PostMediaUrlMapper postMediaUrlMapper;
    @Resource
    private PostCommentsMapper postCommentsMapper;

    /**
     * 获取版本号
     * @return
     */
    @SneakyThrows
    public String getEsVersion() {
        return client.info().version().number();
    }

    /**
     * 根据String流的json数据创建索引库
     * @param indexName
     * @param mappings
     * @return
     */
    @SneakyThrows
    public boolean createIndexByReader(String indexName, Reader mappings) {
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(indexName)
                .withJson(mappings)
                .build();

        CreateIndexResponse response = client.indices().create(request);
        return response.acknowledged();
    }

    /**
     * 根据lambda构造索引库
     * @param indexName
     * @return
     */
    @SneakyThrows
    public boolean createIndexByLambda(String indexName) {
        // 1. 构建索引请求
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(indexName)
                .mappings(mappings -> mappings
                        .properties("id",p -> p.keyword(k ->k))
                        .properties("title", p -> p.text(t -> t.analyzer("ik_max_word")))
                        .properties("summary", p -> p.text(a -> a.analyzer("ik_smart")))
                        .properties("content", p -> p.text(a -> a.analyzer("ik_smart")))
                        .properties("summary", p -> p.text(a -> a.analyzer("ik_smart")))
                        .properties("authorName", p -> p.text(a -> a.analyzer("ik_max_word")))
                        .properties("authorId", p -> p.long_(i -> i.index(false)))
                        .properties("categoryId", p -> p.keyword(k -> k))
                        .properties("tagsId", p -> p.long_(l -> l))
                        .properties("type", p -> p.integer(i -> i))
                        .properties("isTop", p -> p.integer(i -> i))
                        .properties("viewCounts", p -> p.integer(i -> i))
                        .properties("commentCounts", p -> p.integer(i -> i))
                        .properties("coverUrl", p -> p.keyword(k -> k.index(false)))
                        .properties("createTime", p -> p.date(d -> d.format("epoch_millis")))
                        .properties("updateTime", p -> p.date(d -> d.format("epoch_millis")))
                        .properties("lastCommentTime", p -> p.date(d -> d.format("epoch_millis")))
                )
                .build();
        // 2. 执行索引创建请求
        CreateIndexResponse response = client.indices().create(request);
        // 3. 输出响应
        return response.acknowledged();
    }

    /**
     * 根据索引库名删除索引库
     * @param indexName
     * @return
     */
    @SneakyThrows
    public boolean deleteIndex(String indexName) {
        return client.indices().delete(d -> d.index(indexName)).acknowledged();
    }

    /**
     * 检查索引库是否存在
     * @param indexName
     * @return
     */
    @SneakyThrows
    public boolean checkIndexExists(String indexName) {
        return client.indices().exists(e -> e.index(indexName)).value();
    }

    /**
     * 统计索引库中的文档数量
     * @param indexName
     * @return
     */
    @SneakyThrows
    public long countIndexData(String indexName) {
        CountRequest countRequest = new CountRequest.Builder()
                .index(indexName)
                .query(q -> q.matchAll(MatchAllQuery.of(m -> m)))
                .build();

        CountResponse countResponse = client.count(countRequest);
        return countResponse.count();
    }

    /**
     * 批量插入post文档
     * @param indexName
     */
    @SneakyThrows
    public void postsBulkDoc(String indexName){

        try{
            int page = 1, pageSize = 20;
            while (true){
                Page<Posts> postsPage = postsMapper.selectPage(new Page<>(page, pageSize),
                        new LambdaQueryWrapper<Posts>().eq(Posts::getIsPublish, "0"));
                List<Posts> posts = postsPage.getRecords();
                if (posts == null || posts.isEmpty()) {
                    return;
                }

                List<PostDoc> postDocs = posts.stream().map(this::postToPostDoc).toList();

                BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
                for (PostDoc postDoc : postDocs) {
                    bulkRequest.operations(op -> op.index(idx -> idx.index(indexName).document(postDoc)));
                }
                client.bulk(bulkRequest.build());
                page++;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

    }
    @SneakyThrows
    public void insertPostDoc(String indexName,String postId){

        try {
            Posts posts = postsMapper.selectOne(new LambdaQueryWrapper<Posts>().eq(Posts::getIsPublish, "0").eq(Posts::getId, postId));
            PostDoc postDoc = postToPostDoc(posts);
            IndexRequest<PostDoc> request = new IndexRequest.Builder<PostDoc>()
                    .index(indexName)
                    .id(postId)
                    .document(postDoc)
                    .build();
            IndexResponse response = client.index(request);
        }catch (Exception e){
            log.error(e.toString());
        }
    }
    @SneakyThrows
    public void deletePostDoc(String indexName,String postId){
        DeleteRequest request = new DeleteRequest.Builder()
                .index(indexName).id(postId).build();
        client.delete(request);
    }
    /**
     * 全文查询帖子
     * @param postSearchParam
     * @return
     */
    @SneakyThrows
    public PageResult searchPostPage(PostSearchParam postSearchParam) {
        String CNI_POST_INDEX = "cni-post";
        try {

            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

            if (!postSearchParam.getKeyword().isEmpty()){
                MultiMatchQuery.Builder multiMatchQuery = new MultiMatchQuery.Builder()
                        .query(postSearchParam.getKeyword())
                        .fields("title", "summary", "content")
                        .type(TextQueryType.BestFields);

                // 算分函数查询1
                FunctionScoreQuery.Builder functionScoreQuery = new FunctionScoreQuery.Builder()
                        .query(multiMatchQuery.build()._toQuery())
                        .functions(f -> f
                                .filter(fr -> fr.match(MatchQuery.of(m -> m.query(postSearchParam.getKeyword()).field("title"))))
                                .weight(10.0))
                        .functions(f -> f
                                .filter(fr -> fr.match(MatchQuery.of(m -> m.query(postSearchParam.getKeyword()).field("summary"))))
                                .weight(4.00))
                        .functions(f -> f
                                .filter(fr -> fr.match(MatchQuery.of(m -> m.query(postSearchParam.getKeyword()).field("content"))))
                                .weight(2.00))
                        .boostMode(FunctionBoostMode.Multiply);

                boolQueryBuilder.must(functionScoreQuery.build()._toQuery());
            }else{
                boolQueryBuilder.must(MatchAllQuery.of(ma -> ma)._toQuery());
            }
            if (postSearchParam.getAuthorName()!=null){
                boolQueryBuilder.must(ms ->
                        ms.match(MatchQuery.of(m ->
                                m.query(postSearchParam.getAuthorName())
                                        .field("authorName"))));
            }
            //算分函数2 处理上文查询后的结果
            FunctionScoreQuery.Builder scoreQuery = new FunctionScoreQuery.Builder()
                    .functions(f -> f.filter(
                                    fr -> fr.term(t -> t.field("isTop").value(0)))
                            .weight(10.0)
                    )
                    .boostMode(FunctionBoostMode.Multiply);

            boolQueryBuilder.should(scoreQuery.build()._toQuery());

            //条件过滤
            if (postSearchParam.getCategoryId()!=null){
                boolQueryBuilder.filter(TermQuery.of(t -> t
                        .field("categoryId").value(postSearchParam.getCategoryId()))
                        ._toQuery());
            }
            if (postSearchParam.getTagList() != null){
                //todo 多个标签
                boolQueryBuilder.filter(TermQuery.of(t -> t
                        .field("tagsId").value(postSearchParam.getKeyword()))
                        ._toQuery());
            }
            if(postSearchParam.getBeforeTime() != null){

                long beforeTime = postSearchParam.getBeforeTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
                boolQueryBuilder.filter(RangeQuery.of(r -> r
                        .date(d -> d
                                .field("createTime")
                                .lte(String.valueOf(beforeTime))
                        )
                )._toQuery());
            }
            if(postSearchParam.getAfterTime() != null ){
                long afterTime = postSearchParam.getAfterTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
                boolQueryBuilder.filter(RangeQuery.of(r -> r
                        .date(d -> d
                                .field("createTime")
                                .gte(String.valueOf(afterTime))
                        )
                )._toQuery());
            }
            int from  = postSearchParam.getPageNo() > 0 ? (postSearchParam.getPageNo() - 1) * postSearchParam.getPageSize() : 0;
            String sortBy = postSearchParam.getSortBy() == null || postSearchParam.getSortBy().isEmpty() ? "createTime" : postSearchParam.getSortBy();
            // 构建查询请求 todo 高亮处理
            SearchRequest request = new SearchRequest.Builder()
                    .index(CNI_POST_INDEX)
                    .query(boolQueryBuilder.build()._toQuery())//符合查询
                    .from(from)//分页
                    .size(postSearchParam.getPageSize())//分页
                    .sort(s -> s.field(f -> f.field(sortBy)
                            .order(postSearchParam.getIsAsc() ? SortOrder.Asc : SortOrder.Desc)))//排序
                    .build();
            SearchResponse<PostDoc> response = client.search(request, PostDoc.class);
            return processTheResponse(response);
        }catch (Exception e){
            log.error(e.toString());
        }
        return new PageResult(0,null);
    }
    private PostDoc postToPostDoc(Posts post) {
        PostDoc postDoc = new PostDoc();
        BeanUtils.copyProperties(post, postDoc);
        //post
        postDoc.setId(String.valueOf(post.getId()));
        postDoc.setCreateTime(post.getCreateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        postDoc.setUpdateTime(post.getUpdateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        //authorName
        String authorName= Optional.ofNullable(userMapper.selectById(post.getAuthorId()))
                .map(User::getUserName)
                .orElse("未知作者");
        postDoc.setAuthorName(authorName);
        postDoc.setAuthorId(post.getAuthorId());
        //content
        PostContent postContent = postContentMapper.selectById(post.getContentId());
        if (postContent != null) {
            postDoc.setContent(postContent.getContent());
        } else {
            postDoc.setContent("无内容");
        }

        // tags
        List<Long> tagsId = tagMapper.queryTagListByPostId(post.getId()).stream().map(Tag::getId).toList();
        if (!tagsId.isEmpty()) {
            postDoc.setTagsId(tagsId);
        } else {
            postDoc.setTagsId(new ArrayList<>());
        }

        //coverUrl
        PostMediaUrl postMediaUrl = postMediaUrlMapper.selectOne(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, post.getId())
                .eq(PostMediaUrl::getMediaType, LinkType.COVER.getValue()).last("limit 1"));
        if (postMediaUrl != null && postMediaUrl.getMediaUrl() != null) {
            postDoc.setCoverUrl(postMediaUrl.getMediaUrl());
        } else {
            postDoc.setCoverUrl("https://www.nradiowifi.com/d/file/2024/9a2c7e7a80515b0a8e5d02da37ace98c.jpg");
        }
        //comment
        PostComments postComments = postCommentsMapper.selectOne(new LambdaQueryWrapper<PostComments>()
                .eq(PostComments::getPostId, post.getId()).orderByDesc(PostComments::getCommentTime).last("limit 1"));
        if (postComments != null) {
            postDoc.setLastCommentTime(postComments.getCommentTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        }else{
            postDoc.setLastCommentTime(0L);
        }

        return postDoc;
    }

    private PageResult processTheResponse(SearchResponse<PostDoc> response) {
        List<Hit<PostDoc>> hits = response.hits().hits();
        //total
        long total = 0;
        if (!hits.isEmpty()) {
            total =  response.hits().total().value();
        }
        List<PostDoc> postDocs = new ArrayList<>();

        for (Hit<PostDoc> hit : hits) {
            PostDoc postDoc = hit.source();
            Map<String, List<String>> highlight = hit.highlight();
            if (highlight != null && !highlight.isEmpty() && postDoc != null){
                // 高亮处理逻辑
                // 处理 title 字段的高亮
                List<String> titleField = highlight.get("title");
                if (titleField != null && !titleField.isEmpty()) {
                    postDoc.setTitle(titleField.get(0));
                }

                // 处理 summary 字段的高亮
                List<String> summaryField = highlight.get("summary");
                if (summaryField != null && !summaryField.isEmpty()) {
                    postDoc.setSummary(summaryField.get(0));
                }

                // 处理 content 字段的高亮
                List<String> contentField = highlight.get("content");
                if (contentField != null && !contentField.isEmpty()) {
                    postDoc.setContent(contentField.get(0));
                }

                // 处理 authorName 字段的高亮
                List<String> authorNameField = highlight.get("authorName");
                if (authorNameField != null && !authorNameField.isEmpty()) {
                    postDoc.setAuthorName(authorNameField.get(0));
                }
            }
            postDocs.add(postDoc);
        }

        //postDoc to postViewVo
        List<PostViewVo> postViewVoList = new ArrayList<>();
        for (PostDoc postDoc : postDocs) {
            PostViewVo postViewVo = new PostViewVo();
            BeanUtils.copyProperties(postDoc,postViewVo);
            postViewVo.setPostId(postDoc.getId());

            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, postDoc.getAuthorId()));
            UserVo userVo = new UserVo();
            userVo.setUserId(String.valueOf(user.getUserId()));
            userVo.setUserName(user.getUserName());
            userVo.setAvatar(user.getAvatar());
            userVo.setBio(user.getBio());
            postViewVo.setUserVo(userVo);

            LocalDateTime createTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(postDoc.getCreateTime()), ZoneId.of("UTC"));
            postViewVo.setCreateTime(createTime);
            LocalDateTime updateTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(postDoc.getUpdateTime()), ZoneId.of("UTC"));
            postViewVo.setUpdateTime(updateTime);
            LocalDateTime commentTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(postDoc.getLastCommentTime()), ZoneId.of("UTC"));
            postViewVo.setCommentTime(commentTime);

            postViewVo.setIsTop(String.valueOf(postDoc.getIsTop()));
            postViewVo.setType(String.valueOf(postDoc.getType()));
            //todo categoryVo tagVos 优化
            if (postDoc.getCoverUrl() != null){
                PostMediaUrlVo postMediaUrlVo = new PostMediaUrlVo();
                postMediaUrlVo.setMediaUrl(postDoc.getCoverUrl());
                postMediaUrlVo.setMediaType("cover");
                List<PostMediaUrlVo> postMediaUrlVos = new ArrayList<>();
                postViewVo.setUrls(postMediaUrlVos);
            }
            postViewVoList.add(postViewVo);
        }
        return new PageResult(total,postViewVoList);
    }

    public static final Reader CNI_POST_MAPPINGS = new StringReader(
            """
                    {
                      "mappings": {
                        "properties": {
                          "id":{
                            "type": "keyword"
                          },
                          "title":{
                            "type": "text",
                            "analyzer": "ik_max_word"
                          },
                          "summary":{
                            "type": "text",
                            "analyzer": "ik_smart"
                          },
                          "content":{
                            "type": "text",
                            "analyzer": "ik_smart"
                          },
                          "authorId":{
                            "type": "long",
                            "index": false
                          },
                          "authorName":{
                            "type": "text",
                            "analyzer": "ik_max_word"
                          },
                          "categoryId":{
                            "type": "keyword"
                          },
                          "tagsId":{
                            "type": "long"
                          },
                          "type":{
                            "type": "integer"
                          },
                          "isTop":{
                            "type": "integer"
                          },
                          "viewCounts":{
                            "type": "integer"
                          },
                          "commentCounts":{
                            "type": "integer"
                          },
                          "coverUrl":{
                            "type": "keyword",
                            "index": false
                          },
                          "createTime":{
                            "type": "date",
                            "format": "epoch_millis"
                          },
                          "updateTime":{
                            "type": "date",
                            "format": "epoch_millis"
                          },
                          "lastCommentTime":{
                            "type": "date",
                            "format": "epoch_millis"
                          }
                        }
                      }
                    }
                    """
    );
}
