//package com.titancore.service;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.titancore.domain.entity.*;
//import com.titancore.domain.mapper.*;
//import com.titancore.domain.param.PageResult;
//import com.titancore.domain.param.PostSearchParam;
//import com.titancore.domain.vo.*;
//import com.titancore.enums.LinkType;
//import jakarta.annotation.Resource;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.HttpHost;
//import org.apache.lucene.search.TotalHits;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.delete.DeleteRequest;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.support.master.AcknowledgedResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.CreateIndexRequest;
//import org.elasticsearch.client.indices.CreateIndexResponse;
//import org.elasticsearch.client.indices.GetIndexRequest;
//import org.elasticsearch.common.lucene.search.function.CombineFunction;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.MultiMatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
//import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
//import org.elasticsearch.rest.RestStatus;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//@Slf4j
//public class ElasticSearchService {
//
//    private RestHighLevelClient client;
//    @Resource
//    private PostsMapper postsMapper;
//    @Resource
//    private TagMapper tagMapper;
//    @Resource
//    private UserMapper userMapper;
//    @Resource
//    private PostContentMapper postContentMapper;
//    @Resource
//    private PostMediaUrlMapper postMediaUrlMapper;
//    @Resource
//    private PostCommentsMapper postCommentsMapper;
//    @Resource
//    private CategoryMapper categoryMapper;
//
//    @Value("${elasticsearch.host}")
//    private String host;
//
//    @Value("${elasticsearch.port}")
//    private String port;
//
//    public ElasticSearchService(PostsMapper postsMapper, TagMapper tagMapper, UserMapper userMapper, PostContentMapper postContentMapper, PostMediaUrlMapper postMediaUrlMapper, PostCommentsMapper postCommentsMapper) {
//        this.postsMapper = postsMapper;
//        this.tagMapper = tagMapper;
//        this.userMapper = userMapper;
//        this.postContentMapper = postContentMapper;
//        this.postMediaUrlMapper = postMediaUrlMapper;
//        this.postCommentsMapper = postCommentsMapper;
//    }
//
//    /**
//     * 创建索引库
//     * @param indexName
//     * @param indexStructure
//     * @return
//     */
//    @SneakyThrows
//    public boolean createIndex(String indexName,String indexStructure){
//        init();
//        try {
//            //1、准备request对象
//            CreateIndexRequest request = new CreateIndexRequest(indexName);
//            //2、准备请求参数
//            request.source(indexStructure, XContentType.JSON);
//            //3、发送请求
//            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
//            System.out.println(createIndexResponse);
//            return true;
//
//        }catch (Exception e){
//            log.error(String.valueOf(e));
//            return false;
//        }finally {
//            stop();
//        }
//    }
//
//    /**
//     * 检查索引库是否存在
//     * @param indexName
//     * @return
//     */
//    @SneakyThrows
//    public boolean checkIndexExists(String indexName){
//        init();
//        try {
//            GetIndexRequest request = new GetIndexRequest(indexName);
//            boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
//
//            return exists;
//        }catch (Exception e){
//            log.error(String.valueOf(e));
//            return false;
//        }finally {
//            stop();
//        }
//    }
//
//    /**
//     * 删除索引库
//     * @param indexName
//     * @return
//     */
//    @SneakyThrows
//    public boolean deleteIndex(String indexName){
//        init();
//        try {
//            //1、准备request对象
//            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
//            //2、发送请求
//            AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
//            return delete.isAcknowledged();
//        }catch (Exception e){
//            log.error(String.valueOf(e));
//            return false;
//        }finally {
//            stop();
//        }
//
//    }
//    /**
//     * 统计索引库的doc数据
//     * @param indexName
//     * @return
//     */
//    public Long countEsData(String indexName) throws IOException {
//        init();
//
//        try{
//            SearchRequest request = new SearchRequest(indexName);
//
//            request.source().query(QueryBuilders.matchAllQuery()).size(0);
//
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//
//            SearchHits hits = response.getHits();
//            return hits.getTotalHits().value;
//
//        }catch (IOException e){
//            log.error(String.valueOf(e));
//        }finally {
//            stop();
//        }
//        return 0L;
//    }
//    @SneakyThrows
//    public void postsBulkDoc(String indexName) {
//        init();
//        try {
//            int page = 1, pageSize = 20;
//            while (true) {
//                Page<Posts> postsPage = postsMapper.selectPage(new Page<>(page, pageSize),
//                        new LambdaQueryWrapper<Posts>().eq(Posts::getIsPublish, "0"));
//                // 非空校验
//                List<Posts> posts = postsPage.getRecords();
//                if (posts == null || posts.isEmpty()) {
//                    return;
//                }
//                List<PostDoc> postDocs = posts.stream().map(this::postToPostDoc).toList();
//                //1、准备request
//                BulkRequest request = new BulkRequest();
//                // 2.准备参数，添加多个新增的Request
//                //2、准备参数
//                for (PostDoc postDoc : postDocs) {
//                    request.add(new IndexRequest(indexName)
//                            .id(postDoc.getId())
//                            .source(JSON.toJSONString(postDoc), XContentType.JSON));
//                }
//                //3、发送请求
//                BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
//                page++;
//            }
//        }catch (Exception e){
//            log.error(e.toString());
//        }finally {
//            stop();
//        }
//    }
//    /**
//     * 插入文档,单条
//     * @param postId
//     */
//    @SneakyThrows
//    public void createPostDoc(String postId,String indexName) {
//        init();
//        try {
//            Posts posts = postsMapper.selectOne(new LambdaQueryWrapper<Posts>().eq(Posts::getIsPublish, "0").eq(Posts::getId, postId));
//            PostDoc postDoc = postToPostDoc(posts);
//            //1、准备request
//            IndexRequest request = new IndexRequest(indexName).id(postDoc.getId());
//            //2、准备参数
//            request.source(JSON.toJSONString(postDoc), XContentType.JSON);
//            //3、发送请求
//            IndexResponse index = client.index(request, RequestOptions.DEFAULT);
//            RestStatus status = index.status();
//
//        }catch (Exception e){
//            log.error(e.toString());
//        }finally {
//            stop();
//        }
//    }
//
//    /**
//     * 删除指定id的文档
//     * @param postId
//     * @param indexName
//     */
//    @SneakyThrows
//    public void deletePostDoc(String postId,String indexName) {
//        init();
//        try {
//            // 1.准备Request，两个参数，第一个是索引库名，第二个是文档id
//            DeleteRequest request = new DeleteRequest(indexName, postId);
//            // 2.发送请求
//            client.delete(request, RequestOptions.DEFAULT);
//        }catch (Exception e){
//            log.error(e.toString());
//        }finally {
//            stop();
//        }
//    }
//    private PostDoc postToPostDoc(Posts post) {
//        PostDoc postDoc = new PostDoc();
//        BeanUtils.copyProperties(post, postDoc);
//        //post
//        postDoc.setId(String.valueOf(post.getId()));
//        postDoc.setCreatedTime(post.getCreateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
//        postDoc.setUpdateTime(post.getUpdateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
//        //authorName
//        String authorName= Optional.ofNullable(userMapper.selectById(post.getAuthorId()))
//                .map(User::getUserName)
//                .orElse("未知作者");
//        postDoc.setAuthorName(authorName);
//        postDoc.setAuthorId(post.getAuthorId());
//        //content
//        PostContent postContent = postContentMapper.selectById(post.getContentId());
//        if (postContent != null) {
//            postDoc.setContent(postContent.getContent());
//        } else {
//            postDoc.setContent("无内容");
//        }
//
//        // tags
//        List<Long> tagsId = tagMapper.queryTagListByPostId(post.getId()).stream().map(Tag::getId).toList();
//        if (!tagsId.isEmpty()) {
//            postDoc.setTagsId(tagsId);
//        } else {
//            postDoc.setTagsId(new ArrayList<>());
//        }
//
//        //coverUrl
//        PostMediaUrl postMediaUrl = postMediaUrlMapper.selectOne(new LambdaQueryWrapper<PostMediaUrl>().eq(PostMediaUrl::getPostId, post.getId())
//                .eq(PostMediaUrl::getMediaType, LinkType.COVER.getValue()).last("limit 1"));
//        if (postMediaUrl != null && postMediaUrl.getMediaUrl() != null) {
//            postDoc.setCoverUrl(postMediaUrl.getMediaUrl());
//        } else {
//            postDoc.setCoverUrl("https://www.nradiowifi.com/d/file/2024/9a2c7e7a80515b0a8e5d02da37ace98c.jpg");
//        }
//        //comment
//        PostComments postComments = postCommentsMapper.selectOne(new LambdaQueryWrapper<PostComments>()
//                .eq(PostComments::getPostId, post.getId()).orderByDesc(PostComments::getCommentTime).last("limit 1"));
//        if (postComments != null) {
//            postDoc.setLastCommentTime(postComments.getCommentTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
//        }else{
//            postDoc.setLastCommentTime(0L);
//        }
//
//        return postDoc;
//    }
//
//    /**
//     * cni_post索引库的结构模板
//     */
//    public final static String CNI_POSTS_MAPPING =
//            """
//                    {
//                     "mappings": {
//                       "properties": {
//                         "id":{
//                           "type": "keyword"
//                         },
//                         "title":{
//                           "type": "text",
//                           "analyzer": "ik_max_word"
//                         },
//                         "summary":{
//                           "type": "text",
//                           "analyzer": "ik_smart"
//                         },
//                         "content":{
//                           "type": "text",
//                           "analyzer": "ik_smart"
//                         },
//                         "authorId":{
//                           "type": "long",
//                           "index": false
//                         },
//                         "authorName":{
//                           "type": "text",
//                           "analyzer": "ik_max_word"
//                         },
//                         "categoryId":{
//                           "type": "keyword"
//                         },
//                         "tagsId":{
//                           "type": "long"
//                         },
//                         "type":{
//                           "type": "integer"
//                         },
//                         "isTop":{
//                           "type": "integer"
//                         },
//                          "viewCounts":{
//                           "type": "integer"
//                          },
//                           "commentCounts":{
//                           "type": "integer"
//                          },
//                          "coverUrl":{
//                           "type": "keyword",
//                           "index": false
//                          },
//                         "createTime":{
//                           "type": "date",
//                           "format": "epoch_millis"
//                         },
//                         "updateTime":{
//                           "type": "date",
//                           "format": "epoch_millis"
//                         },
//                         "lastCommentTime":{
//                           "type": "date",
//                           "format": "epoch_millis"
//                         }
//                       }
//                     }
//                    }
//            """;
//
//    /**
//     * 初始化方法
//     */
//    private void init(){
//        client =  new RestHighLevelClient(
//                RestClient.builder(HttpHost.create(host+":"+port)));
//    }
//    private void stop() throws IOException {
//        if(client !=null){
//            client.close();
//        }
//    }
//
//    /**
//     * 全文查询帖子
//     * @param postSearchParam
//     * @return
//     */
//    @SneakyThrows
//    public PageResult searchPostPage(PostSearchParam postSearchParam) {
//        init();
//        String CNI_POST_INDEX = "cni-post";
//        try{
//            SearchRequest request = new SearchRequest(CNI_POST_INDEX);
//            QueryBuilder QB;
//            BoolQueryBuilder bool = QueryBuilders.boolQuery();
//            if (!postSearchParam.getKeyword().isEmpty()){
//                QB = QueryBuilders.functionScoreQuery(
//                        QueryBuilders.multiMatchQuery(postSearchParam.getKeyword(), "title", "summary", "content")
//                                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS),
//                                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
//                                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//                                                        QueryBuilders.matchQuery("title", postSearchParam.getKeyword()),
//                                                        ScoreFunctionBuilders.weightFactorFunction(10.0f) // 权重 5
//                                                ),
//                                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//                                                        QueryBuilders.matchQuery("summary", postSearchParam.getKeyword()),
//                                                        ScoreFunctionBuilders.weightFactorFunction(4.0f) // 权重 4
//                                                ),
//                                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//                                                        QueryBuilders.matchQuery("content", postSearchParam.getKeyword()),
//                                                        ScoreFunctionBuilders.weightFactorFunction(2.0f) // 权重 2
//                                                )
//                                        }
//                ).boostMode(CombineFunction.MULTIPLY);
//            }else {
//                QB = QueryBuilders.matchAllQuery();
//            }
//            //算分函数查询1放在bool的must中
//            bool.must(QB);
//            if (postSearchParam.getAuthorName()!=null){
//                bool.must(QueryBuilders.matchQuery("authorName",postSearchParam.getAuthorName()));
//            }
//
//
//            FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders
//                .functionScoreQuery(new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
//                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("isTop", 0),
//                        ScoreFunctionBuilders.weightFactorFunction(10f))})
//                .boostMode(CombineFunction.MULTIPLY);
//            //算分函数查询2放在bool的should中
//            bool.should(functionScoreQuery);
//
//            //条件过滤
//            if (postSearchParam.getCategoryId()!=null){
//                bool.filter(QueryBuilders.termQuery("categoryId",postSearchParam.getCategoryId()));
//            }
//            if (!postSearchParam.getTagList().isEmpty()){
//                bool.filter(QueryBuilders.termsQuery("tagsId",postSearchParam.getKeyword()));
//            }
//            if(!postSearchParam.getBeforeTime().isEmpty()){
//                long beforeTime = LocalDateTime.parse(postSearchParam.getBeforeTime(), DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
//                bool.filter(QueryBuilders.rangeQuery("createTime").lte(beforeTime));
//            }
//            if(!postSearchParam.getAfterTime().isEmpty()){
//                long afterTime = LocalDateTime.parse(postSearchParam.getAfterTime(), DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
//                bool.filter(QueryBuilders.rangeQuery("createTime").gte(afterTime));
//            }
//            request.source().query(bool);
//            //分页
//            request.source().from((postSearchParam.getPageNo() - 1 ) * postSearchParam.getPageSize()).size(postSearchParam.getPageSize());
//            //排序
//            if(postSearchParam.getIsAsc()){
//                request.source().sort(postSearchParam.getSortBy(),postSearchParam.getIsAsc()? SortOrder.ASC:SortOrder.DESC);
//            }
//            // 高亮设置
//            HighlightBuilder highlightBuilder = new HighlightBuilder();
//            highlightBuilder.field(new HighlightBuilder.Field("title").preTags("<em  class=\"bg-yellow-300 p-1 rounded\">").postTags("</em>"));
//            highlightBuilder.field(new HighlightBuilder.Field("summary").preTags("<em  class=\"bg-yellow-300 p-1 rounded\">").postTags("</em>"));
//            highlightBuilder.field(new HighlightBuilder.Field("content").preTags("<em  class=\"bg-yellow-300   p-1 rounded\">").postTags("</em>"));
//            highlightBuilder.field(new HighlightBuilder.Field("authorName"));
//
//            request.source().highlighter(highlightBuilder);
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            return processTheResponse(response);
//        }catch (Exception e){
//            log.error(e.toString());
//        }finally {
//            stop();
//        }
//        return new PageResult(0,null);
//    }
//
//    private PageResult processTheResponse(SearchResponse response) {
//        SearchHits hits = response.getHits();
//        //total
//        TotalHits totalHits = hits.getTotalHits();
//        long total = hits.getTotalHits().value;
//        List<PostDoc> postDocs = new ArrayList<>();
//        for (SearchHit hit : hits) {
//            String jsonString = hit.getSourceAsString();
//            PostDoc postDoc = JSON.parseObject(jsonString, PostDoc.class);
//            // 处理高亮
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            if (highlightFields != null && !highlightFields.isEmpty()) {
//                HighlightField titleField = highlightFields.get("title");
//                if (titleField != null) {
//                    postDoc.setTitle(titleField.getFragments()[0].string());
//                }
//                HighlightField summaryField = highlightFields.get("summary");
//                if (summaryField != null) {
//                    postDoc.setSummary(summaryField.getFragments()[0].string());
//                }
//                HighlightField contentField = highlightFields.get("content");
//                if (contentField != null) {
//                    postDoc.setContent(contentField.getFragments()[0].string());
//                }
//                HighlightField authorNameField = highlightFields.get("authorName");
//                if (authorNameField != null) {
//                    postDoc.setAuthorName(authorNameField.getFragments()[0].string());
//                }
//            }
//            postDocs.add(postDoc);
//        }
//
//        //postDoc to postViewVo
//        List<PostViewVo> postViewVoList = new ArrayList<>();
//        for (PostDoc postDoc : postDocs) {
//            PostViewVo postViewVo = new PostViewVo();
//            BeanUtils.copyProperties(postDoc,postViewVo);
//            postViewVo.setPostId(postDoc.getId());
//
//            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, postDoc.getAuthorId()));
//            UserVo userVo = new UserVo();
//            userVo.setUserId(String.valueOf(user.getUserId()));
//            userVo.setUserName(user.getUserName());
//            userVo.setAvatar(user.getAvatar());
//            userVo.setBio(user.getBio());
//            postViewVo.setUserVo(userVo);
//
//            LocalDateTime createTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(postDoc.getCreatedTime()), ZoneId.of("UTC"));
//            postViewVo.setCreateTime(createTime);
//            LocalDateTime updateTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(postDoc.getUpdateTime()), ZoneId.of("UTC"));
//            postViewVo.setUpdateTime(updateTime);
//            LocalDateTime commentTime =  LocalDateTime.ofInstant(Instant.ofEpochMilli(postDoc.getLastCommentTime()), ZoneId.of("UTC"));
//            postViewVo.setCommentTime(commentTime);
//
//            postViewVo.setIsTop(String.valueOf(postDoc.getIsTop()));
//            postViewVo.setType(String.valueOf(postDoc.getType()));
//            //todo categoryVo tagVos 优化
//            if (!postDoc.getCoverUrl().isEmpty()){
//                PostMediaUrlVo postMediaUrlVo = new PostMediaUrlVo();
//                postMediaUrlVo.setMediaUrl(postDoc.getCoverUrl());
//                postMediaUrlVo.setMediaType("cover");
//                List<PostMediaUrlVo> postMediaUrlVos = new ArrayList<>();
//                postViewVo.setUrls(postMediaUrlVos);
//            }
//            postViewVoList.add(postViewVo);
//        }
//        return new PageResult(total,postViewVoList);
//    }
//}
