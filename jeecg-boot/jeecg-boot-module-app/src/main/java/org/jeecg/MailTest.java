package org.jeecg;

import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.alibaba.fastjson.JSON;
import org.jeecg.modules.core_mail_user.entity.CoreMailUser;
import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

public class MailTest {

  public static void main(String[] args) {
    MailTest mailTest = new MailTest();
    mailTest.getAttrs("20160019@ynu.edu.cn");
    // mailTest.getUserListByOrgId("a");

  }

  public void getAttrs(String userAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket("113.55.14.173", 6195);
      client = APIContext.getClient(socket);


      String[] attrNames = new String[] {"cos_id", "user_list_rank", "user_status", "domain_name",
          "quota_delta", "nf_quota_delta", "user_expiry_date", "org_unit_id", "password",
          "forwarddes", "forwardactive", "keeplocal", "time_zone", "afterdel", "alt_email",
          "pwd_hint_question", "pwd_hint_answer", "true_name", "nick_name", "mobile_number",
          "home_phone", "company_phone", "fax_number", "gender", "province", "city", "birthday",
          "address", "zipcode", "homepage", "country", "anniversary", "reg_ip", "duty", "remarks",
          "org_unit_fullName", "login_ip_range"};
      String[] attrValues =
          IntStream.range(0, attrNames.length).mapToObj(i -> "").toArray(x -> new String[x]);
      String attrs = Utils.encode(attrNames, attrValues);

      APIContext ret = client.getAttrs(userAtDomain, attrs);
      if (ret.getRetCode() == APIContext.RC_NORMAL) {
        System.out.println("User attrs: " + ret.getResult());
        Map<String, String> userAttrs = Utils.decode(ret.getResult());
        Map<String, String> newUserAttrs =
            userAttrs.entrySet().stream().filter(item -> item.getValue().equals(""))
                .collect(Collectors.toMap(item -> item.getKey(), item -> item.getValue()));
        CoreMailUser coreMailUser =
            JSON.parseObject(JSON.toJSONString(userAttrs), CoreMailUser.class);
        System.out.println(coreMailUser);
      } else {
        System.out
            .println("User attrs failed, code=" + ret.getRetCode() + ", msg=" + ret.getErrorInfo());
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }

  }

  public void getUserListByOrgId(String orgId) {
    IClient client = null;
    try {
      Socket socket = new Socket("113.55.14.173", 6195);
      client = APIContext.getClient(socket);
      APIContext ret = client.getOrgCosUser(orgId, 1);
      if (ret.getRetCode() == APIContext.RC_NORMAL) {
        System.out.println("User list: " + ret.getResult());
        System.out.println("User list: " + ret.getResult().split(","));
      } else {
        System.out
            .println("User list failed, code=" + ret.getRetCode() + ", msg=" + ret.getErrorInfo());
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }

  }
}
