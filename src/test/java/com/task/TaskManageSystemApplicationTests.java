package com.task;

import com.task.bean.Task;
import com.task.bean.User;
import com.task.mail.MailUtils;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskManageSystemApplicationTests {
	@Autowired
	MailUtils mailUtils;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	UserRepository userRepository;
	String[] mails = {"864508274@qq.com",
			"1059802525@qq.com"};

	@Test
	public void contextLoads() {
		Task task = taskRepository.findOne(1);
		boolean b = true;
		for (String mail : mails) {
			User user = new User();
			user.setEmail(mail);
			user.setNickName("程佳");
//            mailUtils.sendTaskAddEmail(task, user);
			mailUtils.sendTaskNotifyEmail(task, user);
//            mailUtils.sendTaskVerifyEmail(task, user, b);
			b = !b;
		}
	}

}
