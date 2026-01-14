FROM docker.elastic.co/elasticsearch/elasticsearch:8.16.1

# 安装 ik 分词器插件
USER root
RUN bin/elasticsearch-plugin install --batch https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-8.16.1.zip

# 切换回 elasticsearch 用户
USER elasticsearch
