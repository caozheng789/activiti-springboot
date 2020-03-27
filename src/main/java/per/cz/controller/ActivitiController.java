package per.cz.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import per.cz.entity.Holiday;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * <p>启动请假流程</p>
     * @return String 流程实例ID
     * @author FRH
     * @time 2018年12月10日上午11:03:36
     * @version 1.0
     */
    @RequestMapping(value="/start")
    @ResponseBody
    public String start() {
        // xml中定义的ID
        String instanceKey = "myHoliday6";
        log.info("开启请假流程...");

        // 设置流程参数，开启流程
        Map<String,Object> map = new HashMap<>();
        Holiday holiday = new Holiday();
        holiday.setUserName("joker");
        holiday.setNum(5F);
        map.put("holiday",holiday);

        //使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceKey, map);

        log.info("启动流程实例成功:{}", instance);
        log.info("流程实例ID:{}", instance.getId());
        log.info("流程定义ID:{}", instance.getProcessDefinitionId());


        //验证是否启动成功
        //通过查询正在运行的流程实例来判断
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        //根据流程实例ID来查询
        List<ProcessInstance> runningList = processInstanceQuery.processInstanceId(instance.getProcessInstanceId()).list();
        log.info("根据流程ID查询条数:{}", runningList.size());

        // 返回流程ID
        return instance.getId();
    }



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
