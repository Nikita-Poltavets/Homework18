package com.homework.nix;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdateFileByThread {
    private BlockingQueue<String> drop;

    private final String DONE = "done";
    public UpdateFileByThread()
    {
        drop = new ArrayBlockingQueue<>(1, true);
        (new Thread(new Producer())).start();
        (new Thread(new Consumer())).start();
    }

    class Producer implements Runnable
    {
        @Override
        public void run() {
            try {
                int cnt = 0;
                Scanner in = new Scanner(System.in);

                String input;
                do{
                    System.out.print("Enter something: ");
                    input = in.nextLine();
                    drop.put(input);

                    if (++cnt < 3)
                        Thread.sleep(2000);

                }while (!input.equals("quit"));

                drop.put(DONE);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    class Consumer implements Runnable
    {
        List<String> messagesList = new CopyOnWriteArrayList<>();

        @Override
        public void run() {
            try {
                sleepOneSecond();

                String msg = null;

                while (!((msg = drop.take()).equals(DONE))){
                    if(!messagesList.contains(msg) && !msg.equals("quit")){
                        messagesList.add(msg);

                        FileUtils.writeLines(new File("output.txt"), messagesList);

                        System.out.println("You entered - " + msg);
                    }
                }

            } catch (InterruptedException | IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void sleepOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
