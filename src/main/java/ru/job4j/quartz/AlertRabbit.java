package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class AlertRabbit {

    public static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream input = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return properties;
    }

    public static void main(String[] args) {
        Properties properties = getProperties();
        String interval = properties.getProperty("rabbit.interval");
        try (Connection con = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"))) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", con);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(interval))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();

        } catch (SchedulerException | SQLException | InterruptedException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context)  {
            System.out.println("Rabbit runs here...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement =
                        connection.prepareStatement("INSERT INTO rabbit(created_date) VALUES (?)")) {
                    statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    statement.executeUpdate();
                } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}