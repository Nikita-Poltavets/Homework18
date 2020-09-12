package com.homework.nix;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;


public class UpdateFileByThread {

    private BlockingQueue<String> drop;

    private final String DONE = "done";

    private final AtomicReference<Object> snapshot;

    public UpdateFileByThread(Object initialState)
    {
        snapshot = new AtomicReference<>(initialState);
        drop = new ArrayBlockingQueue<>(1, true);
        (new Thread(new MyProducer())).start();
        (new Thread(new MyConsumer())).start();
    }

    class MyProducer implements Runnable
    {
        @Override
        public void run() {
            try {

                Scanner in = new Scanner(System.in);

                String input;
                do{
                    System.out.print("Enter something: ");
                    input = in.nextLine();

                    if(input.equals("quit")){ continue; }

                    drop.put(input);

                    sleepOneSecond();
                }while (!input.equals("quit"));

                drop.put(DONE);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    class MyConsumer implements Runnable
    {
        @Override
        public void run() {
            sleepOneSecond();

            try {
                Object newMessage;

                while (!((newMessage = drop.take()).equals(DONE))) {

                    Object oldMessage = snapshot.get();

                    if (!newMessage.equals(oldMessage)){

                        snapshot.set(newMessage);

                        writeToFile(newMessage.toString());

                        System.out.println("File got this message - " + newMessage.toString());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void writeToFile(String message){
        try(FileWriter writer = new FileWriter("output.txt", false))
        {
            writer.write(message);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void sleepOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
