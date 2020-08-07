package org.jeecg;

import java.net.Socket;

import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

public class MailTest {

  public void getAttrs(String userAtDomain) {
    IClient client = null;
    try {
      Socket socket = new Socket("113.55.14.173", 6195);
      client = APIContext.getClient(socket);
      APIContext ret = client.getAttrs(userAtDomain,
          "domain_name=&cos_id=&user_status=&user_expiry_date=&org_unit_id&true_name=&nick_name=&mobile_number=&home_phone=&company_phone=&fax_number=&gender=&province=&city=&birthday=&address=&zipcode=&homepage=");
      if (ret.getRetCode() == APIContext.RC_NORMAL) {
        System.out.println("User attrs: " + ret.getResult());
      } else {
        System.out.println("User attrs failed, code=" + ret.getRetCode() + ", msg=" + ret.getErrorInfo());
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (client != null) {
        client.close();
      }
    }

  }

  public static void main(String[] args) {
    MailTest mailTest = new MailTest();
    mailTest.getAttrs("20160019@ynu.edu.cn");

  }
}