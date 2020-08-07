package org.jeecg.modules.mail_user.controller;

import java.io.Serializable;
import java.util.List;
import org.jeecg.modules.mail_user.entity.MailUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMailResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  
  List<MailUser> successUsers;
  List<MailUser> failedUsers;
}
