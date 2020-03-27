package per.cz.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cz.entity.Holiday;

import java.util.HashMap;
import java.util.List;

/**
 * 流程业务
 * @author Administrator
 * @date 2020/3/27
 */
@Slf4j
@RestController
public class ActivitiController {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 启动流程
     * @return
     */
    @GetMapping("startProcess")
    public String startProcess(String key){

        HashMap<String, Object> map = new HashMap<>();
        Holiday holiday = new Holiday();
        holiday.setUserName("joker");
        holiday.setNum(5F);
        map.put("holiday",holiday);

        ProcessInstance myGroup = runtimeService.startProcessInstanceByKey(key,map);

        //4.输出实例相关信息
        log.info("流程部署Id={},流程实例id = {}"
                ,myGroup.getDeploymentId()
                ,myGroup.getId()
        );

        log.info("活动id ={}",myGroup.getActivityId());
        return "start process success";
    }


    /**
     * 查询任务
     * @return
     */
    @RequestMapping("getTask")
    public String getTask(String taskId,String name){
        List<Task> list = taskService.createTaskQuery().processDefinitionId(taskId)
                .taskAssignee(name)
                .list();
        if (list.size() > 0 ){
            for (Task t : list) {
                log.info("task id ={}, 流程实例id ={}",t.getId(),t.getProcessInstanceId());
                log.info("task name ={}",t.getName());
            }
        }
        return "hello world";
    }

    /**
     * 查询任务
     * @return
     */
    @RequestMapping("completeTask")
    public String completeTask(String taskId){
       taskService.complete(taskId);
        log.info("task ={}",taskId);
        return "hello world";
    }

    /**
     * 查询历史任务
     */
    @RequestMapping("getHistoryTask")
    public String getHistoryTask(String taskId){
        List<HistoricActivityInstance> list = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(taskId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        for (HistoricActivityInstance hai:list) {
            log.info("流程活动id{},活动名称{}",hai.getActivityId(),hai.getActivityName());
            log.info("流程定义id{},流程实例id{}",hai.getProcessDefinitionId(),hai.getProcessInstanceId());
        }
        return "hello world";
    }


    /**
     * 获取所有部署流程信息
     */
    @RequestMapping("getProcessDeploy")
    public String showProcess(Model model, String page, String limit){

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> list = processDefinitionQuery.orderByProcessDefinitionVersion().asc().list();

        for (ProcessDefinition pd :list) {
            log.info("id ={} ,key ={}",pd.getId(),pd.getKey());
            log.info("deploy = {} ",pd.getDeploymentId());
        }
        return "getProcessDeploy";
    }



}
