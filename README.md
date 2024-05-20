package com.example.aspect;

import com.example.model.Workflow;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class WorkflowAspect {

    @Autowired
    private HttpServletRequest request;

    @AfterReturning(pointcut = "execution(* com.example.repository.WorkflowRepository.save(..))", returning = "workflow")
    public void afterSaveWorkflow(Workflow workflow) {
        if (workflow != null) {
            request.setAttribute("transactionId", workflow.getTransactionId());
            request.setAttribute("makerId", workflow.getMakerId());
            request.setAttribute("makerTimeStamp", workflow.getMakerTimeStamp());
        }
    }
}

@EnableAspectJAutoProxy

            // Extract updated request attributes if any
            String transactionId = (String) wrappedRequest.getAttribute("transactionId");
            String makerId = (String) wrappedRequest.getAttribute("makerId");
            String makerTimeStamp = (String) wrappedRequest.getAttribute("makerTimeStamp");
