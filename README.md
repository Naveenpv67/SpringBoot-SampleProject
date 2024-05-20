@EnableAspectJAutoProxy 

   @AfterReturning(pointcut = "execution(* com.example.repository.WorkflowRepository.saveAll(..)) && args(workflows)", returning = "workflows")
    public void afterSaveAllWorkflows(List<Workflow> workflows) {
        if (workflows != null && !workflows.isEmpty()) {
            // Extract checker ID and checker timestamp from the first workflow object in the list
            Workflow firstWorkflow = workflows.get(0);
            String checkerId = firstWorkflow.getCheckerId();
            String checkerTimeStamp = firstWorkflow.getCheckerTimeStamp();

            request.setAttribute("checkerId", checkerId);
            request.setAttribute("checkerTimeStamp", checkerTimeStamp);
        }
    }



            // Extract updated request attributes if any
            String transactionId = (String) wrappedRequest.getAttribute("transactionId");
