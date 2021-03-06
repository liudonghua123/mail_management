package org.jeecg;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;
import com.alibaba.fastjson.JSON;
import org.jeecg.modules.core_mail_user.entity.CoreMailUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

@Component
@Slf4j
public class CoremailUtils {

  @Value("${coremail.endpointHost}")
  String endpointHost;
  @Value("${coremail.endpointPort}")
  int endpointPort;


  public Set<CoreMailUser> convertToCoreMailUser(Map<String, Map<String, String>> allUserAttrs) {
    Set<CoreMailUser> coreMailUserList = new HashSet<>();
    for (Entry<String, Map<String, String>> userAttrsMap : allUserAttrs.entrySet()) {
      String userAtDomain = userAttrsMap.getKey();
      Map<String, String> userAttrs = userAttrsMap.getValue();
      // String id = userAtDomain.substring(0, userAtDomain.indexOf("@"));
      String domain = userAtDomain.substring(userAtDomain.indexOf("@") + 1);
      CoreMailUser coreMailUser =
          JSON.parseObject(JSON.toJSONString(userAttrs), CoreMailUser.class);
      coreMailUser.setId(userAtDomain);
      // 更新 domain 信息
      coreMailUser.setDomainName(domain);
      coreMailUserList.add(coreMailUser);
    }
    return coreMailUserList;
  }

  public String[] getUserListByOrgId(String orgId) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.getOrgCosUser(orgId, 1);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("获取用户列表信息 {} 失败，code: {}, msg: {}", orgId, ret.getRetCode(), ret.getErrorInfo());
        return null;
      }
      String result = ret.getResult();
      return result.equals("") ? new String[] {} : result.split(",");
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public Map<String, String[]> getSmtpAlias(List<String> userAtDomains) {
    Map<String, String[]> results = new HashMap<>();
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      for (String userAtDomain : userAtDomains) {
        APIContext ret = client.getSmtpAlias(userAtDomain);
        if (ret.getRetCode() != APIContext.RC_NORMAL) {
          log.warn("获取用户别名信息 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
              ret.getErrorInfo());
          continue;
        }
        String result = ret.getResult();
        results.put(userAtDomain, result.equals("") ? new String[] {} : result.split(","));
      }
      return results;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return results;
  }

  public String[] getSmtpAlias(String userAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.getSmtpAlias(userAtDomain);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("获取用户别名信息 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
        return null;
      }
      String result = ret.getResult();
      return result.equals("") ? new String[] {} : result.split(",");
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext addSmtpAlias(String userAtDomain, String aliasUserAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.addSmtpAlias(userAtDomain, aliasUserAtDomain);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("添加邮箱别名 {} for {} 失败，code: {}, msg: {}", aliasUserAtDomain, userAtDomain,
            ret.getRetCode(), ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext delSmtpAlias(String userAtDomain, String aliasUserAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.delSmtpAlias(userAtDomain, aliasUserAtDomain);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("添加邮箱别名 {} for {} 失败，code: {}, msg: {}", aliasUserAtDomain, userAtDomain,
            ret.getRetCode(), ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public Map<String, Map<String, String>> getAttrs(List<String> userAtDomains) {
    Map<String, Map<String, String>> results = new HashMap<>();
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      String[] attrNames = new String[] {"cos_id", "user_list_rank", "user_status", "domain_name",
          "quota_delta", "nf_quota_delta", "user_expiry_date", "org_unit_id", "password",
          "forwarddes", "forwardactive", "keeplocal", "time_zone", "afterdel", "alt_email",
          "pwd_hint_question", "pwd_hint_answer", "true_name", "nick_name", "mobile_number",
          "home_phone", "company_phone", "fax_number", "gender", "province", "city", "birthday",
          "address", "zipcode", "homepage", "country", "anniversary", "reg_ip", "duty", "remarks",
          "org_unit_fullName", "login_ip_range", "org_id"};
      String[] attrValues =
          IntStream.range(0, attrNames.length).mapToObj(i -> "").toArray(x -> new String[x]);
      String attrs = Utils.encode(attrNames, attrValues);
      for (String userAtDomain : userAtDomains) {
        APIContext ret = client.getAttrs(userAtDomain, attrs);
        if (ret.getRetCode() != APIContext.RC_NORMAL) {
          log.warn("获取邮箱账号属性信息 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
              ret.getErrorInfo());
          continue;
        }
        // log.info("邮箱账号属性信息: {}", ret.getResult());
        results.put(userAtDomain, Utils.decode(ret.getResult()));
      }
      return results;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public Map<String, String> getAttrs(String userAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      String[] attrNames = new String[] {"cos_id", "user_list_rank", "user_status", "domain_name",
          "quota_delta", "nf_quota_delta", "user_expiry_date", "org_unit_id", "password",
          "forwarddes", "forwardactive", "keeplocal", "time_zone", "afterdel", "alt_email",
          "pwd_hint_question", "pwd_hint_answer", "true_name", "nick_name", "mobile_number",
          "home_phone", "company_phone", "fax_number", "gender", "province", "city", "birthday",
          "address", "zipcode", "homepage", "country", "anniversary", "reg_ip", "duty", "remarks",
          "org_unit_fullName", "login_ip_range", "org_id"};
      String[] attrValues =
          IntStream.range(0, attrNames.length).mapToObj(i -> "").toArray(x -> new String[x]);
      String attrs = Utils.encode(attrNames, attrValues);
      APIContext ret = client.getAttrs(userAtDomain, attrs);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("获取邮箱账号 {} 属性信息失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
        return null;
      }
      log.info("邮箱账号 {} 属性信息: ", ret.getResult());
      return Utils.decode(ret.getResult());
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext changeAttrs(String userAtDomain, String attrs) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.changeAttrs(userAtDomain, attrs);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("更新邮箱账号 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext createUser(String providerId, String orgId, String userId, String attrs) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.createUser(providerId, orgId, userId, attrs);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("创建邮箱账号 {} 失败,code: {}, msg: {}", userId, ret.getRetCode(), ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext deleteUser(String id) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.deleteUser(id);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("删除邮箱账号 {} 失败，code: {}, msg: {}", id, ret.getRetCode(), ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext userExist(String userAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.userExist(userAtDomain);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("验证邮箱账号 {} 是否存在失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  public APIContext authenticate(String userAtDomain, String password) {
    IClient client = null;
    try {
      Socket socket = new Socket(endpointHost, endpointPort);
      client = APIContext.getClient(socket);
      APIContext ret = client.authenticate(userAtDomain, password);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("验证邮箱账号密码 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }
}
