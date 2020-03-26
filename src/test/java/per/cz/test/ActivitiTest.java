package per.cz.test;

import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import per.cz.util.SecurityUtil;

/**
 *
 * SpringBoot 与 Junit整合 ，测试流程定义的相关操作
 *
 *  7的新特性中，会当我们把流程文件放在resources/processes目录下，会自动部署
 *
 * Created by Administrator on 2020/3/26.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiTest {

    //实现流程定义相关操作
    @Autowired
    private ProcessRuntime processRuntime;
    //任务相关操作
    @Autowired
    private TaskRuntime taskRuntime;
    //SpringSecurity工具类
    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 流程定义信息的查看
     * 注意：流程部署工作在activiti7与SpringBoot整合后，会自动部署
     */
    @Test
    public void testDefinition(){
        //认证
        securityUtil.logInAs("salaboy");
        //分页查询出流程定义信息
        Page<ProcessDefinition> definitionPage = processRuntime
                .processDefinitions(Pageable.of(0, 10));

        log.info("查看已部署的流程信息个数 = {}",definitionPage.getTotalItems());
        //definitionPage.getContent() 得到当前部署的每一个流程定义信息
        for (ProcessDefinition pd: definitionPage.getContent()) {
            log.info("流程定义id = {},name ={}",pd.getId(),pd.getName());
            log.info("流程定义key = {},version ={}",pd.getKey(),pd.getVersion());
        }

    }

    /**
     * 启动流程实例
     *
     */
    @Test
    public void testStartProcess(){
        String userName = "zhangsan";
        securityUtil.logInAs("salaboy");
        ProcessInstance myGroup = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("myGroup")
//                .withVariable("username",userName) 流程定义变量
                .build());

        log.info("流程实例ID ={}",myGroup.getId());
    }

    /**
     * 查询，并完成任务
     */
    @Test
    public void testTask(){
        securityUtil.logInAs("ryandawsonuk");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        if (tasks.getTotalItems() > 0){
            for (Task task: tasks.getContent()) {
                //拾取任务
//                taskRuntime.claim(TaskPayloadBuilder
//                .claim()
//                .withTaskId(task.getId()).build());
                log.info("拾取任务={}",task);

            }
        }
    }


}
