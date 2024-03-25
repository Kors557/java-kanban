public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Task test1 = taskManager.createTask("TEst1", "testtsts", Status.NEW);
        Task test2 = taskManager.createTask("TEst2", "testtsts", Status.IN_PROGRESS);
        Task test3 = taskManager.createTask("TEst3", "testtsts", Status.DONE);

        System.out.println(test1);
        System.out.println(taskManager.getAllTask());
    }
}
