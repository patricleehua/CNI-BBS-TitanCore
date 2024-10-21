package com.titancore.service;

import com.alibaba.fastjson.JSON;
import com.titancore.domain.entity.ChatGroupMember;
import com.titancore.enums.WebSocketContentType;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    @Data
    public static class WsContent {
        private String type;
        private Object content;
    }

    @Resource
    private ChatGroupMemberService chatGroupMemberService;

    @Lazy
    @Resource
    private UserService userService;

    public static final ConcurrentHashMap<String, Channel> Online_User = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Channel, String> Online_Channel = new ConcurrentHashMap<>();

    public void online(Channel channel, String token) {
        try {
            String userId = userService.onlineByToken(token);
//            String loginUserId = StpUtil.getLoginId().toString();
            Online_User.put(userId, channel);
            Online_Channel.put(channel, userId);
        } catch (Exception e) {
            sendMsg(channel, "连接错误", WebSocketContentType.Msg);
            channel.close();
        }
    }

    public void offline(Channel channel) {
        String userId = Online_Channel.get(channel);
        if (StringUtils.isNotBlank(userId)) {
            Online_User.remove(userId);
            Online_Channel.remove(channel);
//            userService.offline(userId);
        }
    }

    private void sendMsg(Channel channel, Object msg, String type) {
        WsContent wsContent = new WsContent();
        wsContent.setType(type);
        wsContent.setContent(msg);
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(wsContent)));
    }

    public void sendMsgToUser(Object msg, String userId) {
        Channel channel = Online_User.get(userId);
        if (channel != null) {
            sendMsg(channel, msg, WebSocketContentType.Msg);
        }
    }

//    public void sendMsgToGroup(Message message, String groupId) {
//        List<ChatGroupMember> list = chatGroupMemberService.getGroupMember(groupId);
//        for (ChatGroupMember member : list) {
//            if (!message.getFromId().equals(member.getUserId()) || MsgType.System.equals(message.getType())) {
//                sendMsgToUser(message, member.getUserId());
//            }
//        }
//    }
//
//    public void sendMsgAll(Object msg) {
//        Online_Channel.forEach((channel, ext) -> {
//            sendMsg(channel, msg, WsContentType.Msg);
//        });
//    }
//
//    public void sendNotifyToUser(Object msg, String userId) {
//        Channel channel = Online_User.get(userId);
//        if (channel != null) {
//            sendMsg(channel, msg, WsContentType.Notify);
//        }
//    }

//    public void sendNoticeToGroup(Message message, String groupId) {
//        List<ChatGroupMember> list = chatGroupMemberService.getGroupMember(groupId);
//        for (ChatGroupMember member : list) {
//            if (!message.getFromId().equals(member.getUserId()) || MsgType.System.equals(message.getType())) {
//                sendNotifyToUser(message, member.getUserId());
//            }
//        }
//    }
//
//    public void sendVideoToUser(Object msg, String userId) {
//        Channel channel = Online_User.get(userId);
//        if (channel != null) {
//            sendMsg(channel, msg, WsContentType.Video);
//        }
//    }
//
//    public void sendNotifyAll(Object msg) {
//        Online_Channel.forEach((channel, ext) -> {
//            sendMsg(channel, msg, WsContentType.Notify);
//        });
//    }

}
