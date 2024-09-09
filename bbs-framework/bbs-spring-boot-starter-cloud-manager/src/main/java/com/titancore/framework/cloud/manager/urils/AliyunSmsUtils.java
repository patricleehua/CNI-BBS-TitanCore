package com.titancore.framework.cloud.manager.urils;

import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class AliyunSmsUtils {
	private String regionId;
	private String accessKeyId;
	private String secret;
	private String templateCode;
	private String signName;
	/**
	 * 发送短信
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public String sendMessage(String phoneNumbers,String param){

		// 初始化acsClient,暂不支持region化
		DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId(regionId);
		// 必填:待发送手机号
		request.setPhoneNumbers(phoneNumbers);
//      在阿里云设置的签名
		request.setSignName(signName);
//	    在阿里云设置的模板
		request.setTemplateCode(templateCode);
//	    在设置模板的时候有一个占位符
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println(response.getMessage().toString());
			return response.getMessage().toString();
		}catch (ClientException | com.aliyuncs.exceptions.ClientException e) {
			e.printStackTrace();
		}
		return null;
	}
}
